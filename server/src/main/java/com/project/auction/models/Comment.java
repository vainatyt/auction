package com.project.auction.models;

import java.time.LocalDateTime;

public class Comment {

    private Long id;
    private Long commentatorId; // id_commentator
    private Long addresseeId;   // id_addressee
    private Integer rating;
    private String review;
    private LocalDateTime date;

    public Comment() {
    }

    public Comment(Long id, Long commentatorId, Long addresseeId,
                   Integer rating, String review, LocalDateTime date) {
        this.id = id;
        this.commentatorId = commentatorId;
        this.addresseeId = addresseeId;
        this.rating = rating;
        this.review = review;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommentatorId() {
        return commentatorId;
    }

    public void setCommentatorId(Long commentatorId) {
        this.commentatorId = commentatorId;
    }

    public Long getAddresseeId() {
        return addresseeId;
    }

    public void setAddresseeId(Long addresseeId) {
        this.addresseeId = addresseeId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
