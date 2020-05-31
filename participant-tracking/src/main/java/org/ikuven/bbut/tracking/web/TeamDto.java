package org.ikuven.bbut.tracking.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ikuven.bbut.tracking.participant.Participant;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class TeamDto {

    private String name;
    private List<Participant> participants;
    private int totalLaps;
}
