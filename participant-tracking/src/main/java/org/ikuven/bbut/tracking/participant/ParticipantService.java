package org.ikuven.bbut.tracking.participant;

import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ParticipantService {

    private ParticipantRepository repository;

    @Autowired
    public ParticipantService(ParticipantRepository repository) {
        this.repository = repository;
    }

    public List<Participant> getAllParticipants() {

        Comparator<? super Participant> latestCompletedLapComparator = new LatestCompletedLapComparator();

        return repository.findAll(Sort.by(Sort.Direction.ASC, "participantState", "firstName")).stream()
                .sorted(latestCompletedLapComparator)
                .sorted(Comparator.comparing(participant -> participant.getLastLapState().ordinal()))
                .collect(Collectors.toList());
    }

    public Participant getParticipant(long participantId) {
        return repository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No participant found with id %d", participantId)));
    }

    public Participant registerParticipant(String firstName, String lastName, String team) {
        return repository.save(Participant.of(0L, firstName, lastName, team, ParticipantState.REGISTERED));
    }

    public Participant setState(long participantId, ParticipantState participantState) {
        Participant participant = getParticipant(participantId);
        participant.setParticipantState(participantState);

        return repository.save(participant);
    }

    public Participant saveLap(long participantId, LocalDateTime finishTime, LapState lapState) {

        Participant participant = getParticipant(participantId);
        participant.addLap(finishTime, lapState);

        if (LapState.OVERDUE.equals(lapState)) {
            participant.setParticipantState(ParticipantState.RESIGNED);
        }

        return repository.save(participant);
    }

    public Participant deleteLap(long participantId, Integer lapNumber) {

        Participant participant = getParticipant(participantId);
        participant.getLaps().remove(lapNumber - 1);

        return repository.save(participant);
    }

    public Participant updateLapState(long participantId, Integer lapNumber, LapState lapState) {

        Participant participant = getParticipant(participantId);
        participant.updateLapState(lapState, lapNumber);

        return repository.save(participant);
    }

    static class LatestCompletedLapComparator implements Comparator<Participant> {

        @Override
        public int compare(Participant o1, Participant o2) {
            return Integer.compare(latestCompletedLapOrdinal(o2), latestCompletedLapOrdinal(o1));
        }

        private Integer latestCompletedLapOrdinal(Participant participant) {
            return participant.getLaps().stream()
                    .sorted(Comparator.comparingInt(Lap::getNumber).reversed())
                    .limit(1)
                    .map(Lap::getNumber)
                    .findFirst()
                    .orElse(Integer.MIN_VALUE);
        }
    }
}
