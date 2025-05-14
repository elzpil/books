package com.app.community.business.service.impl;

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

        Field field = BookServiceClient.class.getDeclaredField("bookExistsUrl");
        field.setAccessible(true);
        field.set(bookServiceClient, bookExistsUrl);
    }
    @Test
    void testDoesBookExist_Success() {

        Long bookId = 1L;
        String token = "Bearer valid_token";
        Boolean exists = true;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(exists));

        boolean result = bookServiceClient.doesBookExist(bookId, token);

        assertTrue(result);
        verify(webClient, times(1)).get();
    }

    @Test
    void testDoesBookExist_BookNotFound() {
        Long bookId = 1L;
        String token = "Bearer valid_token";
        Boolean exists = false;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(exists));

        boolean result = bookServiceClient.doesBookExist(bookId, token);

        assertFalse(result);
        verify(webClient, times(1)).get();
    }

    @Test
    void testDoesBookExist_WebClientError() {
        Long bookId = 1L;
        String token = "Bearer valid_token";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.error(new RuntimeException("WebClient error")));

        boolean result = bookServiceClient.doesBookExist(bookId, token);
        assertFalse(result);
        verify(webClient, times(1)).get();
    }

    @Test
    void testDoesBookExist_ExceptionThrown() {
        Long bookId = 1L;
        String token = "Bearer valid_token";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException("Unexpected error"));

        boolean result = bookServiceClient.doesBookExist(bookId, token);
        assertFalse(result);
        verify(webClient, times(1)).get();
    }
}
