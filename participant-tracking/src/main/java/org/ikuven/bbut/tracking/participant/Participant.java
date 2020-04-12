package org.ikuven.bbut.tracking.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "participants")
@AllArgsConstructor(staticName = "of")
public class Participant {

    @Transient
    public static final String SEQUENCE_NAME = "participants_sequence";

    @Id
    private long id;
    private String firstName;
    private String lastName;
    private String team;
    private ParticipantState participantState;
    private List<Lap> laps;

    public void addLap(LocalDateTime finishTime, LapState lapState) {
        laps.add(Lap.of(nextLapNumber(), finishTime, lapState));
    }

    private int nextLapNumber() {
        return laps.size() + 1;
    }
}
