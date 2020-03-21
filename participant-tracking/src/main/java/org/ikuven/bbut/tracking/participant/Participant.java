package org.ikuven.bbut.tracking.participant;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class Participant {

    private int id;
    private String firstName;
    private String lastName;
    private List<Lap> laps;

    public void addLap(LocalDateTime finishTime, LapState lapState) {
        if (laps == null) {
            laps = new LinkedList<>();
        }

        laps.add(Lap.of(nextLapNumber(), finishTime, lapState));
    }

    private int nextLapNumber() {
        return laps.size() + 1;
    }
}
