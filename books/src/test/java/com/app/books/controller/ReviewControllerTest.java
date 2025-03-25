package com.app.books.controller;

import com.app.books.business.service.ReviewService;
import com.app.books.dto.ReviewUpdateDTO;
import com.app.books.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private Review review;
    private final Long bookId = 1L;
    private final Long reviewId = 1L;
    private final String token = "Bearer mockedToken";

    @BeforeEach
    void setUp() {
        review = new Review();
        review.setId(reviewId);
        review.setBookId(bookId);
        review.setUserId(1L);
        review.setUsername("TestUser");
        review.setContent("Great book!");
        review.setRating(5);
    }

    @Test
    void addReview_ShouldReturnCreatedReview() {
        when(reviewService.addReview(bookId, review, token)).thenReturn(review);

        ResponseEntity<Review> response = reviewController.addReview(bookId, review, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(reviewId, response.getBody().getId());

        verify(reviewService, times(1)).addReview(bookId, review, token);
    }

    @Test
    void getReviewsByBook_ShouldReturnListOfReviews() {
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(List.of(review));

        ResponseEntity<List<Review>> response = reviewController.getReviewsByBook(bookId);

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());

        verify(reviewService, times(1)).getReviewsByBookId(bookId);
    }

    @Test
    void getReviewById_ReviewExists_ShouldReturnReview() {
        when(reviewService.getReviewById(reviewId)).thenReturn(Optional.of(review));

        ResponseEntity<Review> response = reviewController.getReviewById(reviewId);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(reviewId, response.getBody().getId());

        verify(reviewService, times(1)).getReviewById(reviewId);
    }

    @Test
    void getReviewById_ReviewNotFound_ShouldReturnNotFound() {
        when(reviewService.getReviewById(reviewId)).thenReturn(Optional.empty());

        ResponseEntity<Review> response = reviewController.getReviewById(reviewId);

        assertEquals(NOT_FOUND, response.getStatusCode());

        verify(reviewService, times(1)).getReviewById(reviewId);
    }

    @Test
    void deleteReview_ShouldReturnNoContent() {
        doNothing().when(reviewService).deleteReview(reviewId, token);

        ResponseEntity<Void> response = reviewController.deleteReview(reviewId, token);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(reviewService, times(1)).deleteReview(reviewId, token);
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() {
        ReviewUpdateDTO reviewUpdateDTO = new ReviewUpdateDTO();
        reviewUpdateDTO.setContent("Updated Review");
        reviewUpdateDTO.setRating(4);

        when(reviewService.updateReview(reviewId, reviewUpdateDTO, token)).thenReturn(review);

        ResponseEntity<Review> response = reviewController.updateReview(reviewId, reviewUpdateDTO, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(reviewId, response.getBody().getId());

        verify(reviewService, times(1)).updateReview(reviewId, reviewUpdateDTO, token);
    }
}
