package com.app.books.controller;

import com.app.books.business.service.ReviewService;
import com.app.books.dto.ReviewUpdateDTO;
import com.app.books.model.Review;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/books")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable Long bookId, @Valid @RequestBody Review review,
                                            @RequestHeader("Authorization") String token) {
        log.info("Adding review for book ID {}: {}", bookId, review);
        Review createdReview = reviewService.addReview(bookId, review, token);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByBook(@PathVariable Long bookId) {
        log.info("Fetching reviews for book ID: {}", bookId);
        List<Review> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long reviewId) {
        log.info("Fetching review by ID: {}", reviewId);
        return reviewService.getReviewById(reviewId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String token) {
        log.info("Deleting review ID {}  ", reviewId);
        reviewService.deleteReview(reviewId, token);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateDTO reviewUpdateDTO,
            @RequestHeader("Authorization") String token) {

        log.info("Updating review ID {}: {}", reviewId, reviewUpdateDTO);
        Review review = reviewService.updateReview(reviewId, reviewUpdateDTO, token);
        return ResponseEntity.ok(review);
    }

}
