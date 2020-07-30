package org.ikuven.bbut.tracking.participant;

import lombok.extern.slf4j.Slf4j;
import org.ikuven.bbut.tracking.repository.DatabaseSequenceRepository;
import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.ikuven.bbut.tracking.settings.BackendSettingsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.ikuven.bbut.tracking.participant.ParticipantEvent.of;

@Slf4j
@Component
public class ParticipantService {

    private final ParticipantRepository repository;

    private final DatabaseSequenceRepository sequenceRepository;

    private final BackendSettingsProperties backendSettingsProperties;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ParticipantService(ParticipantRepository repository, DatabaseSequenceRepository sequenceRepository, BackendSettingsProperties backendSettingsProperties, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.sequenceRepository = sequenceRepository;
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

    public List<Participant> getActivatedParticipants() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "firstName")).stream()
                .filter(participant -> participant.getParticipantState().equals(ParticipantState.ACTIVE) || participant.getParticipantState().equals(ParticipantState.RESIGNED))
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

    public Participant registerParticipant(String firstName, String lastName, String club, String team, ParticipantClass participantClass, LocalDate birthDate) {
        return registerParticipant(Participant.of(0L, firstName, lastName, club, team, participantClass, birthDate, ParticipantState.REGISTERED));
    }
    public Participant registerParticipant(Participant participant) {

        participant.setId(0L);
        participant.setParticipantState(ParticipantState.REGISTERED);
        Participant savedParticipant = repository.save(participant);

        log.debug("Registered participant {}", participant);

        eventPublisher.publishEvent(of(ParticipantEvent.Type.REGISTERED_PARTICIPANT, savedParticipant, "Registered participant"));

        return savedParticipant;
    }

    public Participant setState(long participantId, ParticipantState participantState) {
        Participant participant = getParticipant(participantId);
        participant.setParticipantState(participantState);

        Participant savedParticipant = repository.save(participant);

        log.debug("Changed state of participant {}", savedParticipant);

        eventPublisher.publishEvent(of(ParticipantEvent.Type.CHANGED_STATE, savedParticipant, String.format("state: %s", participantState)));

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

            log.debug("Saved lap for participant {}", participant);

            eventPublisher.publishEvent(of(ParticipantEvent.Type.SAVED_LAP, participant, String.format("lapNumber: %d lapState: %s", participant.getLastLap().getNumber(), participant.getLastLap().getState())));
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

            log.debug("Deleted lap number {} for participant {}", lapNumber, participant);

            eventPublisher.publishEvent(of(ParticipantEvent.Type.DELETED_LAP, participant, String.format("lapNumber %d", lapNumber)));
        }

        return participant;
    }

    public Participant updateLapState(long participantId, Integer lapNumber, LapState lapState) {

        Participant participant = getParticipant(participantId);
        participant.updateLapState(lapState, lapNumber);

        Participant savedParticipant = repository.save(participant);

        log.debug("Updated lap state for lap number {} to {} for participant {}", lapNumber, lapState, participant);

        eventPublisher.publishEvent(of(ParticipantEvent.Type.CHANGED_LAP_STATE, savedParticipant, String.format("lapNumber: %d  lapState: %s", lapNumber, lapState)));

        return savedParticipant;
    }

    public void removeAllParticipants() {

        if (getActivatedParticipants().size() > 0) {
            throw new IllegalArgumentException("Will not remove all when there are participants in states ACTIVE or RESIGNED");
        }

        repository.deleteAll();
        sequenceRepository.deleteAll();
    }
}
