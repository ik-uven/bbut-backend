package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.participant.LapState;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.participant.ParticipantState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrackingController {

    private final ParticipantService participantService;

    private final ApplicationEventPublisher eventPublisher;

    public TrackingController(ParticipantService participantService, ApplicationEventPublisher eventPublisher) {
        this.participantService = participantService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping(path = "/api/participants", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> registerParticipant(@RequestBody ParticipantInput participantInput) {

        Participant participant = participantService.registerParticipant(participantInput.getFirstName(), participantInput.getLastName(), participantInput.getTeam());

        eventPublisher.publishEvent(participant);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(participant);
    }

    @PutMapping(path = "/api/participants/{id}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> registerState(@PathVariable(value = "id") long participantId, @PathVariable(value = "state") ParticipantState participantState) {

        Participant participant = participantService.setState(participantId, participantState);

        eventPublisher.publishEvent(participant);

        return ResponseEntity.ok(participant);
    }

    @GetMapping(path = "/api/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Participant>> getAllParticipants() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(participantService.getAllParticipants());
    }

    @GetMapping(path = "/api/participants/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> getParticipant(@PathVariable(value = "id") long participantId) {
        return ResponseEntity.ok(participantService.getParticipant(participantId));
    }

    @PutMapping(path = "/api/participants/{id}/laps", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> saveLap(@PathVariable(value = "id") long participantId, @RequestBody LapInput lapInput) {

        Participant participant = participantService.saveLap(participantId, lapInput.getFinishTime(), lapInput.getLapState());

        eventPublisher.publishEvent(participant);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(participant);
    }

    @PutMapping(path = "/api/participants/{id}/laps/{lapNumber}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> updateLapState(@PathVariable(value = "id") long participantId, @PathVariable Integer lapNumber, @PathVariable(value = "state") LapState lapState) {

        Participant participant = participantService.updateLapState(participantId, lapNumber, lapState);

        eventPublisher.publishEvent(participant);

        return ResponseEntity.ok(participant);
    }

    @DeleteMapping(path = "/api/participants/{id}/laps/{lapNumber}")
    public ResponseEntity<Participant> deleteLap(@PathVariable(value = "id") long participantId, @PathVariable Integer lapNumber) {

        Participant participant = participantService.deleteLap(participantId, lapNumber);

        eventPublisher.publishEvent(participant);

        return ResponseEntity.ok(participant);
    }
}
