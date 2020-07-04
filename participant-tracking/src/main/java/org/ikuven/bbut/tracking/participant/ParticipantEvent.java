package org.ikuven.bbut.tracking.participant;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Value
public class ParticipantEvent {

    public enum Type {
        REGISTERED_PARTICIPANT,
        CHANGED_STATE,
        SAVED_LAP,
        CHANGED_LAP_STATE,
        DELETED_LAP,
        CHANGED_PARTICIPANT,
        DELETED_PARTICIPANT;
    }

    long participantId;
    Type type;
    String message;
    LocalDateTime timestamp = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    Participant source;

    public static ParticipantEvent of(Type type, Participant participant, String message) {
        return new ParticipantEvent(participant, type, message);
    }

    private ParticipantEvent(Participant source, Type type, String message) {
        this.type = type;
        this.message = message;
        this.source = source;
        this.participantId = source != null ? source.getId() : 0L;

        if (source == null) {
            log.warn("The source object of event {} {} was null, this indicates a programming error", type, message);
        }
    }
}
