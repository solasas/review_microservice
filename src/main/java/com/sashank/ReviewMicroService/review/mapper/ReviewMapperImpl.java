package com.sashank.ReviewMicroService.review.mapper;

import com.sashank.ReviewMicroService.review.Review;
import com.sashank.ReviewMicroService.review.dto.ReviewWithCompanyDTO;
import com.sashank.ReviewMicroService.review.external.Company;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ReviewMapperImpl implements ReviewMapper {
    private static final Logger logger = LoggerFactory.getLogger(ReviewMapperImpl.class);

    @Override
    public ReviewWithCompanyDTO reviewToReviewWithCompanyDTO(Review review, Company company) {
        if (review == null) {
            logger.warn("Review object is null in mapper");
            return null;
        }

        ReviewWithCompanyDTO reviewWithCompanyDTO = new ReviewWithCompanyDTO();

        try {
            // Map review fields
            reviewWithCompanyDTO.setId(review.getId());
            reviewWithCompanyDTO.setTitle(review.getTitle());
            reviewWithCompanyDTO.setDescription(review.getDescription());
            reviewWithCompanyDTO.setRating(review.getRating());
            reviewWithCompanyDTO.setCompanyId(review.getCompanyId());

            // Map company object - log if null
            if (company == null) {
                logger.warn("Company is NULL for review id: {}. Check if Company Microservice is running on port 8081", review.getId());
            } else {
                logger.info("Successfully mapped company: {} for review: {}", company.getId(), review.getId());
            }
            reviewWithCompanyDTO.setCompany(company);

            return reviewWithCompanyDTO;
        } catch (Exception e) {
            logger.error("Error mapping Review to DTO", e);
            throw new RuntimeException("Mapping error: " + e.getMessage(), e);
        }
    }

    @Override
    public Review reviewWithCompanyDTOtoReview(ReviewWithCompanyDTO reviewWithCompanyDTO) {
        if (reviewWithCompanyDTO == null) {
            logger.warn("ReviewWithCompanyDTO object is null in mapper");
            return null;
        }

        try {
            Review review = new Review();

            review.setId(reviewWithCompanyDTO.getId());
            review.setTitle(reviewWithCompanyDTO.getTitle());
            review.setDescription(reviewWithCompanyDTO.getDescription());
            review.setRating(reviewWithCompanyDTO.getRating());
            review.setCompanyId(reviewWithCompanyDTO.getCompanyId());

            logger.info("Successfully mapped DTO to Review for reviewId: {}", reviewWithCompanyDTO.getId());
            return review;
        } catch (Exception e) {
            logger.error("Error mapping DTO to Review", e);
            throw new RuntimeException("Reverse mapping error: " + e.getMessage(), e);
        }
    }
}

