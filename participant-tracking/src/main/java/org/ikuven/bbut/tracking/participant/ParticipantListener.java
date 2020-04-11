package org.ikuven.bbut.tracking.participant;

import org.ikuven.bbut.tracking.sequence.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class ParticipantListener extends AbstractMongoEventListener<Participant> {

    private final SequenceGeneratorService sequenceGenerator;

    @Autowired
    public ParticipantListener(SequenceGeneratorService sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Participant> event) {
        if (event.getSource().getId() < 1) {
            event.getSource().setId(sequenceGenerator.generateSequence(Participant.SEQUENCE_NAME));
        }
    }
}
