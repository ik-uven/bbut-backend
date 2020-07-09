package org.ikuven.bbut.tracking.statistics;

import org.ikuven.bbut.tracking.participant.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    public static final LocalDateTime REG_TIME = LocalDateTime.of(2020, 8, 8, 12, 0, 0);

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    @DisplayName("should return correct durations for the given laps")
    void correctDurations() {

        Participant participant = createParticipant();

        participant.addLap(REG_TIME.plusMinutes(35), LapState.COMPLETED);
        participant.addLap(REG_TIME.plusHours(1).plusMinutes(45), LapState.COMPLETED);
        participant.addLap(REG_TIME.plusHours(1).plusMinutes(50), LapState.COMPLETED);

        LapStatistics lapStatistics = statisticsService.calculateLapStatistics(participant);

        assertThat(lapStatistics)
                .isNotNull();

        assertThat(lapStatistics.getLapDurations())
                .containsExactly(
                        LapDuration.of(1, Duration.of(35, ChronoUnit.MINUTES)),
                        LapDuration.of(2, Duration.of(45, ChronoUnit.MINUTES)),
                        LapDuration.of(3, Duration.of(50, ChronoUnit.MINUTES))
                );
    }

    @Test
    @DisplayName("should return correct average for the given laps")
    void correctAverage() {

        Participant participant = createParticipant();

        participant.addLap(REG_TIME.plusMinutes(35), LapState.COMPLETED);
        participant.addLap(REG_TIME.plusHours(1).plusMinutes(45), LapState.COMPLETED);
        participant.addLap(REG_TIME.plusHours(1).plusMinutes(51), LapState.COMPLETED);

        LapStatistics lapStatistics = statisticsService.calculateLapStatistics(participant);

        assertThat(lapStatistics.getAverageLapInMinutes())
                .isEqualTo(44);
    }

    @Test
    void name() {
        Participant participant1 = createParticipant();
        participant1.addLap(null, LapState.COMPLETED);
        participant1.addLap(null, LapState.COMPLETED);
        participant1.addLap(null, LapState.COMPLETED);
        participant1.addLap(null, LapState.OVERDUE);
        Participant participant2 = createParticipant();
        participant2.addLap(null, LapState.COMPLETED);
        participant2.addLap(null, LapState.COMPLETED);
        participant2.addLap(null, LapState.COMPLETED);
        participant2.addLap(null, LapState.COMPLETED);
        participant2.addLap(null, LapState.COMPLETED);
        participant2.addLap(null, LapState.COMPLETED);

        List<Participant> participants = List.of(participant1, participant2);

        Map<Integer, Long> counts =
                participants.stream()
                        .flatMap(participant -> participant.getLaps().stream())
                        .filter(lap -> lap.getState().equals(LapState.COMPLETED))
                        .map(Lap::getNumber)
                        .collect(Collectors.groupingBy(lapNumber -> lapNumber, Collectors.counting()));

        System.out.println(counts);
    }

    private Participant createParticipant() {
        return Participant.of(2L, "Andie", "Longrunner", "IK Uven", null, Gender.FEMALE, null, ParticipantState.ACTIVE);
    }
}
