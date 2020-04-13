package org.ikuven.bbut.tracking.generation;

import org.ikuven.bbut.tracking.participant.LapState;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.participant.ParticipantState;
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

    private final List<String> shuffledGivenNames;
    private final List<String> shuffledSurnames;
    private final List<String> shuffledTeams;

    private final ParticipantService participantService;

    @Autowired
    private ParticipantGenerator(ParticipantService participantService) {
        this.participantService = participantService;

        Stream<String> women = readNames("names-givenname-women.txt");
        Stream<String> men = readNames( "names-givenname-men.txt");

        shuffledGivenNames = Stream.concat(women, men)
                .collect(toShuffledStream())
                .collect(Collectors.toList());

        shuffledSurnames = readNames("names-surname.txt")
                .collect(toShuffledStream())
                .collect(Collectors.toList());

        shuffledTeams = readNames("names-team.txt")
                .collect(toShuffledStream())
                .collect(Collectors.toList());
    }

    public void generate() {

        LocalDateTime now = LocalDateTime.parse("2020-08-08T10:00:00");

        for (int i = 1; i <= 8; i++) {
            String newGivenNames = generateGivenNames();
            String newsSurname = generateLastName();
            String newTeam = generateTeam();

            participantService.registerParticipant(newGivenNames, newsSurname, newTeam);
        }

        participantService.getAllParticipants().stream()
                .skip(3)
                .forEach(participant -> {
                    participantService.setState(participant.getId(), ParticipantState.ACTIVE);
                    for (int i = 0; i < 3; i++) {
                        participantService.saveLap(participant.getId(), now.plusHours(i).plusMinutes(45 + i), LapState.COMPLETED);
                    }
                });
    }

    public String generateGivenNames() {
        return shuffledGivenNames.get(new Random().nextInt(shuffledGivenNames.size()));
    }

    public String generateLastName() {
        return shuffledSurnames.get(new Random().nextInt(shuffledSurnames.size()));
    }

    public String generateTeam() {
        return shuffledTeams.get(new Random().nextInt(shuffledTeams.size()));
    }

    private Stream<String> readNames(String fileName) {

        InputStream resourceAsStream;

        try {
            ClassPathResource classPathResource = new ClassPathResource(fileName);
            resourceAsStream = classPathResource.getInputStream();

        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Could not find file named %s", fileName));
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));

        return reader.lines();
    }

    private <T> Collector<T, ?, Stream<T>> toShuffledStream() {
        return Collectors.collectingAndThen(Collectors.toList(), strings -> {
            Collections.shuffle(strings);
            return strings.stream();
        });
    }
}
