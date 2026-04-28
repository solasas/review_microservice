package com.sashank.ReviewMicroService.review.impl;

import com.sashank.ReviewMicroService.review.Review;
import com.sashank.ReviewMicroService.review.ReviewRepository;
import com.sashank.ReviewMicroService.review.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

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

            reviewToUpdate.setContent(review.getContent());
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
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);

    }

    @Override
    public List<Review> getReviewsByCompanyId(Long companyId) {
        return reviewRepository.findByCompanyId(companyId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
