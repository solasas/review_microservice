package com.sashank.ReviewMicroService.review;

import java.util.List;

public interface ReviewService {
    boolean createReview(Long companyId, Review review);
    boolean createReview(Review review);
    boolean updateReview(Review review,Long id);
    boolean deleteReview(Long id);
    Review getReviewById(Long id);
    List<Review> getReviewsByCompanyId(Long companyId);
    List<Review> getAllReviews();
}
