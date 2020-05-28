package org.ikuven.bbut.tracking.web;

import lombok.Data;
import org.ikuven.bbut.tracking.participant.LapState;

import java.time.LocalDateTime;

@Data
public class LapInput {
    LocalDateTime registrationTime;
    LapState lapState;
}
