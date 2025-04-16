package com.app.books.business.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean doesUserExist(Long userId) {
        try {
            String url = "http://localhost:8082/users/exists/" + userId;
            return restTemplate.getForObject(url, Boolean.class);
        } catch (HttpClientErrorException e) {
            log.error("User with ID {} not found", userId);
            return false;
        }
    }
    public String getUserEmail(Long userId) {
        try {
            String url = "http://localhost:8082/users/" + userId + "/email";
            return restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            log.error("Failed to get email for user with ID {}: {}", userId, e.getMessage());
            return null;
        }
    }


}
