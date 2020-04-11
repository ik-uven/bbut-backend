package org.ikuven.bbut.tracking.repository;

import org.ikuven.bbut.tracking.sequence.DatabaseSequence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DatabaseSequenceRepository extends MongoRepository<DatabaseSequence, Long> {
}
