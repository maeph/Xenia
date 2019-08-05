package pl.jug.torun.xenia.events

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.meetup.InvalidTokenException
import pl.jug.torun.xenia.meetup.Member

@RestController
@RequestMapping(value = "/events/{id}/attendees", produces = "application/json")
final class AttendeesController {

    private final AttendeeRepository attendeeRepository
    private final AttendeesSynchronizationService synchronizationService

    @Autowired
    AttendeesController(AttendeeRepository attendeeRepository, AttendeesSynchronizationService synchronizationService) {
        this.attendeeRepository = attendeeRepository
        this.synchronizationService = synchronizationService
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Member> listAll(@PathVariable("id") long eventId) {
        return attendeeRepository.findAllByEventId(eventId).collect { it.member }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refresh(@PathVariable("id") Event event) {
        try {
            synchronizationService.synchronizeLocalAttendeesWithRemoteService(event)
            return new ResponseEntity<>(listAll(event.id), HttpStatus.OK)
        } catch (InvalidTokenException e) {
            return new ResponseEntity<>(["clientId": e.clientId], HttpStatus.UNAUTHORIZED)
        }
    }
}
