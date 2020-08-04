package org.ikuven.bbut.tracking.web;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.ikuven.bbut.tracking.participant.Lap;
import org.ikuven.bbut.tracking.participant.ParticipantClass;
import org.ikuven.bbut.tracking.participant.ParticipantState;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "of")
public class ParticipantDto {

    long id;
    long startNumber;
    String firstName;
    String lastName;
    String club;
    String team;
    ParticipantClass participantClass;
    ParticipantState participantState;
    List<Lap> laps;
}
