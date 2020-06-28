package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.admin.ParticipantAdminService;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantEvent;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.ikuven.bbut.tracking.participant.ParticipantEvent.of;

@RestController
@RequestMapping(path = "/api/admin")
public class AdminController {

    private final ParticipantAdminService participantAdminService;
    private final ParticipantService participantService;

    private final ApplicationEventPublisher eventPublisher;


    @Autowired
    public AdminController(ParticipantAdminService participantAdminService, ParticipantService participantService, ApplicationEventPublisher eventPublisher) {
        this.participantAdminService = participantAdminService;
        this.participantService = participantService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping(path = "/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParticipantAdminDto>> getAllParticipants() {

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
    public ResponseEntity<ParticipantAdminDto> registerParticipant(@RequestBody ParticipantUpdateInput participantInput) {
        participantInput.setId(0L);
        Participant participant = participantService.registerParticipant(toParticipant(participantInput));

        eventPublisher.publishEvent(of(ParticipantEvent.EventId.ADDED_PARTICIPANT, participant, String.format("participantId %d", participant.getId())));

        return ResponseEntity.ok().body(toDto(participant));
    }

    @PutMapping(path = "/participants/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantAdminDto> updateParticipant(@PathVariable("id") long participantId, @RequestBody ParticipantUpdateInput participantInput) {
        Participant participant = participantAdminService.updateParticipant(toParticipant(participantInput));

        eventPublisher.publishEvent(of(ParticipantEvent.EventId.CHANGED_PARTICIPANT, participant, String.format("participantId %d", participant.getId())));

        return ResponseEntity.ok().body(toDto(participant));
    }

    private Participant toParticipant(ParticipantUpdateInput input) {
        if (input == null) {
            throw new IllegalArgumentException("participant input must not be empty");
        }

        return Participant.of(input.getId(), input.getFirstName(), input.getLastName(), input.getClub(), input.getTeam(), input.getGender(), null, null);
    }

    private ParticipantAdminDto toDto(Participant participant) {
        return ParticipantAdminDto.of(participant.getId(), participant.getFirstName(), participant.getLastName(), participant.getClub(), participant.getTeam(), participant.getGender());
    }
}
