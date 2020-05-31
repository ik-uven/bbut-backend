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

    public int getTotalLaps() {
        return participants.stream()
                .mapToInt(participant -> participant.getLaps().size())
                .sum();
    }
}
