package org.ikuven.bbut.tracking.imports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ikuven.bbut.tracking.participant.Gender;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantService;
import org.ikuven.bbut.tracking.participant.ParticipantState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.ikuven.bbut.tracking.imports.ImportEvent.EventId;

@Slf4j
@RestController
@RequestMapping("/api/participants/imports")
public class ImportsController {

    private final ObjectMapper objectMapper;

    private final ParticipantService participantService;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ImportsController(ObjectMapper objectMapper, ParticipantService participantService, ApplicationEventPublisher eventPublisher) {
        this.objectMapper = objectMapper;
        this.participantService = participantService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Object> importParticipants(@RequestPart("file") List<MultipartFile> multipartFiles) throws IOException {

        if (multipartFiles.size() != 1) {
            String message = String.format("Number of uploaded parts should be 1, was %d.", multipartFiles.size());
            ImportsResult importsResult = ImportsResult.createErrorResult(message);

            log.error(importsResult.toString());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(resultAsJson(importsResult));
        }

        saveIncomingFile(multipartFiles.get(0));

        InputStream uploadedInputStream = multipartFiles.get(0).getInputStream();

        List<Participant> registeredParticipants = new ArrayList<>();
        new BufferedReader(new InputStreamReader(uploadedInputStream, StandardCharsets.UTF_8))
                .lines()
                .filter(Objects::nonNull)
                //.filter(not(String::isEmpty))
                .map(this::prepareParticipant)
                .forEach(participant -> {
                    Participant registered = participantService.registerParticipant(participant);
                    log.info("Imported {} {} {}", registered.getId(), registered.getFirstName(), registered.getLastName());
                    registeredParticipants.add(registered);
                });

        eventPublisher.publishEvent(ImportEvent.of(registeredParticipants, EventId.IMPORTED, "imported participants"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registeredParticipants);
    }

    private void saveIncomingFile(MultipartFile file) throws IOException {
        String path = String.format("participant-tracker-imported-%s-%s", LocalDateTime.now(), file.getOriginalFilename());
        File convertFile = new File(path);
        convertFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(convertFile)) {
            outputStream.write(file.getBytes());
        }

        log.info(String.format("Saved a copy of the import file to %s", path));
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
