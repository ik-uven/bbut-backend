package org.ikuven.bbut.tracking.statistics;

import lombok.Data;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LapStatisticsDto {

    private long participantId;
    private String firstName;
    private String lastName;
    private String club;
    private String team;
    private List<LapDurationDto> lapDurations;
    private int averageLapInMinutes;

    private LapStatisticsDto(long participantId, String firstName, String lastName, String club, String team, List<LapDuration> lapDurations, int averageLapInMinutes) {
        this.participantId = participantId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.team = team;
        this.lapDurations = toLapDurationDto(lapDurations);
        this.averageLapInMinutes = averageLapInMinutes;
    }

    public static LapStatisticsDto of(LapStatistics lapStatistics) {
        return new LapStatisticsDto(
                lapStatistics.getParticipant().getId(),
                lapStatistics.getParticipant().getFirstName(),
                lapStatistics.getParticipant().getLastName(),
                lapStatistics.getParticipant().getClub(),
                lapStatistics.getParticipant().getTeam(),
                lapStatistics.getLapDurations(),
                lapStatistics.getAverageLapInMinutes()
        );
    }

    private List<LapDurationDto> toLapDurationDto(List<LapDuration> lapDurations) {
        return lapDurations.stream()
                .map(lapDuration -> LapDurationDto.of(lapDuration.getLapNumber(), lapDuration.getDuration().toMinutesPart(), lapDuration.getDuration().toSecondsPart(), round(lapDuration.getDuration())))
                .collect(Collectors.toList());
    }

    private long round(Duration duration) {
        return duration.toSecondsPart() < 29 ? duration.truncatedTo(ChronoUnit.MINUTES).toMinutes() : duration.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).toMinutes();
    }
}
