package org.ikuven.bbut.racesimulator.client;

import lombok.Data;

import java.util.List;

@Data
public class Participant {
    private long id;
    private String firstName;
    private String lastName;
    private String participantState;
    private List<Lap> laps;
}
