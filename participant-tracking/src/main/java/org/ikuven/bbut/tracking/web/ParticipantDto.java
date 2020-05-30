package org.ikuven.bbut.tracking.web;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.Lap;
import org.ikuven.bbut.tracking.participant.ParticipantState;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "of")
public class ParticipantDto {

    private long id;
    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private Gender gender;
    private ParticipantState participantState;
    private List<Lap> laps;
}
