package com.app.books.business.service.impl;

import com.app.books.business.mapper.ReviewMapper;
import com.app.books.business.repository.ReviewRepository;
import com.app.books.business.repository.model.ReviewDAO;
import com.app.books.business.service.ReviewService;
import com.app.books.model.Review;
import com.app.books.business.service.impl.UserServiceClient;  // Import UserServiceClient
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserServiceClient userServiceClient;  // Add UserServiceClient

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper, UserServiceClient userServiceClient) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Review addReview(Long bookId, Review review) {
        log.info("Adding review for book ID {}: {}", bookId, review);

        // Check if the user exists using UserServiceClient
        if (!userServiceClient.doesUserExist(review.getUserId())) {
            throw new IllegalArgumentException("User with ID " + review.getUserId() + " does not exist");
        }

        Optional<ReviewDAO> existingReview = reviewRepository.findByUserIdAndBookId(review.getUserId(), review.getBookId());

        if (existingReview.isPresent()) {
            throw new IllegalStateException("This user has already reviewed this book");
        }

        review.setBookId(bookId);
        ReviewDAO savedReview = reviewRepository.save(reviewMapper.reviewToReviewDAO(review));
        log.info("Successfully added review with ID: {}", savedReview.getId());
        return reviewMapper.reviewDAOToReview(savedReview);
    }

    @Override
    public List<Review> getReviewsByBookId(Long bookId) {
        log.info("Fetching reviews for book ID: {}", bookId);
        return reviewRepository.findByBookId(bookId).stream()
                .map(reviewMapper::reviewDAOToReview)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Review> getReviewById(Long reviewId) {
        log.info("Fetching review by ID: {}", reviewId);
        return reviewRepository.findById(reviewId).map(reviewMapper::reviewDAOToReview);
    }

    @Transactional
    @Override
    public Review updateReview(Long reviewId, Review review, Long userId) {
        log.info("Updating review ID {} by user ID {}: {}", reviewId, userId, review);

        // Check if the user exists using UserServiceClient
        if (!userServiceClient.doesUserExist(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        Optional<ReviewDAO> existingReview = reviewRepository.findById(reviewId);

        if (existingReview.isPresent()) {
            ReviewDAO updatedReview = existingReview.get();

            if (!updatedReview.getUserId().equals(userId)) {
                log.warn("Unauthorized attempt to update review ID {} by user ID {}", reviewId, userId);
                throw new RuntimeException("Unauthorized to update this review");
            }

            updatedReview.setContent(review.getContent());
            updatedReview.setRating(review.getRating());
            ReviewDAO savedReview = reviewRepository.save(updatedReview);
            log.info("Successfully updated review ID {}", reviewId);
            return reviewMapper.reviewDAOToReview(savedReview);
        }

        log.error("Review not found with ID: {}", reviewId);
        throw new RuntimeException("Review not found");
    }

    @Override
    public void deleteReview(Long reviewId, Long userId, boolean isAdmin) {
        log.info("Attempting to delete review ID {} by user ID {} (Admin: {})", reviewId, userId, isAdmin);

        if (!userServiceClient.doesUserExist(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        Optional<ReviewDAO> existingReview = reviewRepository.findById(reviewId);

        if (existingReview.isPresent()) {
            if (!existingReview.get().getUserId().equals(userId) && !isAdmin) {
                log.warn("Unauthorized attempt to delete review ID {} by user ID {}", reviewId, userId);
                throw new RuntimeException("Unauthorized to delete this review");
            }

            reviewRepository.deleteById(reviewId);
            log.info("Successfully deleted review ID {}", reviewId);
        } else {
            log.error("Review not found with ID: {}", reviewId);
            throw new RuntimeException("Review not found");
        }
    }
}
