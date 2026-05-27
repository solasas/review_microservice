package com.sashank.ReviewMicroService.review.mapper;

import com.sashank.ReviewMicroService.review.Review;
import com.sashank.ReviewMicroService.review.dto.ReviewWithCompanyDTO;
import com.sashank.ReviewMicroService.review.external.Company;

public interface ReviewMapper {
    ReviewWithCompanyDTO reviewToReviewWithCompanyDTO(Review review, Company company);
    Review reviewWithCompanyDTOtoReview(ReviewWithCompanyDTO reviewWithCompanyDTO);
}

