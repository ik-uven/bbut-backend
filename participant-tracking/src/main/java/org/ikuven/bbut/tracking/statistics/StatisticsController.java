package org.ikuven.bbut.tracking.statistics;

import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/participants/statistics")
public class StatisticsController {

    private final ParticipantService participantService;
    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(ParticipantService participantService, StatisticsService statisticsService) {
        this.participantService = participantService;
        this.statisticsService = statisticsService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LapStatisticsDto>> getStatistics() {
        List<LapStatisticsDto> lapStatisticsDtos = participantService.getAllQualifiedParticipants().stream()
                .map(statisticsService::calculateLapStatistics)
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lapStatisticsDtos);
    }

    private LapStatisticsDto toDto(LapStatistics lapStatistics) {
        return LapStatisticsDto.of(lapStatistics);
    }
}
