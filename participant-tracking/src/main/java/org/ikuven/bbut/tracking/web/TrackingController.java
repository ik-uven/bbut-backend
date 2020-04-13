package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.participant.LapState;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.participant.ParticipantState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(participantService.registerParticipant(participantInput.getFirstName(), participantInput.getLastName(), participantInput.getTeam()));
    }

    @PutMapping(path = "/api/participants/{id}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> registerState(@PathVariable(value = "id") long participantId, @PathVariable(value = "state") ParticipantState participantState) {

        return ResponseEntity.ok(participantService.setState(participantId, participantState));
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(participantService.saveLap(participantId, lapInput.getFinishTime(), lapInput.getLapState()));
    }

    @PutMapping(path = "/api/participants/{id}/laps/{lapNumber}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> updateLapState(@PathVariable(value = "id") long participantId, @PathVariable Integer lapNumber, @PathVariable(value = "state") LapState lapState) {

        return ResponseEntity.ok(participantService.updateLapState(participantId, lapNumber, lapState));
    }

    @DeleteMapping(path = "/api/participants/{id}/laps/{lapNumber}")
    public ResponseEntity<Participant> deleteLap(@PathVariable(value = "id") long participantId, @PathVariable Integer lapNumber) {

        return ResponseEntity.ok(participantService.deleteLap(participantId, lapNumber));
    }
}
