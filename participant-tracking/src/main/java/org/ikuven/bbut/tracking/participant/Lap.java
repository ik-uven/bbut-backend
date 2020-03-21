package org.ikuven.bbut.tracking.participant;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@AllArgsConstructor(staticName = "of")
public class Lap {

    int number;
    LocalDateTime finishTime;
    LapState lapState;
}
