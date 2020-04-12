package org.ikuven.bbut.tracking;

import org.ikuven.bbut.tracking.generation.ParticipantGenerator;
import org.ikuven.bbut.tracking.repository.DatabaseSequenceRepository;
import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

@SpringBootApplication
@EnableMongoRepositories
public class ResultTrackingApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ResultTrackingApplication.class, args);
    }

    @Autowired
    ParticipantRepository repository;

    @Autowired
    DatabaseSequenceRepository sequenceRepository;

    @Autowired
    ParticipantGenerator participantGenerator;

    @Autowired
    Environment environment;

    @Override
    public void run(String... args) {

        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            sequenceRepository.deleteAll();
            repository.deleteAll();
        }

        if (Arrays.asList(environment.getActiveProfiles()).contains("demo")) {
            participantGenerator.generate();
        }
    }
}
