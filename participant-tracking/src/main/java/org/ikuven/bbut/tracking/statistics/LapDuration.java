package org.ikuven.bbut.tracking.statistics;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Duration;

@Value
@AllArgsConstructor(staticName = "of")
public class LapDuration {
    int lapNumber;
    Duration duration;
}
