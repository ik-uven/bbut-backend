package org.ikuven.bbut.tracking.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/participants/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LapTimeStatisticsDto>> getLapTimeStatistics() {
        List<LapTimeStatisticsDto> lapTimeStatisticsDtos = statisticsService.calculateLapTimeStatistics().stream()
                .map(this::toLapTimeStatisticsDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lapTimeStatisticsDtos);
    }

    @GetMapping(path = "/completedlaps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LapStatisticsCountDto> getParticipantsPerLapStatistics() {
        List<ParticipantsPerLapDto> participantsPerLapDtos = new ArrayList<>();

        int totalActivatedParticipants = statisticsService.getTotalActivatedParticipants();

        statisticsService.calculateParticipantsPerLapStatistics()
                    .forEach((lapNumber, count) -> participantsPerLapDtos.add(toParticipantsPerLapDto(lapNumber, count)));

        return ResponseEntity.ok(LapStatisticsCountDto.of(totalActivatedParticipants, participantsPerLapDtos));
    }

    private ParticipantsPerLapDto toParticipantsPerLapDto(Integer lapNumber, Long count) {
        return ParticipantsPerLapDto.of(lapNumber, count);
    }

    private LapTimeStatisticsDto toLapTimeStatisticsDto(LapTimeStatistics lapTimeStatistics) {
        return LapTimeStatisticsDto.of(lapTimeStatistics);
    }
}
