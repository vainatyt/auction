package com.project.auction.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment {

    @EmbeddedId
    private CommentId id;
    @Column(name = "rating")
    private Integer rating;
    @Column(name = "review")
    private String review;
    @Column(name = "data")
    private Instant date;

    public Comment() {
    }

    public Comment(CommentId id,
                   Integer rating, String review,Instant date) {
        this.id = id;
        this.rating = rating;
        this.review = review;
        this.date = date;
    }

    public CommentId getId() {
        return id;
    }

    public void setId(CommentId id) {
        this.id = id;
    }

    public Long getCommentatorId() {
        return id.getCommentatorId();
    }
    
    public Long getAddresseeId() {
        return id.getAddresseeId();
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

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }
}
