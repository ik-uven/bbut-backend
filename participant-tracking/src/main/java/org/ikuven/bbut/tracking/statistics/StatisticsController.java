package org.ikuven.bbut.tracking.statistics;

import org.ikuven.bbut.tracking.participant.ParticipantClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @GetMapping(path = "/age", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AgeStatisticsDto>> getAgeDemographics() {

        return ResponseEntity
                .ok(toAgeStatisticsDto(statisticsService.getAgeDemographics()));
    }

    @GetMapping(path = "/class", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ClassStatisticsDto>> getClassDemographics() {

        return ResponseEntity
                .ok(toClassStatisticsDto(statisticsService.getClassDemographics()));
    }

    private List<ClassStatisticsDto> toClassStatisticsDto(Map<ParticipantClass, Long> classDemographics) {
        List<ClassStatisticsDto> classStatisticsDtos = new ArrayList<>();

        int total = classDemographics.values().stream()
                .mapToInt(Long::intValue)
                .sum();

        classDemographics.forEach(((participantClass, count) -> classStatisticsDtos.add(ClassStatisticsDto.of(participantClass.toString(), count, calculatePercentage(total, count)))));
        return classStatisticsDtos;
    }

    private Long calculatePercentage(int total, Long count) {
        double ratio = count.doubleValue() / total;
        return new BigDecimal(Double.toString(ratio))
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    private List<AgeStatisticsDto> toAgeStatisticsDto(Map<String, Long> ageDemographics) {
        List<AgeStatisticsDto> ageStatisticsDtos = new ArrayList<>();
        ageDemographics.forEach((ageSpan, count) -> ageStatisticsDtos.add(AgeStatisticsDto.of(ageSpan, count)));

        return ageStatisticsDtos;
    }

    private ParticipantsPerLapDto toParticipantsPerLapDto(Integer lapNumber, Long count) {
        return ParticipantsPerLapDto.of(lapNumber, count);
    }

    private LapTimeStatisticsDto toLapTimeStatisticsDto(LapTimeStatistics lapTimeStatistics) {
        return LapTimeStatisticsDto.of(lapTimeStatistics);
    }
}
