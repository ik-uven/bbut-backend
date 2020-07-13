package org.ikuven.bbut.tracking.web;

import lombok.Data;
import org.ikuven.bbut.tracking.participant.ParticipantClass;

@Data
public class ParticipantUpdateInput {

    private Long id;
    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private ParticipantClass participantClass;
}
