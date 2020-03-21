package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantLapService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrackingController {

    private ParticipantLapService participantLapService;

    public TrackingController(ParticipantLapService participantLapService) {
        this.participantLapService = participantLapService;
    }

    @GetMapping(path = "/api/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Participant>> getAllParticipants() {

        return ResponseEntity.ok(participantLapService.getAllParticipants());
    }

    @PostMapping(path = "/api/participants/{id}/laps", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Participant> saveLap(@PathVariable(value = "id") int participantId, @RequestBody LapInput lapInput) {

        return ResponseEntity.ok(participantLapService.saveLap(participantId, lapInput.getFinishTime(), lapInput.getLapState()));
    }
}
