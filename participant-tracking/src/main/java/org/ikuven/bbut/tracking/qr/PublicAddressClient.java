package org.ikuven.bbut.tracking.qr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public class PublicAddressClient {

    private final WebClient client;

    public PublicAddressClient() {
        // This is just needed for displaying extended response debug logging when
        // logging.level.reactor.netty.http.client=DEBUG is set in application.yml
        HttpClient httpClient = HttpClient
                .create()
                .wiretap(true);

        String url = "http://ident.me";
        this.client = WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(url)
                .build();
    }

    public String publicIp() {
        return this.client
                .get()
                .exchange()
                .onErrorReturn(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .flatMap(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class);
                    } else {
                        return response.bodyToMono(Void.class).then(Mono.empty());
                    }
                })
                .block();
    }
}
