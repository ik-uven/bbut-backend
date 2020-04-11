package org.ikuven.bbut.tracking;

import org.ikuven.bbut.tracking.participant.Lap;
import org.ikuven.bbut.tracking.participant.LapState;
import org.ikuven.bbut.tracking.repository.DatabaseSequenceRepository;
import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.LocalDateTime;

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

    @Override
    public void run(String... args) {

        sequenceRepository.deleteAll();
        repository.deleteAll();
//
//        Participant participant = Participant.of(1, "Ken", "Alexandersson", Collections.singletonList(createLap(1, LapState.NOT_STARTED)));
//        Participant participant2 = Participant.of(2, "Torbjörn", "Grahn", Arrays.asList(createLap(1, LapState.COMPLETED), createLap(2, LapState.COMPLETED)));
//        Participant participant3 = Participant.of(3, "Sune", "Bom", Arrays.asList(createLap(1, LapState.COMPLETED), createLap(2, LapState.STARTED)));
//        Participant participant4 = Participant.of(4, "Vera", "Blut", Collections.emptyList());
//
////        repository.save(participant);
////        repository.save(participant2);
////        repository.save(participant3);
//        repository.save(participant4);
    }

    private Lap createLap(int lapNumber, LapState lapState) {
        return Lap.of(lapNumber, LocalDateTime.now(), lapState);
    }
}
