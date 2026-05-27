package com.sashank.ReviewMicroService.review.impl;

import com.sashank.ReviewMicroService.review.Review;
import com.sashank.ReviewMicroService.review.ReviewRepository;
import com.sashank.ReviewMicroService.review.ReviewService;
import com.sashank.ReviewMicroService.review.dto.ReviewWithCompanyDTO;
import com.sashank.ReviewMicroService.review.external.Company;
import com.sashank.ReviewMicroService.review.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);
    
    private final ReviewRepository reviewRepository;
    
    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    ReviewMapper reviewMapper;

    @Value("${company.service.url:http://COMPANY-MICROSERVICE}")
    private String companyServiceUrl;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public boolean createReview(Long companyId, Review review) {
        if(companyId != null){
            review.setCompanyId(companyId);
            reviewRepository.save(review);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean createReview(Review review) {
        return createReview(review.getCompanyId(), review);
    }

    @Override
    public boolean updateReview(Review review, Long id) {
        Optional<Review> reviewOptional =reviewRepository.findById(id);
        if(reviewOptional.isPresent()){
            Review reviewToUpdate = reviewOptional.get();

            reviewToUpdate.setTitle(review.getTitle());
            reviewToUpdate.setDescription(review.getDescription());
            reviewToUpdate.setRating(review.getRating());

            reviewRepository.save(reviewToUpdate);
            return true;

        }
        return false;
    }

    @Override
    public boolean deleteReview(Long id) {
        if(reviewRepository.existsById(id)){
            reviewRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ReviewWithCompanyDTO getReviewById(Long id) {
        logger.info("Getting review by id: {}", id);
        try {
            Optional<Review> reviewOptional = reviewRepository.findById(id);
            if(reviewOptional.isPresent()) {
                logger.info("Review found with id: {}", id);
                return convertToDto(reviewOptional.get());
            } else {
                logger.warn("Review not found with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error retrieving review with id: {}", id, e);
            throw new RuntimeException("Failed to retrieve review: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ReviewWithCompanyDTO> getReviewsByCompanyId(Long companyId) {
        logger.info("Getting reviews by company id: {}", companyId);
        try {
            List<Review> reviews = reviewRepository.findByCompanyId(companyId);
            logger.info("Found {} reviews for company: {}", reviews.size(), companyId);
            List<ReviewWithCompanyDTO> reviewWithCompanyDTOS = new ArrayList<>();
            
            for(Review review: reviews){
                try {
                    ReviewWithCompanyDTO dto = convertToDto(review);
                    if (dto != null) {
                        reviewWithCompanyDTOS.add(dto);
                    }
                } catch (Exception e) {
                    logger.error("Error converting review {} to DTO, skipping", review.getId(), e);
                }
            }
            logger.info("Successfully converted {} reviews to DTOs", reviewWithCompanyDTOS.size());
            return reviewWithCompanyDTOS;
        } catch (Exception e) {
            logger.error("Error retrieving reviews for company: {}", companyId, e);
            throw new RuntimeException("Failed to retrieve reviews: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ReviewWithCompanyDTO> getAllReviews() {
        logger.info("Getting all reviews");
        try {
            List<Review> reviews = reviewRepository.findAll();
            logger.info("Found {} reviews in database", reviews.size());
            List<ReviewWithCompanyDTO> reviewWithCompanyDTOS = new ArrayList<>();
            
            for(Review review: reviews){
                try {
                    ReviewWithCompanyDTO dto = convertToDto(review);
                    if (dto != null) {
                        reviewWithCompanyDTOS.add(dto);
                    }
                } catch (Exception e) {
                    logger.error("Error converting review {} to DTO, skipping", review.getId(), e);
                }
            }
            logger.info("Successfully converted {} reviews to DTOs", reviewWithCompanyDTOS.size());
            return reviewWithCompanyDTOS;
        } catch (Exception e) {
            logger.error("Error retrieving all reviews", e);
            throw new RuntimeException("Failed to retrieve reviews: " + e.getMessage(), e);
        }
    }

    private ReviewWithCompanyDTO convertToDto(Review review) {
        if (review == null) {
            logger.warn("Review object is null");
            return null;
        }
        
        logger.info("Converting Review {} to ReviewWithCompanyDTO", review.getId());
        
        Company company = null;
        if (review.getCompanyId() != null) {
            company = fetchCompanyWithRetry(review.getCompanyId(), 3);
        } else {
            logger.warn("Review {} has no companyId", review.getId());
        }
        
        try {
            // Use ReviewMapper to convert Review and Company to ReviewWithCompanyDTO
            ReviewWithCompanyDTO dto = reviewMapper.reviewToReviewWithCompanyDTO(review, company);
            logger.info("Successfully converted Review {} to DTO", review.getId());
            return dto;
        } catch (Exception e) {
            logger.error("Error during mapping Review {} to DTO", review.getId(), e);
            throw new RuntimeException("Failed to convert Review to DTO: " + e.getMessage(), e);
        }
    }
    
    private Company fetchCompanyWithRetry(Long companyId, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                String url = companyServiceUrl + "/companies/" + companyId;
                logger.info("[Attempt {}/{}] Fetching company from URL: {}", attempt, maxAttempts, url);
                
                Company company = restTemplate.getForObject(url, Company.class);
                
                if (company != null) {
                    logger.info("✓ Successfully fetched company: {} - {}", company.getId(), company.getName());
                    return company;
                } else {
                    logger.warn("[Attempt {}/{}] Company returned null from URL: {}", attempt, maxAttempts, url);
                }
            } catch (RestClientException e) {
                logger.error("[Attempt {}/{}] RestClientException for companyId {}: {}", 
                    attempt, maxAttempts, companyId, e.getMessage());
                
                if (attempt < maxAttempts) {
                    try {
                        logger.info("Waiting 1 second before retry...");
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                logger.error("[Attempt {}/{}] Unexpected exception for companyId {}", attempt, maxAttempts, companyId, e);
                
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        logger.error("✗ Failed to fetch company {} after {} attempts", companyId, maxAttempts);
        return null;
    }
}
