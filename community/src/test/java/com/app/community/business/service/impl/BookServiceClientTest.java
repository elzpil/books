package com.app.community.business.service.impl;

import com.app.community.business.service.impl.BookServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;

class BookServiceClientTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private BookServiceClient bookServiceClient;

    @Value("${books.service.url}")
    private String bookExistsUrl = "http://localhost:8080/api/books/";


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        bookServiceClient = new BookServiceClient(webClientBuilder);

        // Use reflection to set the private field
        Field field = BookServiceClient.class.getDeclaredField("bookExistsUrl");
        field.setAccessible(true);
        field.set(bookServiceClient, bookExistsUrl);
    }
    @Test
    void testDoesBookExist_Success() {
        // Given
        Long bookId = 1L;
        String token = "Bearer valid_token";
        Boolean exists = true; // Simulating that the book exists

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(exists));

        // When
        boolean result = bookServiceClient.doesBookExist(bookId, token);

        // Then
        assertTrue(result);
        verify(webClient, times(1)).get();
    }

    @Test
    void testDoesBookExist_BookNotFound() {
        // Given
        Long bookId = 1L;
        String token = "Bearer valid_token";
        Boolean exists = false; // Simulating that the book does not exist

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(exists));

        // When
        boolean result = bookServiceClient.doesBookExist(bookId, token);

        // Then
        assertFalse(result);
        verify(webClient, times(1)).get();
    }

    @Test
    void testDoesBookExist_WebClientError() {
        // Given
        Long bookId = 1L;
        String token = "Bearer valid_token";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.error(new RuntimeException("WebClient error")));

        // When
        boolean result = bookServiceClient.doesBookExist(bookId, token);

        // Then
        assertFalse(result); // Expecting false because WebClient error occurs
        verify(webClient, times(1)).get();
    }

    @Test
    void testDoesBookExist_ExceptionThrown() {
        // Given
        Long bookId = 1L;
        String token = "Bearer valid_token";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException("Unexpected error"));

        // When
        boolean result = bookServiceClient.doesBookExist(bookId, token);

        // Then
        assertFalse(result); // Expecting false due to unexpected error
        verify(webClient, times(1)).get();
    }
}
