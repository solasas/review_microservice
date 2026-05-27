package com.sashank.ReviewMicroService.review.dto;

import com.sashank.ReviewMicroService.review.Review;
import com.sashank.ReviewMicroService.review.external.Company;

public class ReviewWithCompanyDTO {
    private Long id;
    private String title;
    private String description;
    private int rating;
    private Long companyId;
    private Company company;

    // Default constructor
    public ReviewWithCompanyDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}

