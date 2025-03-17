package com.app.books.business.service;

import com.app.books.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review addReview(Long bookId, Review review);
    List<Review> getReviewsByBookId(Long bookId);
    Optional<Review> getReviewById(Long reviewId);
    Review updateReview(Long reviewId, Review review, Long userId);
    void deleteReview(Long reviewId, Long userId, boolean isAdmin);
}
