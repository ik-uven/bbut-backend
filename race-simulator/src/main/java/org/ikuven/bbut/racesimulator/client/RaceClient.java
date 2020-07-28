package org.ikuven.bbut.racesimulator.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RaceClient {

    private final WebClient client;

    public RaceClient( @Value("${tracking-app.url}") String url) {
        log.info(String.format("Setting up client with base url %s", url));

        this.client = WebClient
                .builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", url))
                .build();
    }

    public List<Participant> getActiveParticipants() {
        return getAllParticipants().stream()
                .filter(participant -> participant.getParticipantState().equals("ACTIVE"))
                .collect(Collectors.toList());
    }

    public List<Participant> getAllParticipants() {
        return client
                .get()
                .uri("/api/participants")
                .exchange()
                .block()
                .bodyToFlux(Participant.class)
                .collectList()
                .block();
    }

    public void activate(Participant participant) {
        registerState(participant.getId(), "ACTIVE");
    }

    public void addLap(long id, String lapState) {
        LapInput lapInput = LapInput.of("", lapState);

        ClientResponse response = client
                .put()
                .uri(String.format("/api/participants/%d/laps", id))
                .body(BodyInserters.fromValue(lapInput))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("x-client-origin", "web")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .block();

        if (response != null) {
            log.info(response.statusCode().toString());
        }
    }

    public void resignParticipant(Long id) {
        registerState(id, "RESIGNED");
    }

    private void registerState(long id, String state) {
        ClientResponse response = client
                .put()
                .uri(String.format("/api/participants/%d/states/%s", id, state))
                .exchange()
                .block();

        if (response != null) {
            log.info(response.statusCode().toString());
        }
    }
}
