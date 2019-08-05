package pl.jug.torun.xenia.meetup

import groovy.transform.ToString

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.oauth.OAuthData



@Component
final class MeetupClient {

    private final MeetupRestTemplate restTemplate

    @Autowired
    OAuthData oAuthData

    @Autowired
    MeetupClient(MeetupRestTemplate restTemplate) {
        this.restTemplate = restTemplate
    }

    List<Event> getAllEvents() throws InvalidTokenException {
        return restTemplate.exchange("/events?only=id,name,time&status=upcoming,past",
                HttpMethod.GET, getAuthHeaders(), EventsResponse).body.getResults() ?: []

    }

    List<Member> getAllEventAttendees(long id) throws InvalidTokenException {

        def url = String.format(
                "/rsvps?event_id=%d&only=%s&rsvp=%s",
                id,
                "member,member_photo,answers",
                "yes"
        )
        return restTemplate.exchange(url, HttpMethod.GET, getAuthHeaders(), MembersResponse).body.getResults() ?: []
    }

    private HttpEntity getAuthHeaders() throws InvalidTokenException {
        if (oAuthData.isTokenInvalid()) {
            throw new InvalidTokenException(oAuthData.clientId)
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + oAuthData.requestToken)
        HttpEntity entity = new HttpEntity(headers)
        entity
    }

    @ToString(includePackage = false)
    private static class EventsResponse {
        List<Event> results = []
    }


    @ToString(includePackage = false)
    private static class MembersResponse {
        List<Member> results = []
    }
}