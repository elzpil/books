package com.app.community.business.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    @Value("${users.service.url}")
    private String userExistsUrl;

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean doesUserExist(Long userId) {
        try {
            String url = userExistsUrl + userId;

            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

            if (response.getStatusCode().is4xxClientError()) {
                log.error("User with ID {} not found", userId);
                return false;
            }

            return true;
        } catch (HttpClientErrorException e) {
            log.error("Error checking if user with ID {} exists: {}", userId, e.getMessage());
            return false;
        }
    }

}
