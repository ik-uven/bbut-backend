package org.ikuven.bbut.tracking.web;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "of")
public class TeamWrapperDto {
    List<TeamDto> teamDtos;
    long teamMinSize;
}
