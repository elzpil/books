package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.ReviewMapper;
import com.app.books.business.repository.ReviewRepository;
import com.app.books.business.repository.model.ReviewDAO;
import com.app.books.dto.ReviewUpdateDTO;
import com.app.books.exception.UnauthorizedException;
import com.app.books.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewServiceImpl(reviewRepository, reviewMapper, jwtTokenUtil);
    }

    @Test
    void addReview_ShouldReturnCreatedReview() {
        Long bookId = 1L;
        String token = "Bearer validToken";
        Review review = new Review();
        review.setBookId(bookId);
        review.setContent("Great book!");
        review.setRating(5);

        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.setBookId(bookId);
        reviewDAO.setContent("Great book!");
        reviewDAO.setRating(5);

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);
        when(reviewRepository.findByUserIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(reviewRepository.save(any(ReviewDAO.class))).thenReturn(reviewDAO);
        when(reviewMapper.reviewToReviewDAO(any(Review.class))).thenReturn(reviewDAO);
        when(reviewMapper.reviewDAOToReview(any(ReviewDAO.class))).thenReturn(review);

        Review createdReview = reviewService.addReview(bookId, review, token);

        assertNotNull(createdReview);
        assertEquals("Great book!", createdReview.getContent());
        assertEquals(5, createdReview.getRating());
        verify(reviewRepository, times(1)).save(any(ReviewDAO.class));
    }

    @Test
    void addReview_ShouldThrowIllegalStateException_WhenReviewAlreadyExists() {
        Long bookId = 1L;
        String token = "Bearer validToken";
        Review review = new Review();
        review.setBookId(bookId);
        review.setContent("Great book!");
        review.setRating(5);

        ReviewDAO existingReviewDAO = new ReviewDAO();
        existingReviewDAO.setBookId(bookId);
        existingReviewDAO.setContent("Great book!");
        existingReviewDAO.setRating(5);

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);
        when(reviewRepository.findByUserIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(existingReviewDAO));

        assertThrows(IllegalStateException.class, () -> reviewService.addReview(bookId, review, token));
    }

    @Test
    void getReviewsByBookId_ShouldReturnReviews() {
        Long bookId = 1L;
        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.setBookId(bookId);
        reviewDAO.setContent("Great book!");
        reviewDAO.setRating(5);

        Review review = new Review();
        review.setBookId(bookId);
        review.setContent("Great book!");
        review.setRating(5);

        when(reviewRepository.findByBookId(bookId)).thenReturn(List.of(reviewDAO));
        when(reviewMapper.reviewDAOToReview(any(ReviewDAO.class))).thenReturn(review);

        List<Review> reviews = reviewService.getReviewsByBookId(bookId);

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals("Great book!", reviews.get(0).getContent());
    }

    @Test
    void getReviewById_ShouldReturnReview() {
        Long reviewId = 1L;
        ReviewDAO reviewDAO = new ReviewDAO();
        reviewDAO.setId(reviewId);
        reviewDAO.setContent("Great book!");
        reviewDAO.setRating(5);

        Review review = new Review();
        review.setId(reviewId);
        review.setContent("Great book!");
        review.setRating(5);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(reviewDAO));
        when(reviewMapper.reviewDAOToReview(any(ReviewDAO.class))).thenReturn(review);

        Optional<Review> result = reviewService.getReviewById(reviewId);

        assertTrue(result.isPresent());
        assertEquals("Great book!", result.get().getContent());
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() {

        Long reviewId = 1L;
        String token = "Bearer validToken";
        ReviewUpdateDTO updateDTO = new ReviewUpdateDTO();
        updateDTO.setContent("Updated review content");
        updateDTO.setRating(4);

        ReviewDAO existingReviewDAO = new ReviewDAO();
        existingReviewDAO.setId(reviewId);
        existingReviewDAO.setContent("Great book!");
        existingReviewDAO.setRating(5);
        existingReviewDAO.setUserId(1L);

        ReviewDAO updatedReviewDAO = new ReviewDAO();
        updatedReviewDAO.setId(reviewId);
        updatedReviewDAO.setContent("Updated review content");
        updatedReviewDAO.setRating(4);
        updatedReviewDAO.setUserId(1L);

        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        updatedReview.setContent("Updated review content");
        updatedReview.setRating(4);

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReviewDAO));
        when(reviewRepository.save(any(ReviewDAO.class))).thenReturn(updatedReviewDAO);
        when(reviewMapper.reviewDAOToReview(any(ReviewDAO.class))).thenReturn(updatedReview);

        Review result = reviewService.updateReview(reviewId, updateDTO, token);

        assertNotNull(result);
        assertEquals("Updated review content", result.getContent());
        assertEquals(4, result.getRating());
    }

    @Test
    void updateReview_ShouldThrowUnauthorizedException_WhenUserIsNotAuthorized() {

        Long reviewId = 1L;
        String token = "Bearer invalidToken";
        ReviewUpdateDTO updateDTO = new ReviewUpdateDTO();
        updateDTO.setContent("Updated review content");
        updateDTO.setRating(4);

        ReviewDAO existingReviewDAO = new ReviewDAO();
        existingReviewDAO.setId(reviewId);
        existingReviewDAO.setUserId(2L);

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReviewDAO));

        assertThrows(UnauthorizedException.class, () -> reviewService.updateReview(reviewId, updateDTO, token));
    }

    @Test
    void deleteReview_ShouldDeleteReview() {
        Long reviewId = 1L;
        String token = "Bearer validToken";
        ReviewDAO existingReviewDAO = new ReviewDAO();
        existingReviewDAO.setId(reviewId);
        existingReviewDAO.setUserId(1L);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReviewDAO));
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);

        reviewService.deleteReview(reviewId, token);

        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    void deleteReview_ShouldThrowUnauthorizedException_WhenUserIsNotAuthorized() {
        Long reviewId = 1L;
        String token = "Bearer invalidToken";
        ReviewDAO existingReviewDAO = new ReviewDAO();
        existingReviewDAO.setId(reviewId);
        existingReviewDAO.setUserId(2L);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReviewDAO));
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);

        assertThrows(UnauthorizedException.class, () -> reviewService.deleteReview(reviewId, token));
    }
}
