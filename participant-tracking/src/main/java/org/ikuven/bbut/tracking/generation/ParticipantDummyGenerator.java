package org.ikuven.bbut.tracking.generation;

import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ParticipantDummyGenerator {

    private final List<String> shuffledFemaleGivenNames;
    private final List<String> shuffledMaleGivenNames;
    private final List<String> shuffledSurnames;
    private final List<String> shuffledClubs;
    private final List<String> shuffledTeams;

    private final ParticipantService participantService;

    @Autowired
    private ParticipantDummyGenerator(ParticipantService participantService) {
        this.participantService = participantService;

        shuffledFemaleGivenNames = readNames("names-givenname-women.txt")
                .collect(toShuffledStream())
                .collect(Collectors.toList());

        shuffledMaleGivenNames = readNames( "names-givenname-men.txt")
                .collect(toShuffledStream())
                .collect(Collectors.toList());

        shuffledSurnames = readNames("names-surname.txt")
                .collect(toShuffledStream())
                .collect(Collectors.toList());

        shuffledClubs = readNames("names-club.txt")
                .collect(toShuffledStream())
                .collect(Collectors.toList());

        shuffledTeams = readNames("names-team.txt")
                .collect(toShuffledStream())
                .collect(Collectors.toList());
    }

    public void generate() {

        for (int i = 1; i <= 8; i++) {
            String newGivenNames;
            Gender gender;
            String newsSurname = generateLastName();
            String club = generateClub();
            String team = generateTeam();

            if (isEven(getRandomInteger(1, 10))) {
                newGivenNames = generateFemaleGivenNames();
                gender = Gender.FEMALE;
            } else {
                newGivenNames = generateMaleGivenNames();
                gender = Gender.MALE;
            }

            participantService.registerParticipant(newGivenNames, newsSurname, club, team, gender, 1974);
        }
    }

    public String generateFemaleGivenNames() {
        return shuffledFemaleGivenNames.get(new Random().nextInt(shuffledFemaleGivenNames.size()));
    }

    private String generateMaleGivenNames() {
        return shuffledMaleGivenNames.get(new Random().nextInt(shuffledMaleGivenNames.size()));
    }

    public String generateLastName() {
        return shuffledSurnames.get(new Random().nextInt(shuffledSurnames.size()));
    }

    public String generateClub() {
        return shuffledClubs.get(new Random().nextInt(shuffledClubs.size()));
    }

    private String generateTeam() {
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

    public boolean isEven(int number) {
        return (number & 1) == 0;
    }

    public int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }
}
