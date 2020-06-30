package org.ikuven.bbut.tracking.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.ParticipantState;

@Data
@AllArgsConstructor(staticName = "of")
public class ParticipantAdminDto {

    private long id;
    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private Gender gender;
    private ParticipantState participantState;
}
