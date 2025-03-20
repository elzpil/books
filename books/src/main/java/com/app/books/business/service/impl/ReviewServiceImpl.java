package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.ReviewMapper;
import com.app.books.business.repository.ReviewRepository;
import com.app.books.business.repository.model.ReviewDAO;
import com.app.books.business.service.ReviewService;
import com.app.books.dto.ReviewUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.exception.UnauthorizedException;
import com.app.books.model.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserServiceClient userServiceClient;

    private final JwtTokenUtil jwtTokenUtil;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper,
                             UserServiceClient userServiceClient, JwtTokenUtil jwtTokenUtil) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.userServiceClient = userServiceClient;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Review addReview(Long bookId, Review review, String token) {
        log.info("Adding review for book ID {}: {}", bookId, review);

        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        Optional<ReviewDAO> existingReview = reviewRepository.findByUserIdAndBookId(userId, bookId);

        if (existingReview.isPresent()) {
            throw new IllegalStateException("This user has already reviewed this book");
        }

        review.setUserId(userId);
        review.setBookId(bookId);
        review.setCreatedAt(LocalDateTime.now());

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
    public Review updateReview(Long reviewId, ReviewUpdateDTO reviewUpdateDTO, String token) {
        log.info("Updating review ID {}: {}", reviewId, reviewUpdateDTO);

        ReviewDAO existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));

        // Check if the user is authorized
        if (!isAuthorized(token, existingReview.getUserId())) {
            log.warn("Unauthorized attempt to update review ID {} by user ID {}", reviewId, existingReview.getUserId());
            throw new UnauthorizedException("You are not authorized to update this review");
        }

        if (reviewUpdateDTO.getContent() != null) {
            existingReview.setContent(reviewUpdateDTO.getContent());
        }

        if (reviewUpdateDTO.getRating() != null) {
            existingReview.setRating(reviewUpdateDTO.getRating());
        }

        existingReview.setCreatedAt(LocalDateTime.now());

        ReviewDAO savedReview = reviewRepository.save(existingReview);
        log.info("Successfully updated review ID {}", reviewId);

        return reviewMapper.reviewDAOToReview(savedReview);
    }



    @Override
    public void deleteReview(Long reviewId, String token) {
        log.info("Attempting to delete review ID {}", reviewId);

        ReviewDAO existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));

        // Check if the user is authorized
        if (!isAuthorized(token, existingReview.getUserId())) {
            log.warn("Unauthorized attempt to delete review ID {} by user ID {}", reviewId, existingReview.getUserId());
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        reviewRepository.deleteById(reviewId);
        log.info("Successfully deleted review ID {}", reviewId);
    }


    private boolean isAuthorized(String token, Long userId) {
        String cleanToken = token.replace("Bearer ", "");
        Long tokenUserId = jwtTokenUtil.extractUserId(cleanToken);
        String role = jwtTokenUtil.extractRole(cleanToken);
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }
}
