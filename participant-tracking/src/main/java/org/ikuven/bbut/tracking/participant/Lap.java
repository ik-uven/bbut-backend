package org.ikuven.bbut.tracking.participant;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(staticName = "of")
public class Lap {

    int number;
    LocalDateTime registrationTime;
    LapState state;
}
