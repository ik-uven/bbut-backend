package org.ikuven.bbut.tracking.web;

import lombok.Data;

@Data
public class ParticipantInput {

    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private String gender;
    private Integer birthYear;
}
