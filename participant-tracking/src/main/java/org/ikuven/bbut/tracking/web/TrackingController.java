package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.participant.ParticipantState;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrackingController {

    private ParticipantService participantService;

    public TrackingController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping(path = "/api/participants", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> registerParticipant(@RequestBody ParticipantInput participantInput) {

        return ResponseEntity.ok(participantService.registerParticipant(participantInput.getFirstName(), participantInput.getLastName()));
    }

    @PutMapping(path = "/api/participants/{id}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> registerState(@PathVariable(value = "id") long participantId, @PathVariable(value = "state") ParticipantState participantState) {

        return ResponseEntity.ok(participantService.setState(participantId, participantState));
    }

    @GetMapping(path = "/api/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Participant>> getAllParticipants() {

        return ResponseEntity.ok(participantService.getAllParticipants());
    }

    @GetMapping(path = "/api/participants/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> getParticipant(@PathVariable(value = "id") long participantId) {

        return ResponseEntity.ok(participantService.getParticipant(participantId));
    }

    @PutMapping(path = "/api/participants/{id}/laps", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> saveLap(@PathVariable(value = "id") long participantId, @RequestBody LapInput lapInput) {

        return ResponseEntity.ok(participantService.saveLap(participantId, lapInput.getFinishTime(), lapInput.getLapState()));
    }
}
