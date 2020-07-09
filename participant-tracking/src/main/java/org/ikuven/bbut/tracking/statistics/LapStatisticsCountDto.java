package org.ikuven.bbut.tracking.statistics;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "of")
public class LapStatisticsCountDto {

    int totalParticipants;
    List<CountsPerLapDto> countsPerLaps;

}
