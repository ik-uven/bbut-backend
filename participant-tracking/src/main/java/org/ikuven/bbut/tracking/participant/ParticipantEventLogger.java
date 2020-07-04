package org.ikuven.bbut.tracking.participant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ParticipantEventLogger {

    private final ObjectMapper objectMapper;

    @Autowired
    public ParticipantEventLogger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Async
    @EventListener
    public void participantEventListener(ParticipantEvent event) throws JsonProcessingException {
        log.info(toJson(event));
    }

    String toJson(ParticipantEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }
}
