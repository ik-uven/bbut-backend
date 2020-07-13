package org.ikuven.bbut.tracking.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ikuven.bbut.tracking.participant.ParticipantClass;
import org.ikuven.bbut.tracking.participant.ParticipantState;

@Data
@AllArgsConstructor(staticName = "of")
public class ParticipantAdminDto {

    private long id;
    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private ParticipantClass participantClass;
    private ParticipantState participantState;
}
