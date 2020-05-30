package org.ikuven.bbut.tracking.generation;

import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParticipantGenerator {

    private final ParticipantService participantService;

    @Autowired
    private ParticipantGenerator(ParticipantService participantService) {
        this.participantService = participantService;
    }

    public void generate(String firstName, String lastName, String club, String gender, Integer birthYear) {
        generate(firstName, lastName, club, null, gender, birthYear);
    }

    public void generate(String firstName, String lastName, String club, String team, String gender, Integer birthYear) {
        this.participantService.registerParticipant(firstName, lastName, club, team, Gender.valueOf(gender), birthYear);
    }
}
