package org.ikuven.bbut.tracking.statistics;

import lombok.extern.slf4j.Slf4j;
import org.ikuven.bbut.tracking.participant.Lap;
import org.ikuven.bbut.tracking.participant.LapState;
import org.ikuven.bbut.tracking.participant.Participant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StatisticsService {

    public LapStatistics calculateLapStatistics(Participant participant) {

        return LapStatistics.of(participant);
    }

    public Map<Integer, Long> calculateLapCounts(List<Participant> participants) {

        return participants.stream()
                .flatMap(participant -> participant.getLaps().stream())
                .filter(lap -> lap.getState().equals(LapState.COMPLETED))
                .map(Lap::getNumber)
                .collect(Collectors.groupingBy(lapNumber -> lapNumber, Collectors.counting()));

    }

}
