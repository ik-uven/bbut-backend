package org.ikuven.bbut.tracking.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ImportsResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportsResult.class);

    private boolean valid;
    private List<String> messages;

    public static ImportsResult createErrorResult(String message) {

        return new ImportsResult(false, Collections.singletonList(message));
    }

    public ImportsResult() {
        this.valid = true;
    }

    public ImportsResult(boolean valid, List<String> messages) {
        this.valid = valid;
        this.messages = messages;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getMessages() {
        if (Objects.isNull(messages)) {
            messages = new ArrayList<>();
        }
        return messages;
    }
}
