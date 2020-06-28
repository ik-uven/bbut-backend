package org.ikuven.bbut.tracking.participant;

import org.springframework.context.ApplicationEvent;

public class ParticipantEvent extends ApplicationEvent {

    public enum EventId {
        REGISTERED,
        CHANGED_STATE,
        SAVED_LAP,
        CHANGED_LAP_STATE,
        DELETED_LAP,
        ADDED_PARTICIPANT,
        CHANGED_PARTICIPANT,
        DELETED_PARTICIPANT
    }

    private final EventId eventId;
    private final String message;

    public static ParticipantEvent of(EventId eventId, Participant participant, String message) {
        return new ParticipantEvent(participant, eventId, message);
    }

    private ParticipantEvent(Object source, EventId eventId, String message) {
        super(source);
        this.eventId = eventId;
        this.message = message;
    }

    public Participant getParticipant() {
        return (Participant) getSource();
    }

    public EventId getEventId() {
        return eventId;
    }

    public String getMessage() {
        return message;
    }
}
