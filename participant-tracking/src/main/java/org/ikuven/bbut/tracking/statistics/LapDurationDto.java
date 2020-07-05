package org.ikuven.bbut.tracking.statistics;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class LapDurationDto {
    int lapNumber;
    long minutes;
    long seconds;
    long roundedInMinutes;
}
