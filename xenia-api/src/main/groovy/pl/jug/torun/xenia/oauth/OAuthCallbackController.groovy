package pl.jug.torun.xenia.oauth

import groovyx.net.http.HTTPBuilder
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping(value = "/oauth2", produces = "application/json")
class OAuthCallbackController {

    @Autowired
    OAuthData oAuthData

    @Autowired
    RestTemplate restTemplate

    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    String getToken(@RequestParam("code") String code) {
        def http = new HTTPBuilder("https://secure.meetup.com/oauth2/access")
        def bodyForTokenRequest = getBodyForTokenRequest(oAuthData.clientId, oAuthData.clientSecret, code)
        def response = http.post(body: bodyForTokenRequest)

        oAuthData.fromResponse(response)

        return "Initial access token retrieved from Meetup, you can go back to the app now...";
    }

    private static LinkedHashMap<String, String> getBodyForTokenRequest(String clientId, String clientSecret, String code) {
        return [
                client_id    : clientId,
                client_secret: clientSecret,
                grant_type   : "authorization_code",
                redirect_uri : "http://localhost:8000/app/",
                code         : code
        ]
    };

}
