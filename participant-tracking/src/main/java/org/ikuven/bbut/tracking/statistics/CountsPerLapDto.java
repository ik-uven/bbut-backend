package org.ikuven.bbut.tracking.statistics;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class CountsPerLapDto {
    Integer lap;
    Long participants;
}
