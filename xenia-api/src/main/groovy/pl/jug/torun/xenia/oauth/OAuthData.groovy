package pl.jug.torun.xenia.oauth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.awt.Desktop
import java.time.Duration

@Component
@Configuration
@ConfigurationProperties(prefix = "oauth")
class OAuthData {
    String requestToken
    String refreshToken
    int expiryPeriod
    long tokenTime

    String clientId
    String clientSecret

    boolean isTokenInvalid() {
        return requestToken == null || (tokenTime + Duration.ofSeconds(expiryPeriod).toMillis() < System.currentTimeMillis())
    }

    void fromResponse(response) {
        tokenTime = System.currentTimeMillis()
        refreshToken = response["refresh_token"]
        expiryPeriod = response["expires_in"] as Integer
        requestToken = response["access_token"]
    }
}
