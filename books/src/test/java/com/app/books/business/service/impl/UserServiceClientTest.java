package com.app.books.business.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserServiceClient userServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userServiceClient = new UserServiceClient(restTemplate);
    }

    @Test
    void doesUserExist_ShouldReturnTrue_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        String url = "http://localhost:8082/users/exists/" + userId;

        when(restTemplate.getForObject(url, Boolean.class)).thenReturn(true);

        // Act
        boolean result = userServiceClient.doesUserExist(userId);

        // Assert
        assertTrue(result);
    }

    @Test
    void doesUserExist_ShouldReturnFalse_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        String url = "http://localhost:8082/users/exists/" + userId;

        when(restTemplate.getForObject(url, Boolean.class)).thenReturn(false);

        // Act
        boolean result = userServiceClient.doesUserExist(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void doesUserExist_ShouldReturnFalse_WhenHttpClientErrorExceptionOccurs() {
        // Arrange
        Long userId = 1L;
        String url = "http://localhost:8082/users/exists/" + userId;

        when(restTemplate.getForObject(url, Boolean.class)).thenThrow(HttpClientErrorException.class);

        // Act
        boolean result = userServiceClient.doesUserExist(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void getUserEmail_ShouldReturnEmail_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        String expectedEmail = "user@example.com";
        String url = "http://localhost:8082/users/" + userId + "/email";

        when(restTemplate.getForObject(url, String.class)).thenReturn(expectedEmail);

        // Act
        String result = userServiceClient.getUserEmail(userId);

        // Assert
        assertEquals(expectedEmail, result);
    }

    @Test
    void getUserEmail_ShouldReturnNull_WhenHttpClientErrorExceptionOccurs() {
        // Arrange
        Long userId = 1L;
        String url = "http://localhost:8082/users/" + userId + "/email";

        when(restTemplate.getForObject(url, String.class)).thenThrow(HttpClientErrorException.class);

        // Act
        String result = userServiceClient.getUserEmail(userId);

        // Assert
        assertNull(result);
    }
}
