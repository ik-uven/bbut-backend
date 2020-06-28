package org.ikuven.bbut.tracking.admin;

import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParticipantAdminService {

    private final ParticipantRepository repository;
    private final ParticipantService participantService;

    @Autowired
    public ParticipantAdminService(ParticipantRepository repository, ParticipantService participantService) {
        this.repository = repository;
        this.participantService = participantService;
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

        return repository.save(participant);
    }
}
