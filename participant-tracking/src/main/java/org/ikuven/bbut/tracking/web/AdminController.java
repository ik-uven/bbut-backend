package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.admin.ParticipantAdminService;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/admin")
public class AdminController {

    private final ParticipantAdminService participantAdminService;
    private final ParticipantService participantService;

    @Autowired
    public AdminController(ParticipantAdminService participantAdminService, ParticipantService participantService) {
        this.participantAdminService = participantAdminService;
        this.participantService = participantService;
    }

    @GetMapping(path = "/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParticipantDto>> getAllParticipants() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .body(
                        participantAdminService.getAllParticipants().stream()
                                .map(this::toDto)
                                .collect(Collectors.toList())
                );
    }

    @PostMapping(path = "/participants", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> registerParticipant(@RequestBody ParticipantUpdateInput participantInput) {
        participantInput.setId(0L);
        Participant participant = participantService.registerParticipant(toParticipant(participantInput));

        return ResponseEntity.ok().body(toDto(participant));
    }

    @PutMapping(path = "/participants/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantDto> updateParticipant(@PathVariable("id") long participantId, @RequestBody ParticipantUpdateInput participantInput) {

        participantInput.setId(participantId);

        Participant participant = participantAdminService.updateParticipant(toParticipant(participantInput));

        return ResponseEntity.ok().body(toDto(participant));
    }

    @DeleteMapping(path = "/participants/{id}")
    public ResponseEntity<String> deleteParticipant(@PathVariable("id") long participantId) {

        participantAdminService.deleteParticipant(participantId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    private Participant toParticipant(ParticipantUpdateInput input) {
        if (input == null) {
            throw new IllegalArgumentException("participant input must not be empty");
        }

        return Participant.of(input.getId(), input.getFirstName(), input.getLastName(), input.getClub(), input.getTeam(), input.getParticipantClass(), null, null);
    }

    private ParticipantDto toDto(Participant participant) {
        return ParticipantDto.of(participant.getId(), participant.getFirstName(), participant.getLastName(), participant.getClub(), participant.getTeam(), participant.getParticipantClass(), participant.getParticipantState(), participant.getLaps());
    }
}
