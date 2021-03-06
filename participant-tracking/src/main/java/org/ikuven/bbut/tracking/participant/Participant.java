package org.ikuven.bbut.tracking.participant;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "participants")
@NoArgsConstructor
public class Participant {

    @Transient
    public static final String SEQUENCE_NAME = "participants_sequence";

    @Id
    private long id;
    private long startNumber;
    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private ParticipantClass participantClass;
    private LocalDate birthDate;
    private ParticipantState participantState;
    private List<Lap> laps = new ArrayList<>();

    public static Participant of(long id, long startNumber, String firstName, String lastName, String club, String team, ParticipantClass participantClass, LocalDate birthDate, ParticipantState participantState) {
        return new Participant(id, startNumber, firstName, lastName, club, team, participantClass, birthDate, participantState);
    }

    public static Participant of(long id, long startNumber, String firstName, String lastName, String club, String team, ParticipantClass participantClass, LocalDate birthDate, ParticipantState participantState, List<Lap> laps) {
        return new Participant(id, startNumber, firstName, lastName, club, team, participantClass, birthDate, participantState, laps);
    }

    private Participant(long id, long startNumber, String firstName, String lastName, String club, String team, ParticipantClass participantClass, LocalDate birthDate, ParticipantState participantState) {
        this.id = id;
        this.startNumber = startNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.team = team;
        this.participantClass = participantClass;
        this.birthDate = birthDate;
        this.participantState = participantState;
    }

    private Participant(long id, long startNumber, String firstName, String lastName, String club, String team, ParticipantClass participantClass, LocalDate birthDate, ParticipantState participantState, List<Lap> laps) {
        this(id, startNumber, firstName, lastName, club, team, participantClass, birthDate, participantState);
        this.laps = laps;
    }

    public void addLap(LocalDateTime registrationTime, LapState lapState) {
        laps.add(Lap.of(nextLapNumber(), registrationTime, lapState));
    }

    public void updateLapState(LapState lapState, Integer lapNumber) {
        this.getLaps().stream()
                .filter(lap -> lap.getNumber() == lapNumber)
                .findFirst()
                .ifPresent(lap -> lap.setState(lapState));

    }

    public Lap getLastLap() {
        return laps.stream()
                .reduce((first, second) -> second)
                .orElse(null);
    }

    public LapState getLastLapState() {
        Lap lastLap = getLastLap();
        return lastLap != null ? lastLap.getState() : LapState.NONE;
    }

    public int getLastSuccessfulLapNumber() {
        int lapNumber = 0;

        Lap lastLap = getLastLap();
        if (lastLap != null) {
            lapNumber = lastLap.getState().equals(LapState.COMPLETED)  ? lastLap.getNumber() : lastLap.getNumber() - 1;
        }

        return lapNumber;
    }

    private int nextLapNumber() {
        return laps.size() + 1;
    }

}
