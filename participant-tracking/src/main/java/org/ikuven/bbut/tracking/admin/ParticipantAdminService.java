package org.ikuven.bbut.tracking.admin;

import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantEvent;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.participant.ParticipantState;
import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ikuven.bbut.tracking.participant.ParticipantEvent.of;

@Component
public class ParticipantAdminService {

    private final ParticipantRepository repository;
    private final ParticipantService participantService;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ParticipantAdminService(ParticipantRepository repository, ParticipantService participantService, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.participantService = participantService;
        this.eventPublisher = eventPublisher;
    }

    public List<Participant> getAllParticipants() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Participant updateParticipant(Participant participantToUpdate) {
        Participant participant = participantService.getParticipant(participantToUpdate.getId());

        participant.setFirstName(participantToUpdate.getFirstName());
        participant.setLastName(participantToUpdate.getLastName());
        participant.setClub(participantToUpdate.getClub());
        participant.setTeam(participantToUpdate.getTeam());
        participant.setGender(participantToUpdate.getGender());

        Participant savedParticipant = repository.save(participant);

        eventPublisher.publishEvent(of(ParticipantEvent.EventId.CHANGED_PARTICIPANT, savedParticipant, String.format("participantId %d", savedParticipant.getId())));

        return savedParticipant;
    }

    public Participant deleteParticipant(long id) {
        Participant participant = participantService.getParticipant(id);

        if (!participant.getParticipantState().equals(ParticipantState.REGISTERED)) {
            throw new IllegalStateException("Does not allow delete of participants with other state than REGISTERED");
        }

        repository.deleteById(participant.getId());
        eventPublisher.publishEvent(of(ParticipantEvent.EventId.DELETED_PARTICIPANT, participant, String.format("participantId %d", participant.getId())));

        return participant;
    }
}
