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

    // Helper method to check if the user exists
    public boolean doesUserExist(Long userId) {
        try {
            String url = "http://localhost:8082/users/exists/" + userId;
            return restTemplate.getForObject(url, Boolean.class);  // Assuming user service returns a boolean
        } catch (HttpClientErrorException e) {
            log.error("User with ID {} not found", userId);
            return false;
        }
    }
}
