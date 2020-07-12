package org.ikuven.bbut.racesimulator;

import lombok.extern.slf4j.Slf4j;
import org.ikuven.bbut.racesimulator.client.Participant;
import org.ikuven.bbut.racesimulator.client.RaceClient;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class RaceSimulator {

    private final RaceClient raceClient;

    public RaceSimulator(RaceClient raceClient) {
        this.raceClient = raceClient;
        this.init();
    }

    private void init() {
        this.activateAll();
        log.info("Initialized Simulator");
    }

    private void activateAll() {
        raceClient.getAllParticipants().stream()
                .filter(participant -> participant.getLaps().size() == 0)
                .forEach(raceClient::activate);
    }

    @Scheduled(cron = "${cron.expression}")
    public void handleParticipantsOnGoalLine() {
        log.info("Scheduler triggered at " + LocalDateTime.now());
        List<Participant> participants = raceClient.getActiveParticipants();

        boolean isFirstLap = participants.stream()
                .map(Participant::getLaps)
                .map(List::size)
                .max(Integer::compareTo)
                .orElse(0) == 0;

        int numbersToQuit;

        int size = participants.size();

        if (isFirstLap) {
            numbersToQuit = 0;
        } else if (size <= 5) {
            numbersToQuit = getRandomInteger(0, 1);
        } else if (size <= 10) {
            numbersToQuit = getRandomInteger(1, 4);
        } else {
            numbersToQuit = getRandomInteger(0, 5);
        }

        List<Long> quitters = participants.stream()
                .map(Participant::getId)
                .collect(toShuffledStream())
                .limit(numbersToQuit)
                .collect(Collectors.toList());

        participants.stream()
                .map(Participant::getId)
                .forEach(id -> handleParticipant(id, quitters));
    }

    private void handleParticipant(Long id, List<Long> quitters) {

        if (quitters.contains(id) && getRandomInteger(0, 10) > 7) {
            raceClient.addLap(id, "OVERDUE");
        } else if (quitters.contains(id)) {
            raceClient.addLap(id, "COMPLETED");
            raceClient.resignParticipant(id);
        } else {
            raceClient.addLap(id, "COMPLETED");
        }
    }

    public static int getRandomInteger(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private <T> Collector<T, ?, Stream<T>> toShuffledStream() {
        return Collectors.collectingAndThen(Collectors.toList(), strings -> {
            Collections.shuffle(strings);
            return strings.stream();
        });
    }
}
