package com.app.community.business.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BookServiceClient {

    private final WebClient webClient;

    @Value("${books.service.url}")
    private String bookExistsUrl;

    public BookServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public boolean doesBookExist(Long bookId, String token) {
        try {
            String url = bookExistsUrl + bookId;
            log.info("Checking if book exists with ID: {}", bookId);

            Boolean exists = webClient.get()
                    .uri(url)
                    .headers(headers -> headers.setBearerAuth(token.replace("Bearer ", ""))) // Add token if required
                    .retrieve()
                    .bodyToMono(Boolean.class) // Expect a boolean response
                    .doOnNext(response -> log.info("Received response: {}", response))
                    .doOnError(error -> log.error("Error in WebClient call: {}", error.getMessage()))
                    .onErrorResume(error -> {
                        log.error("Book check failed for ID {}: {}", bookId, error.getMessage());
                        return Mono.just(false); // Return false only if the request actually fails
                    })
                    .block(); // Blocking call for synchronous behavior

            log.info("Book existence check result for ID {}: {}", bookId, exists);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Unexpected error checking book ID {}: {}", bookId, e.getMessage());
            return false;
        }
    }
}
