package org.ikuven.bbut.tracking.generation;

import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.participantService.registerParticipant(firstName, lastName, club, team, Participant.Gender.valueOf(gender), birthYear);
    }
}
