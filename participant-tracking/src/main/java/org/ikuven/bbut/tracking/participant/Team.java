package org.ikuven.bbut.tracking.participant;

import java.util.LinkedList;
import java.util.List;

public class Team {

    private final String name;
    private final List<Participant> participants = new LinkedList<>();

    public static Team of(String name) {
        return new Team(name);
    }

    private Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public int getTotalCompletedLaps() {
        return participants.stream()
                .mapToInt(participant -> (int) participant.getLaps().stream()
                        .filter(lap -> lap.getState().equals(LapState.COMPLETED))
                        .count()
                )
                .sum();
    }
}
