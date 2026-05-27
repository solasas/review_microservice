package com.sashank.ReviewMicroService.review;

import com.sashank.ReviewMicroService.review.dto.ReviewWithCompanyDTO;
import java.util.List;

public interface ReviewService {
    boolean createReview(Long companyId, Review review);
    boolean createReview(Review review);
    boolean updateReview(Review review,Long id);
    boolean deleteReview(Long id);
    ReviewWithCompanyDTO getReviewById(Long id);
    List<ReviewWithCompanyDTO> getReviewsByCompanyId(Long companyId);
    List<ReviewWithCompanyDTO> getAllReviews();
}
