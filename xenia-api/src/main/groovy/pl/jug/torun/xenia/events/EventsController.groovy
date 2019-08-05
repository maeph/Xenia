package pl.jug.torun.xenia.events

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import pl.jug.torun.xenia.meetup.InvalidTokenException

import java.nio.charset.Charset

@RestController
@RequestMapping(value = "/events", produces = "application/json")
final class EventsController {

    private final EventRepository eventRepository
    private final EventsSynchronizationService synchronizationService

    @Autowired
    EventsController(EventRepository eventRepository, EventsSynchronizationService synchronizationService) {
        this.eventRepository = eventRepository
        this.synchronizationService = synchronizationService
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Event> listAll() {
        return eventRepository.findAll()
                .sort { it.startDateTime }
                .reverse()
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refresh() {
        try {
            synchronizationService.synchronizeLocalEventsWithRemoteService()
            return new ResponseEntity<>(listAll(), HttpStatus.OK)
        } catch (InvalidTokenException e) {
            return new ResponseEntity<>(["clientId" : e.clientId], HttpStatus.UNAUTHORIZED)
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event eventDetails(@PathVariable("id") long id) {
        return eventRepository.findById(id).get()
    }
}
