package pl.jug.torun.xenia

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import groovyx.net.http.HTTPBuilder
import org.joda.time.DateTime
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.util.UriComponentsBuilder

import java.awt.Desktop
import java.util.function.Function


class OAuthTester {
    static private HttpServer server

    static void main(String[] args) {

        String address = "https://secure.meetup.com/oauth2/authorize" +
                "?client_id="+ args[0] +
                "&response_type=code" +
                "&redirect_uri=http://localhost:8080/oauth2/redirect";

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(address))
        } else {
            println "Browse action not supported, please navigate manually to the following address:"
            println address
        }
        getCodeForMeetup({ code ->
            try {

                def http = new HTTPBuilder("https://secure.meetup.com/oauth2/access")
                def response = http.post(body: getBodyForTokenRequest(args[0], args[1], code))
                println "accessToken = " + response["access_token"]
                SpringApplication.run Application, args
            } catch (e) {
                e.printStackTrace();
            }
        })
    }

    private static LinkedHashMap<String, String> getBodyForTokenRequest(String clientId, String clientSecret, String code) {
        Map<String, String> body = [
                client_id    : clientId,
                client_secret: clientSecret,
                grant_type   : "authorization_code",
                redirect_uri : "http://localhost:8080/oauth2/redirect",
                code         : code
        ]
        body
    };

    static String getCodeForMeetup(Function<String, Void> codeCallback) {
        server = HttpServer.create(new InetSocketAddress(8080), 0)
        server.createContext("/oauth2/redirect", new OauthCodeHandler(codeCallback));
        server.setExecutor(null);
        server.start();
    }

    static class OauthCodeHandler implements HttpHandler {

        private Function<String, Void> callback

        private OauthCodeHandler(Function<String, Void> callback) {
            this.callback = callback
        }
        @Override
        void handle(HttpExchange t) throws IOException {
            String response = "You can return to the app now...";
            String code = UriComponentsBuilder.fromUri(t.getRequestURI()).build().getQueryParams().getFirst("code")
            callback.apply(code);
            t.sendResponseHeaders(200, response.length())
            OutputStream os = t.getResponseBody()
            os.write(response.getBytes())
            os.close();
            server.stop()
        }
    }
}
