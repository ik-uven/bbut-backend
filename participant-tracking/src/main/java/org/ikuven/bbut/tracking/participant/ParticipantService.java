package org.ikuven.bbut.tracking.participant;

import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParticipantService {

    private ParticipantRepository repository;

    @Autowired
    public ParticipantService(ParticipantRepository repository) {
        this.repository = repository;
    }

    public List<Participant> getAllParticipants() {

        Comparator<? super Participant> participantComparator = new ParticipantLapComparator();

        return repository.findAll(Sort.by(Sort.Direction.ASC, "participantState", "firstName")).stream()
                .sorted(participantComparator)
                .collect(Collectors.toList());
    }

    public Participant getParticipant(long participantId) {
        return repository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No participant found with id %d", participantId)));
    }

    public Participant registerParticipant(String firstName, String lastName, String team) {
        return repository.save(Participant.of(0L, firstName, lastName, team, ParticipantState.NOT_STARTED, Collections.emptyList()));
    }

    public Participant setState(long participantId, ParticipantState participantState) {
        Participant participant = getParticipant(participantId);
        participant.setParticipantState(participantState);

        return repository.save(participant);
    }

    public Participant saveLap(long participantId, LocalDateTime finishTime, LapState lapState) {

        Participant participant = getParticipant(participantId);
        participant.addLap(finishTime, lapState);

        return repository.save(participant);
    }

    public Participant deleteLap(long participantId, Integer lapNumber) {

        Participant participant = getParticipant(participantId);
        participant.getLaps().remove(lapNumber - 1);

        return repository.save(participant);
    }

    public Participant updateLapState(long participantId, Integer lapNumber, LapState lapState) {

        Participant participant = getParticipant(participantId);
        participant.getLaps().stream()
                .filter(lap -> lap.getNumber() == lapNumber)
                .findFirst()
                .ifPresent(lap -> lap.setState(lapState));

        return repository.save(participant);
    }

    private static class ParticipantLapComparator implements Comparator<Participant> {

        @Override
        public int compare(Participant o1, Participant o2) {
            return Integer.compare(latestCompletedLapOrdinal(o2), latestCompletedLapOrdinal(o1));
        }

        private Integer latestCompletedLapOrdinal(Participant participant) {
            return participant.getLaps().stream()
                    .filter(lap -> lap.getState().equals(LapState.COMPLETED))
                    .sorted(Comparator.comparingInt(Lap::getNumber).reversed())
                    .limit(1)
                    .map(Lap::getNumber)
                    .findFirst()
                    .orElse(Integer.MIN_VALUE);
        }
    }
}
