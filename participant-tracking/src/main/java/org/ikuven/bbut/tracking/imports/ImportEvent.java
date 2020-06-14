package org.ikuven.bbut.tracking.imports;

import org.ikuven.bbut.tracking.participant.Participant;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class ImportEvent extends ApplicationEvent {

    public enum EventId {
        IMPORTED
    }

    public static ImportEvent of(List<Participant> participants, EventId eventId, String message) {
        return new ImportEvent(participants, eventId, message);
    }

    private final EventId eventId;
    private final String message;

    private ImportEvent(Object source, EventId eventId, String message) {
        super(source);
        this.eventId = eventId;
        this.message = message;
    }

    public EventId getEventId() {
        return eventId;
    }

    public String getMessage() {
        return message;
    }
}
