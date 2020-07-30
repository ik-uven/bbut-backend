package org.ikuven.bbut.tracking.imports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ikuven.bbut.tracking.participant.Participant;
import org.ikuven.bbut.tracking.participant.ParticipantClass;
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
import java.time.LocalDate;
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
            return createErrorResponse(message, HttpStatus.BAD_REQUEST);
        }

        final var file = multipartFiles.get(0);

        if (file.getOriginalFilename() != null && !file.getOriginalFilename().contains(".csv")) {
            String message = "The import does only accept csv files";
            return createErrorResponse(message, HttpStatus.BAD_REQUEST);
        }

        final var firstLine = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .findFirst()
                .orElseThrow();

        log.warn(firstLine);

        String headerString = "Förnamn;Efternamn;Klubb;Klass;Lagnamn;Födelsedatum";
        if (firstLine == null || !firstLine.endsWith(headerString)) {
            String message = String.format("The first line in file must be: %s", headerString);
            return createErrorResponse(message, HttpStatus.BAD_REQUEST);
        }

        try {
            participantService.removeAllParticipants();
        } catch (IllegalArgumentException e) {
            String message = "Cannot import due to active participants in system";
            return createErrorResponse(message, HttpStatus.FORBIDDEN);
        }

        saveIncomingFile(file);

        InputStream uploadedInputStream = file.getInputStream();

        List<Participant> registeredParticipants = new ArrayList<>();
        new BufferedReader(new InputStreamReader(uploadedInputStream, StandardCharsets.UTF_8))
                .lines()
                .skip(1)
                .filter(Objects::nonNull)
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

    private ResponseEntity<Object> createErrorResponse(String message, HttpStatus status) throws JsonProcessingException {
        ImportsResult importsResult = ImportsResult.createErrorResult(message);

        log.error(importsResult.toString());

        return ResponseEntity
                .status(status)
                .body(resultAsJson(importsResult));
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
        ParticipantClass participantClass = toParticipantClass(candidate[3]);
        String team = capitalizeFirst(candidate[4]);
        LocalDate birthDate = toBirthDate(candidate[5]);

        return Participant.of(0L, firstName, lastName, club, team, participantClass, birthDate, ParticipantState.REGISTERED);
    }

    private ParticipantClass toParticipantClass(String string) {
        ParticipantClass participantClass;

        switch (string) {
            case "Man":
            case "Herrar":
            case "Men":
                participantClass = ParticipantClass.MEN;
                break;
            case "Kvinna":
            case "Damer":
            case "Women":
                participantClass = ParticipantClass.WOMEN;
                break;
            default:
                participantClass = ParticipantClass.UNKNOWN;
                break;
        }

        return participantClass;
    }

    private LocalDate toBirthDate(String string) {
        LocalDate date;
        try {
            date = string != null ? LocalDate.parse(string) : null;
        } catch (Exception e) {
            date = null;
            log.warn("Failed to parse birth date string {}", string, e);
        }

        return date;
    }

    private String resultAsJson(ImportsResult importsResult) throws JsonProcessingException {
        return objectMapper.writeValueAsString(importsResult);
    }

    private String capitalizeFirst(String string) {
        if (string != null && !string.isEmpty()) {
            string = string.trim();
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }
        return "";
    }
}
