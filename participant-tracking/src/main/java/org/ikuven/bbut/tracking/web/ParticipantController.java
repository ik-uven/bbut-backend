package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.participant.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
public class ParticipantController {

    private final ParticipantService participantService;

    private final ApplicationEventPublisher eventPublisher;

    public ParticipantController(ParticipantService participantService, ApplicationEventPublisher eventPublisher) {
        this.participantService = participantService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping(path = "/participants", consumes = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<ParticipantDto> registerParticipant(@RequestBody ParticipantInput participantInput) {

        Gender gender = Gender.valueOf(participantInput.getGender());

        Participant participant = participantService.registerParticipant(participantInput.getFirstName(), participantInput.getLastName(), participantInput.getClub(), participantInput.getTeam(), gender, participantInput.getBirthYear());

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

    @GetMapping(path = "/participants/teams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamDto>> getAllTeams() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(
                        participantService.getAllTeams().stream()
                                .map(this::toTeamDto)
                                .collect(Collectors.toList())
                );
    }

    @GetMapping(path = "/participants/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> getParticipant(@PathVariable(value = "id") long participantId) {
        return ResponseEntity.ok(toDto(participantService.getParticipant(participantId)));
    }

    @PutMapping(path = "/participants/{id}/laps", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> saveLap(@PathVariable(value = "id") long participantId, @RequestBody LapInput lapInput, @RequestHeader(value = "x-client-origin", defaultValue = "QR") String origin) {

        Participant participant = participantService.saveLap(participantId, lapInput.getLapState(), ClientOrigin.valueOf(origin.toUpperCase()));

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
        return ParticipantDto.of(participant.getId(), participant.getFirstName(), participant.getLastName(), participant.getClub(), participant.getTeam(), participant.getGender(), participant.getParticipantState(), participant.getLaps());
    }

    private TeamDto toTeamDto(Team team) {
        return TeamDto.of(team.getName(), team.getParticipants(), team.getTotalLaps());
    }
}
