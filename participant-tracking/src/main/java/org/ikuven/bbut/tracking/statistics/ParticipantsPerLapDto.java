package org.ikuven.bbut.tracking.statistics;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class ParticipantsPerLapDto {
    Integer lap;
    Long participants;
}
