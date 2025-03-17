package com.app.books.business.mapper;

import com.app.books.business.repository.model.ReviewDAO;
import com.app.books.model.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review reviewDAOToReview(ReviewDAO reviewDAO);
    ReviewDAO reviewToReviewDAO(Review review);
}
