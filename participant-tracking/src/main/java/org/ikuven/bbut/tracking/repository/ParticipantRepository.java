package org.ikuven.bbut.tracking.repository;

import org.ikuven.bbut.tracking.participant.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParticipantRepository extends MongoRepository<Participant, Integer> {
}
