package org.ikuven.bbut.tracking.statistics;

import lombok.Data;
import org.ikuven.bbut.tracking.participant.Lap;
import org.ikuven.bbut.tracking.participant.Participant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class LapTimeStatistics {

    private Participant participant;
    private List<LapDuration> lapDurations = new ArrayList<>();
    private int averageLapInMinutes;

    public static LapTimeStatistics of(Participant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Cannot generate statistics for null participant");
        }

        return new LapTimeStatistics(participant);
    }

    private LapTimeStatistics(Participant participant) {
        this.participant = participant;
        this.lapDurations = calculateLapsDuration(participant.getLaps());
        this.averageLapInMinutes = calculateAverageLapTime();
    }

    private List<LapDuration> calculateLapsDuration(List<Lap> laps) {
        return laps.stream()
                .filter(Objects::nonNull)
                .map(this::toLapDuration)
                .collect(Collectors.toList());
    }

    private LapDuration toLapDuration(Lap lap) {
        return LapDuration.of(lap.getNumber(), toDuration(lap.getRegistrationTime()));
    }

    private Duration toDuration(LocalDateTime localDateTime) {
        return Duration.between(LocalTime.of(localDateTime.getHour(), 0), localDateTime);
    }

    private int calculateAverageLapTime() {

        double result = 0;

        if (this.lapDurations != null) {
            result = this.lapDurations.stream()
                    .filter(Objects::nonNull)
                    .map(lapDuration -> lapDuration.getDuration().toSecondsPart() < 29 ? lapDuration.getDuration().truncatedTo(ChronoUnit.MINUTES).toMinutes() : lapDuration.getDuration().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).toMinutes())
                    .mapToInt(Long::intValue)
                    .average()
                    .orElse(0.0d);
        }

        return (int) Math.round(result);
    }
}
