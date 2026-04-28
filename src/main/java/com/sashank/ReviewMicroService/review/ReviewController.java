package com.sashank.ReviewMicroService.review;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<String> createReview(@RequestParam(required = false) Long companyId, @RequestBody Review review){
        // Allow companyId to be provided either as query param or inside the request body (companyId field)
        Long finalCompanyId = companyId != null ? companyId : review.getCompanyId();
        if(finalCompanyId == null){
            return new ResponseEntity<>("companyId is required either as query parameter or inside the request body", HttpStatus.BAD_REQUEST);
        }

        // basic validation for rating
        int rating = review.getRating();
        if(rating < 1 || rating > 5){
            return new ResponseEntity<>("rating must be an integer between 1 and 5", HttpStatus.BAD_REQUEST);
        }

        boolean created = reviewService.createReview(finalCompanyId, review);
        if(created){
            return new ResponseEntity<>("Review Added Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to create review", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviewsByCompany(@RequestParam Long companyId){
        List<Review> reviews = reviewService.getReviewsByCompanyId(companyId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviews(){
        List<Review> reviews = reviewService.getAllReviews();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id){
        Review review = reviewService.getReviewById(id);
        if(review != null){
            return new ResponseEntity<>(review, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateReview(@PathVariable Long id, @RequestBody Review review){
        boolean updated = reviewService.updateReview(review, id);
        if(updated){
            return new ResponseEntity<>("Review Updated Successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Review Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id){
        boolean isDeleted = reviewService.deleteReview(id);
        if(isDeleted){
            return new ResponseEntity<>("Review Deleted Successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Review Not Found", HttpStatus.NOT_FOUND);
        }
    }
}
