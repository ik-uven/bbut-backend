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
    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private Gender gender;
    private LocalDate birthDate;
    private ParticipantState participantState;
    private List<Lap> laps = new ArrayList<>();

    public static Participant of(long id, String firstName, String lastName, String club, String team, Gender gender, LocalDate birthDate, ParticipantState participantState) {
        return new Participant(id, firstName, lastName, club, team, gender, birthDate, participantState);
    }

    public static Participant of(long id, String firstName, String lastName, String club, String team, Gender gender, LocalDate birthDate, ParticipantState participantState, List<Lap> laps) {
        return new Participant(id, firstName, lastName, club, team, gender, birthDate, participantState, laps);
    }

    private Participant(long id, String firstName, String lastName, String club, String team, Gender gender, LocalDate birthDate, ParticipantState participantState) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.team = team;
        this.gender = gender;
        this.birthDate = birthDate;
        this.participantState = participantState;
    }

    private Participant(long id, String firstName, String lastName, String club, String team, Gender gender, LocalDate birthDate, ParticipantState participantState, List<Lap> laps) {
        this(id, firstName, lastName, club, team, gender, birthDate, participantState);
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

    private int nextLapNumber() {
        return laps.size() + 1;
    }

}
