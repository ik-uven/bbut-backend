package org.ikuven.bbut.tracking.participant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ParticipantEventLoggerTest {

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private ParticipantEventLogger eventLogger;

    @Test
    @DisplayName("should not fail when input is null")
    void shouldGracefullyHandle_whenNullEvent() throws JsonProcessingException {

        ArgumentCaptor<ParticipantEvent> eventCaptor = ArgumentCaptor.forClass(ParticipantEvent.class);

        assertDoesNotThrow(() -> eventLogger.participantEventListener(null));

        verify(objectMapper).writeValueAsString(eventCaptor.capture());

        assertThat(eventCaptor.getValue()).isNull();
    }

    @Test
    @DisplayName("should not fail when input fields all are null")
    void shouldGracefullyHandle_whenAllFieldsAreNull() throws JsonProcessingException {

        ArgumentCaptor<ParticipantEvent> eventCaptor = ArgumentCaptor.forClass(ParticipantEvent.class);

        assertDoesNotThrow(() -> eventLogger.participantEventListener(ParticipantEvent.of(null, null, null)));

        verify(objectMapper).writeValueAsString(eventCaptor.capture());

        assertThat(eventCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("should log the event correctly")
    void shouldLogEvent() throws JsonProcessingException {

        Participant participant = Participant.of(1L, 1, "first", "last", "club", "team", ParticipantClass.WOMEN, null, null);
        ParticipantEvent event = ParticipantEvent.of(ParticipantEvent.Type.CHANGED_STATE, participant, "event message");

        ArgumentCaptor<ParticipantEvent> eventCaptor = ArgumentCaptor.forClass(ParticipantEvent.class);

        eventLogger.participantEventListener(event);

        verify(objectMapper).writeValueAsString(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isNotNull();

        assertThat(eventLogger.toJson(event))
                .isNotBlank()
                .contains(ParticipantEvent.Type.CHANGED_STATE.toString(), "event message", "first", "last", "club", "team", ParticipantClass.WOMEN.toString());

    }
}
