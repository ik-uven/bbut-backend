package org.ikuven.bbut.tracking.participant;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@Document(collection = "participants")
@NoArgsConstructor
public class Participant {

    @Transient
    public static final String SEQUENCE_NAME = "participants_sequence";

    @Id
    private long id;
    private String firstName;
    private String lastName;
    private String team;
    private ParticipantState participantState;
    private List<Lap> laps = Collections.emptyList();
    private LapState lastLapState = LapState.NONE;

    public static Participant of(long id, String firstName, String lastName, String team, ParticipantState participantState) {
        return new Participant(id, firstName, lastName, team, participantState);
    }

    public static Participant of(long id, String firstName, String lastName, String team, ParticipantState participantState, List<Lap> laps) {
        return new Participant(id, firstName, lastName, team, participantState, laps);
    }

    private Participant(long id, String firstName, String lastName, String team, ParticipantState participantState) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
        this.participantState = participantState;
    }

    private Participant(long id, String firstName, String lastName, String team, ParticipantState participantState, List<Lap> laps) {
        this(id, firstName, lastName, team, participantState);
        this.laps = laps;
    }

    public void addLap(LocalDateTime finishTime, LapState lapState) {
        laps.add(Lap.of(nextLapNumber(), finishTime, lapState));
        lastLapState = lapState;
    }

    public void updateLapState(LapState lapState, Integer lapNumber) {
        this.getLaps().stream()
                .filter(lap -> lap.getNumber() == lapNumber)
                .findFirst()
                .ifPresent(lap -> lap.setState(lapState));

        if (this.getLaps().size() == lapNumber) {
            this.setLastLapState(lapState);
        }
    }

    private int nextLapNumber() {
        return laps.size() + 1;
    }
}
