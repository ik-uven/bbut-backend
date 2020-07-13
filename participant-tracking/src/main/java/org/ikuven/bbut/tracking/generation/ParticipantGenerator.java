package org.ikuven.bbut.tracking.generation;

import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ParticipantGenerator {

    private final ParticipantService participantService;

    @Autowired
    private ParticipantGenerator(ParticipantService participantService) {
        this.participantService = participantService;
    }

    public void generate(String firstName, String lastName, String club, String gender, LocalDate birthDate) {
        generate(firstName, lastName, club, null, gender, birthDate);
    }

    public void generate(String firstName, String lastName, String club, String team, String gender, LocalDate birthDate) {
        this.participantService.registerParticipant(firstName, lastName, club, team, Gender.valueOf(gender), birthDate);
    }
}
