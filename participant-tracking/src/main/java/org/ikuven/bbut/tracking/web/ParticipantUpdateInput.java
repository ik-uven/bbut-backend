package org.ikuven.bbut.tracking.web;

import lombok.Data;
import org.ikuven.bbut.tracking.participant.Gender;

@Data
public class ParticipantUpdateInput {

    private Long id;
    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private Gender gender;
}
