package org.ikuven.bbut.tracking.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikuven.bbut.tracking.imports.ImportEvent;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantEvent;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class LiveController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ParticipantService participantService;

    private final ObjectMapper objectMapper;

    @Autowired
    public LiveController(SimpMessagingTemplate simpMessagingTemplate, ParticipantService participantService, ObjectMapper objectMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.participantService = participantService;
        this.objectMapper = objectMapper;
    }

    @Async
    @EventListener
    public void participantListener(ParticipantEvent event) throws Exception {
        this.simpMessagingTemplate.convertAndSend("/topics/results", objectMapper.writeValueAsString(participantService.getAllParticipants()));
    }

    @Async
    @EventListener
    public void importListener(ImportEvent event) throws Exception {
        this.simpMessagingTemplate.convertAndSend("/topics/results", objectMapper.writeValueAsString(participantService.getAllParticipants()));
    }
}
