package org.ikuven.bbut.tracking;

import org.ikuven.bbut.tracking.generation.ParticipantDummyGenerator;
import org.ikuven.bbut.tracking.repository.DatabaseSequenceRepository;
import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableMongoRepositories
public class ResultTrackingApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ResultTrackingApplication.class, args);
    }

    private final ParticipantRepository repository;

    private final DatabaseSequenceRepository sequenceRepository;

    private final ParticipantDummyGenerator participantDummyGenerator;

    private final Environment environment;

    @Autowired
    public ResultTrackingApplication(ParticipantRepository repository, DatabaseSequenceRepository sequenceRepository, ParticipantDummyGenerator participantDummyGenerator, Environment environment) {
        this.repository = repository;
        this.sequenceRepository = sequenceRepository;
        this.participantDummyGenerator = participantDummyGenerator;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {

        List<String> profiles = Arrays.asList(environment.getActiveProfiles());

        if (profiles.contains("dev") && profiles.contains("demo")) {
            sequenceRepository.deleteAll();
            repository.deleteAll();
            participantDummyGenerator.generate();
        }
    }
}
