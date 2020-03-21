package org.ikuven.bbut.tracking.participant;

import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParticipantLapService {

    private ParticipantRepository repository;

    @Autowired
    public ParticipantLapService(ParticipantRepository repository) {
        this.repository = repository;
    }

    public List<Participant> getAllParticipants() {

        Comparator<? super Participant> participantComparator = new ParticipantLapComparator();

        return repository.findAll(Sort.by(Sort.Direction.ASC, "firstName")).stream()
                .sorted(participantComparator)
                .collect(Collectors.toList());
    }

    public Participant saveLap(int participantId, LocalDateTime finishTime, LapState lapState) {

        Participant participant = repository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No participant found with id %d", participantId)));

        participant.addLap(finishTime, lapState);

        return repository.save(participant);
    }

    private static class ParticipantLapComparator implements Comparator<Participant> {

        @Override
        public int compare(Participant o1, Participant o2) {
            return Integer.compare(latestCompletedLapOrdinal(o2), latestCompletedLapOrdinal(o1));
        }

        private Integer latestCompletedLapOrdinal(Participant participant) {
            return participant.getLaps().stream()
                    .filter(lap -> lap.getLapState().equals(LapState.COMPLETED))
                    .sorted(Comparator.comparingInt(Lap::getNumber).reversed())
                    .limit(1)
                    .map(Lap::getNumber)
                    .findFirst()
                    .orElse(Integer.MIN_VALUE);
        }
    }
}
