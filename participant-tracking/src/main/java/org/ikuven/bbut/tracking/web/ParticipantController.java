package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.participant.*;
import org.ikuven.bbut.tracking.settings.BackendSettingsProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
public class ParticipantController {

    private final ParticipantService participantService;
    private final BackendSettingsProperties backendSettingsProperties;
    private final ExcelService excelService;

    public ParticipantController(ParticipantService participantService, BackendSettingsProperties backendSettingsProperties, ExcelService excelService) {
        this.participantService = participantService;
        this.backendSettingsProperties = backendSettingsProperties;
        this.excelService = excelService;
    }

    @PostMapping(path = "/participants", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> registerParticipant(@RequestBody ParticipantInput participantInput) {

        ParticipantClass participantClass = ParticipantClass.valueOf(participantInput.getGender());

        Participant participant = participantService.registerParticipant(participantInput.getFirstName(), participantInput.getLastName(), participantInput.getClub(), participantInput.getTeam(), participantClass, participantInput.getBirthDate());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toDto(participant));
    }

    @PutMapping(path = "/participants/{id}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> registerState(@PathVariable(value = "id") long participantId, @PathVariable(value = "state") ParticipantState participantState) {

        Participant participant = participantService.setState(participantId, participantState);

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
    public ResponseEntity<TeamWrapperDto> getAllTeams() {

        final var minSize = backendSettingsProperties.getTeams().getMinSize();

        final var teamDtoList = participantService.getAllTeams().stream()
                .filter(team -> team.getParticipants().size() >= minSize)
                .map(this::toTeamDto)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(toTeamWrapperDto(teamDtoList, minSize));
    }

    @GetMapping(path = "/participants/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> getParticipant(@PathVariable(value = "id") long participantId) {
        return ResponseEntity.ok(toDto(participantService.getParticipant(participantId)));
    }

    @PutMapping(path = "/participants/{id}/laps", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> saveLap(@PathVariable(value = "id") long participantId, @RequestBody LapInput lapInput, @RequestHeader(value = "x-client-origin", defaultValue = "QR") String origin) {

        Participant participant = participantService.saveLap(participantId, lapInput.getLapState(), ClientOrigin.valueOf(origin.toUpperCase()));

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(toDto(participant));
    }

    @PutMapping(path = "/participants/{id}/laps/{lapNumber}/states/{state}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> updateLapState(@PathVariable(value = "id") long participantId, @PathVariable Integer lapNumber, @PathVariable(value = "state") LapState lapState) {

        Participant participant = participantService.updateLapState(participantId, lapNumber, lapState);

        return ResponseEntity.ok(toDto(participant));
    }

    @DeleteMapping(path = "/participants/{id}/laps/{lapNumber}")
    public ResponseEntity<ParticipantDto> deleteLap(@PathVariable(value = "id") long participantId, @PathVariable Integer lapNumber) {

        Participant participant = participantService.deleteLap(participantId, lapNumber);

        return ResponseEntity.ok(toDto(participant));
    }

    @GetMapping(value = "/participants/download")
    public ResponseEntity<InputStreamResource> excelParticipants() throws IOException {
        List<Participant> participants = participantService.getAllParticipants();
        List<Team> teams = participantService.getAllTeams();

        ByteArrayInputStream in = excelService.exportToExcel(participants, teams);

        final var now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String fileName = String.format("results-%s.xlsx", now);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=%s", fileName));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    private ParticipantDto toDto(Participant participant) {
        return ParticipantDto.of(participant.getId(), participant.getFirstName(), participant.getLastName(), participant.getClub(), participant.getTeam(), participant.getParticipantClass(), participant.getParticipantState(), participant.getLaps());
    }

    private TeamDto toTeamDto(Team team) {
        return TeamDto.of(team.getName(), team.getParticipants(), team.getTotalCompletedLaps());
    }

    private TeamWrapperDto toTeamWrapperDto(List<TeamDto> teamDtoList, long teamSize) {
        return TeamWrapperDto.of(teamDtoList, teamSize);
    }
}
