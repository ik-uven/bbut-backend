package org.ikuven.bbut.tracking.statistics;

import org.ikuven.bbut.tracking.participant.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    public static final LocalDateTime REG_TIME = LocalDateTime.of(2020, 8, 8, 12, 0, 0);

    @Mock
    private ParticipantService participantService;

    @Spy
    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    @DisplayName("should return correct durations for the given laps")
    void correctDurations() {

        Participant participant = createParticipant();

        participant.addLap(REG_TIME.plusMinutes(35), LapState.COMPLETED);
        participant.addLap(REG_TIME.plusHours(1).plusMinutes(45), LapState.COMPLETED);
        participant.addLap(REG_TIME.plusHours(1).plusMinutes(50), LapState.COMPLETED);

        when(participantService.getActivatedParticipants()).thenReturn(List.of(participant));

        List<LapTimeStatistics> lapTimeStatistics = statisticsService.calculateLapTimeStatistics();

        assertThat(lapTimeStatistics)
                .isNotNull()
                .hasSize(1);

        assertThat(lapTimeStatistics.get(0).getLapDurations())
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

        when(participantService.getActivatedParticipants()).thenReturn(List.of(participant));

        List<LapTimeStatistics> lapTimeStatistics = statisticsService.calculateLapTimeStatistics();

        assertThat(lapTimeStatistics.get(0).getAverageLapInMinutes())
                .isEqualTo(44);
    }

    @Test
    @DisplayName("should return correct lapcount")
    void correctLapCount() {
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

        when(participantService.getActivatedParticipants()).thenReturn(List.of(participant1, participant2));

        Map<Integer, Long> lapCounts = statisticsService.calculateParticipantsPerLapStatistics();

        assertThat(lapCounts)
                .containsExactly(
                        entry(1, 2L),
                        entry(2, 2L),
                        entry(3, 2L),
                        entry(4, 1L),
                        entry(5, 1L),
                        entry(6, 1L)
                );
    }

    @Test
    @DisplayName("should return age count categorized by age span")
    void ageDemographics() {
        Participant participant1 = createParticipant();
        participant1.setBirthDate(LocalDate.of(1990, 1, 1));
        Participant participant2 = createParticipant();
        participant2.setBirthDate(LocalDate.of(1999, 1, 1));
        Participant participant3 = createParticipant();
        participant3.setBirthDate(LocalDate.of(1980, 1, 1));
        Participant participant4 = createParticipant();
        participant4.setBirthDate(LocalDate.of(1985, 1, 1));
        Participant participant5 = createParticipant();
        participant5.setBirthDate(LocalDate.of(1989, 1, 1));

        when(participantService.getAllParticipants()).thenReturn(List.of(participant1, participant2, participant3, participant4, participant5));
        when(statisticsService.now()).thenReturn(LocalDate.of(2020, 1, 1));

        Map<String, Long> ageDemographics = statisticsService.getAgeDemographics();

        assertThat(ageDemographics)
                .containsExactly(
                        entry("20-29",1L),
                        entry("30-39",3L),
                        entry("40-49",1L)
                );
    }

    private Participant createParticipant() {
        return Participant.of(2L, 2, "Andie", "Longrunner", "IK Uven", null, ParticipantClass.WOMEN, null, ParticipantState.ACTIVE);
    }
}
