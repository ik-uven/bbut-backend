package org.ikuven.bbut.tracking.statistics;

import lombok.extern.slf4j.Slf4j;
import org.ikuven.bbut.tracking.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Slf4j
@Component
public class StatisticsService {

    private final ParticipantService participantService;

    @Autowired
    public StatisticsService(ParticipantService participantService) {
        this.participantService = participantService;
    }

    public List<LapTimeStatistics> calculateLapTimeStatistics() {
        return participantService.getActivatedParticipants().stream()
                .map(LapTimeStatistics::of)
                .collect(Collectors.toList());
    }

    public Map<Integer, Long> calculateParticipantsPerLapStatistics() {

        return participantService.getActivatedParticipants().stream()
                .flatMap(participant -> participant.getLaps().stream())
                .filter(lap -> lap.getState().equals(LapState.COMPLETED))
                .map(Lap::getNumber)
                .collect(Collectors.groupingBy(lapNumber -> lapNumber, Collectors.counting()));

    }

    public int getTotalActivatedParticipants() {
        return participantService.getActivatedParticipants().size();
    }

    public Map<String, Long> getAgeDemographics() {

        int UPPER_LIMIT = 79;
        List<String> keys = List.of("0-9", "10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70+");

        return participantService.getAllParticipants().stream()
                .filter(Objects::nonNull)
                .filter(not(participant -> participant.getParticipantState().equals(ParticipantState.NO_SHOW)))
                .map(participant -> calculateAge(participant.getBirthDate()))
                .filter(Objects::nonNull)
                .map(age -> Math.min(age, UPPER_LIMIT) / 10)
                .collect(Collectors.groupingBy(keys::get, Collectors.counting()));
    }

    private Integer calculateAge(LocalDate birthDate) {
        return birthDate != null ? Period.between(birthDate, now()).getYears() : null;
    }

    LocalDate now() {
        return LocalDate.now();
    }

    public Map<ParticipantClass, Long> getClassDemographics() {
        return participantService.getAllParticipants().stream()
                .filter(Objects::nonNull)
                .filter(not(participant -> participant.getParticipantState().equals(ParticipantState.NO_SHOW)))
                .filter(participant -> Objects.nonNull(participant.getParticipantClass()))
                .collect(Collectors.groupingBy(Participant::getParticipantClass, Collectors.counting()));
    }
}
