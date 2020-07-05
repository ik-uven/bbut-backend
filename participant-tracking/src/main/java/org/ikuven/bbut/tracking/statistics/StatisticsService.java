package org.ikuven.bbut.tracking.statistics;

import lombok.extern.slf4j.Slf4j;
import org.ikuven.bbut.tracking.participant.Participant;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StatisticsService {

    public LapStatistics calculateLapStatistics(Participant participant) {

        return LapStatistics.of(participant);
    }

}
