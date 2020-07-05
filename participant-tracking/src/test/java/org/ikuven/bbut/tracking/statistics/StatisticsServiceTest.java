package org.ikuven.bbut.tracking.statistics;

import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.LapState;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    private Participant createParticipant() {
        return Participant.of(2L, "Andie", "Longrunner", "IK Uven", null, Gender.FEMALE, null, ParticipantState.ACTIVE);
    }
}
