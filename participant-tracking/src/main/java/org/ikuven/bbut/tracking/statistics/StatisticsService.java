package org.ikuven.bbut.tracking.statistics;

import lombok.extern.slf4j.Slf4j;
import org.ikuven.bbut.tracking.participant.Lap;
import org.ikuven.bbut.tracking.participant.LapState;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
}
