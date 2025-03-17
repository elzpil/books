package com.app.community.business.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class BookServiceClient {

    private final RestTemplate restTemplate;
    @Value("${books.service.url}")
    private String bookExistsUrl;

    public BookServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean doesBookExist(Long bookId) {
        try {
            String url = bookExistsUrl + bookId;

            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

            if (response.getStatusCode().is4xxClientError()) {
                log.error("Book with ID {} not found", bookId);
                return false;
            }

            return true;
        } catch (HttpClientErrorException e) {
            log.error("Error checking if user with ID {} exists: {}", bookId, e.getMessage());
            return false;
        }
    }

}
