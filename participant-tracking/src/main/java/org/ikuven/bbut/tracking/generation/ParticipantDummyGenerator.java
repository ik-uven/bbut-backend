package org.ikuven.bbut.tracking.generation;

import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.repository.DatabaseSequenceRepository;
import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

@Profile("dev & demo")
@Component
public class ParticipantDummyGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantDummyGenerator.class);

    private final List<String> shuffledFemaleGivenNames;
    private final List<String> shuffledMaleGivenNames;
    private final List<String> shuffledSurnames;
    private final List<String> shuffledClubs;
    private final List<String> shuffledTeams;

    private final ParticipantRepository repository;
    private final DatabaseSequenceRepository sequenceRepository;
    private final ParticipantService participantService;

    private final Environment environment;

    @Autowired
    private ParticipantDummyGenerator(ParticipantRepository repository, DatabaseSequenceRepository sequenceRepository, ParticipantService participantService, Environment environment) {
        this.repository = repository;
        this.sequenceRepository = sequenceRepository;
        this.participantService = participantService;
        this.environment = environment;

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

    @PostConstruct
    public void generate() {

        sequenceRepository.deleteAll();
        repository.deleteAll();

        int amountToGenerate = environment.getProperty("demo.participants-to-generate", Integer.class, 8);

        for (int i = 1; i <= amountToGenerate; i++) {
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

        LOGGER.info("Generated {} demo participants", amountToGenerate);
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
