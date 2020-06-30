package org.ikuven.bbut.tracking.participant;

import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.ikuven.bbut.tracking.settings.BackendSettingsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.ikuven.bbut.tracking.participant.ParticipantEvent.of;

@Component
public class ParticipantService {

    private final ParticipantRepository repository;

    private final BackendSettingsProperties backendSettingsProperties;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ParticipantService(ParticipantRepository repository, BackendSettingsProperties backendSettingsProperties, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.backendSettingsProperties = backendSettingsProperties;
        this.eventPublisher = eventPublisher;
    }

    public List<Participant> getAllParticipants() {

        return repository.findAll(Sort.by(Sort.Direction.ASC, "firstName")).stream()
                .sorted(onNumberOfLaps()
                        .thenComparing(Participant::getParticipantState)
                        .thenComparing(participant -> participant.getLastLapState().ordinal())
                )
                .collect(Collectors.toList());
    }

    public List<Team> getAllTeams() {

        Map<String, Team> teams = new LinkedHashMap<>();

        repository.findAll(Sort.by(Sort.Direction.ASC, "team", "firstName")).stream()
                .filter(Objects::nonNull)
                .filter(participant -> participant.getTeam() != null && !participant.getTeam().isEmpty())
                .forEach(participant -> {
                    teams.computeIfAbsent(participant.getTeam(), Team::of);
                    teams.get(participant.getTeam()).getParticipants().add(participant);
                });

        teams.values()
                .forEach(team -> team.getParticipants()
                        .sort(Comparator.comparing(participant -> participant.getLaps().size(), Comparator.reverseOrder())));

        return new ArrayList<>(teams.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(Team::getTotalCompletedLaps, Comparator.reverseOrder())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new)
                )
                .values());

    }

    private Comparator<Participant> onNumberOfLaps() {
        return Comparator.comparing(participant -> participant.getLaps().size(), Comparator.reverseOrder());
    }

    public Participant getParticipant(long participantId) {
        return repository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No participant found with id %d", participantId)));
    }

    public Participant registerParticipant(String firstName, String lastName, String club, String team, Gender gender, Integer birthYear) {
        Participant participant = repository.save(Participant.of(0L, firstName, lastName, club, team, gender, birthYear, ParticipantState.REGISTERED));

        eventPublisher.publishEvent(of(ParticipantEvent.EventId.REGISTERED, participant, String.format("participantId %d", participant.getId())));

        return participant;
    }

    public Participant registerParticipant(Participant participant) {
        participant.setId(0L);
        participant.setParticipantState(ParticipantState.REGISTERED);
        Participant savedParticipant = repository.save(participant);

        eventPublisher.publishEvent(of(ParticipantEvent.EventId.ADDED_PARTICIPANT, savedParticipant, String.format("participantId %d", savedParticipant.getId())));

        return savedParticipant;
    }

    public Participant setState(long participantId, ParticipantState participantState) {
        Participant participant = getParticipant(participantId);
        participant.setParticipantState(participantState);

        Participant savedParticipant = repository.save(participant);

        eventPublisher.publishEvent(of(ParticipantEvent.EventId.CHANGED_STATE, savedParticipant, String.format("state: %s", participantState)));

        return savedParticipant;
    }

    public Participant saveLap(long participantId, LapState lapState, ClientOrigin clientOrigin) {
        return saveLap(participantId, LocalDateTime.now(), lapState, clientOrigin);
    }

    public Participant saveLap(long participantId, LocalDateTime registrationTime, LapState lapState, ClientOrigin clientOrigin) {

        Participant participant = getParticipant(participantId);

        if (ClientOrigin.WEB.equals(clientOrigin) || !lapAlreadyRegistered(participant.getLaps(), registrationTime)) {
            participant.addLap(registrationTime, lapState);

            if (LapState.OVERDUE.equals(lapState)) {
                participant.setParticipantState(ParticipantState.RESIGNED);
            }

            participant = repository.save(participant);

            eventPublisher.publishEvent(of(ParticipantEvent.EventId.SAVED_LAP, participant, String.format("lapNumber: %d lapState: %s", participant.getLastLap().getNumber(), participant.getLastLap().getState())));
        }

        return participant;
    }

    private boolean lapAlreadyRegistered(List<Lap> laps, LocalDateTime registrationTime) {

        boolean alreadyRegistered = false;

        Optional<Lap> lastLap = laps.stream()
                .reduce((first, second) -> second);

        if (lastLap.isPresent()) {
            long gracePeriod = backendSettingsProperties.getLaps().getRegistrationGracePeriod();
            alreadyRegistered = registrationTime.isBefore(lastLap.get().getRegistrationTime().plusMinutes(gracePeriod));
        }

        return alreadyRegistered;
    }

    public Participant deleteLap(long participantId, Integer lapNumber) {

        Participant participant = getParticipant(participantId);

        if (participant.getLaps().size() > 0) {
            participant.getLaps().remove(lapNumber - 1);
            participant = repository.save(participant);

            eventPublisher.publishEvent(of(ParticipantEvent.EventId.DELETED_LAP, participant, String.format("lapNumber %d", lapNumber)));
        }

        return participant;
    }

    public Participant updateLapState(long participantId, Integer lapNumber, LapState lapState) {

        Participant participant = getParticipant(participantId);
        participant.updateLapState(lapState, lapNumber);

        Participant savedParticipant = repository.save(participant);

        eventPublisher.publishEvent(of(ParticipantEvent.EventId.CHANGED_LAP_STATE, savedParticipant, String.format("lapNumber: %d  lapState: %s", lapNumber, lapState)));

        return savedParticipant;
    }
}
