package org.ikuven.bbut.tracking.participant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class ParticipantEventLogger {

    @Async
    @EventListener
    public void eventListener(ParticipantEvent event) {
        log.info("{}: event participant {} {} {} - {}", timeStampToLocalDateTime(event.getTimestamp()), event.getParticipant().getId(), event.getEventId(), event.getMessage(), event.getSource());
    }

    private LocalDateTime timeStampToLocalDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
