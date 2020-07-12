package org.ikuven.bbut.racesimulator.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class LapInput {
    String registrationTime;
    String lapState;
}
