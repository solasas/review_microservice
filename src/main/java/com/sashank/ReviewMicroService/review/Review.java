package com.sashank.ReviewMicroService.review;

import jakarta.persistence.*;

@Entity
@Table(name="review-table")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private int rating;
    private Long companyId;

    public Review(Long id, String content, int rating) {
        this.id = id;
        this.content = content;
        this.rating = rating;
    }
    public Review(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
