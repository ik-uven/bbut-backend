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
import java.util.stream.Collectors;

@RestController
public class TrackingController {

    private final ParticipantService participantService;

    private final ApplicationEventPublisher eventPublisher;

    public TrackingController(ParticipantService participantService, ApplicationEventPublisher eventPublisher) {
        this.participantService = participantService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping(path = "/participants", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> registerParticipant(@RequestBody ParticipantInput participantInput) {

        Participant participant = participantService.registerParticipant(participantInput.getFirstName(), participantInput.getLastName(), participantInput.getTeam());

        eventPublisher.publishEvent(participant);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toDto(participant));
    }

    @PutMapping(path = "/participants/{id}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> registerState(@PathVariable(value = "id") long participantId, @PathVariable(value = "state") ParticipantState participantState) {

        Participant participant = participantService.setState(participantId, participantState);

        eventPublisher.publishEvent(participant);

        return ResponseEntity.ok(toDto(participant));
    }

    @GetMapping(path = "/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParticipantDto>> getAllParticipants() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(
                        participantService.getAllParticipants().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
                );
    }

    @GetMapping(path = "/participants/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> getParticipant(@PathVariable(value = "id") long participantId) {
        return ResponseEntity.ok(toDto(participantService.getParticipant(participantId)));
    }

    @PutMapping(path = "/participants/{id}/laps", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> saveLap(@PathVariable(value = "id") long participantId, @RequestBody LapInput lapInput) {

        Participant participant = participantService.saveLap(participantId, lapInput.getFinishTime(), lapInput.getLapState());

        eventPublisher.publishEvent(participant);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(toDto(participant));
    }

    @PutMapping(path = "/participants/{id}/laps/{lapNumber}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> updateLapState(@PathVariable(value = "id") long participantId, @PathVariable Integer lapNumber, @PathVariable(value = "state") LapState lapState) {

        Participant participant = participantService.updateLapState(participantId, lapNumber, lapState);

        eventPublisher.publishEvent(participant);

        return ResponseEntity.ok(toDto(participant));
    }

    @DeleteMapping(path = "/participants/{id}/laps/{lapNumber}")
    public ResponseEntity<ParticipantDto> deleteLap(@PathVariable(value = "id") long participantId, @PathVariable Integer lapNumber) {

        Participant participant = participantService.deleteLap(participantId, lapNumber);

        eventPublisher.publishEvent(participant);

        return ResponseEntity.ok(toDto(participant));
    }

    private ParticipantDto toDto(Participant participant) {
        return ParticipantDto.of(participant.getId(), participant.getFirstName(), participant.getLastName(), participant.getTeam(), participant.getParticipantState(), participant.getLaps());
    }
}
