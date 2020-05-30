package org.ikuven.bbut.tracking.imports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.participant.ParticipantState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static java.util.function.Predicate.*;

@RestController
@RequestMapping("/api/participants/imports")
public class ImportsController {

    private final ObjectMapper objectMapper;

    private final ParticipantService participantService;

    @Autowired
    public ImportsController(ObjectMapper objectMapper, ParticipantService participantService) {
        this.objectMapper = objectMapper;
        this.participantService = participantService;
    }

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<String> importParticipants(@RequestPart("file") List<MultipartFile> multipartFiles) throws IOException {

        if (multipartFiles.size() != 1) {
            String message = String.format("Number of uploaded parts should be 1, was %d.", multipartFiles.size());
            ImportsResult importsResult = ImportsResult.createErrorResult(message);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(resultAsJson(importsResult));
        }

        InputStream uploadedInputStream = multipartFiles.get(0).getInputStream();

        new BufferedReader(new InputStreamReader(uploadedInputStream, StandardCharsets.UTF_8))
                .lines()
                .filter(Objects::nonNull)
                .filter(not(String::isEmpty))
                .map(this::prepareParticipant)
                .forEach(participantService::registerParticipant);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(null);
    }

    private Participant prepareParticipant(String line) {
        String[] candidate = line.split(";");
        String firstName = capitalizeFirst(candidate[0]);
        String lastName = capitalizeFirst(candidate[1]);
        String club = capitalizeFirst(candidate[2]);
        String team = capitalizeFirst(candidate[3]);
        Gender gender = candidate[4] != null ? Gender.valueOf(candidate[4]) : Gender.UNKNOWN;
        String town = null; //capitalizeFirst(candidate[5]);
        Integer birthYear = null; //candidate[6] != null ? Integer.valueOf(candidate[6]) : null;

        return Participant.of(0L, firstName, lastName, club, team, gender, birthYear, ParticipantState.REGISTERED);
    }

    private String resultAsJson(ImportsResult importsResult) throws JsonProcessingException {
        return objectMapper.writeValueAsString(importsResult);
    }

    private String capitalizeFirst(String string) {
        return string != null && !string.isEmpty() ? string.substring(0, 1).toUpperCase() + string.substring(1) : null;
    }
}
