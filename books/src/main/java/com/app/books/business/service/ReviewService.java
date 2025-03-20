package com.app.books.business.service;

import com.app.books.dto.ReviewUpdateDTO;
import com.app.books.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review addReview(Long bookId, Review review);
    List<Review> getReviewsByBookId(Long bookId);
    Optional<Review> getReviewById(Long reviewId);
    Review updateReview(Long reviewId, ReviewUpdateDTO reviewUpdateDTO, String token);
    void deleteReview(Long reviewId, String token);
}
