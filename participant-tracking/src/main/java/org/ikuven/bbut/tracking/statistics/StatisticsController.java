package org.ikuven.bbut.tracking.statistics;

import org.ikuven.bbut.tracking.participant.Lap;
import org.ikuven.bbut.tracking.participant.LapState;
import org.ikuven.bbut.tracking.participant.ParticipantService;
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

    @GetMapping(path = "/lapcounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LapStatisticsCountDto> getCountsPerLap() {
        List<CountsPerLapDto> countsPerLapDtos = new ArrayList<>();

        int totalParticipants = participantService.getAllQualifiedParticipants().size();

        participantService.getAllQualifiedParticipants().stream()
                .flatMap(participant -> participant.getLaps().stream())
                .filter(lap -> lap.getState().equals(LapState.COMPLETED))
                .map(Lap::getNumber)
                .collect(Collectors.groupingBy(lapNumber -> lapNumber, Collectors.counting()))
                .forEach((lapNumber, count) -> countsPerLapDtos.add(toCountsPerLapDto(lapNumber, count)));

        return ResponseEntity.ok(LapStatisticsCountDto.of(totalParticipants, countsPerLapDtos));
    }

    private CountsPerLapDto toCountsPerLapDto(Integer lapNumber, Long count) {
        return CountsPerLapDto.of(lapNumber, count);
    }

    private LapStatisticsDto toDto(LapStatistics lapStatistics) {
        return LapStatisticsDto.of(lapStatistics);
    }
}
