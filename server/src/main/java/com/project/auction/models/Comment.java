package com.project.auction.models;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "comments")
public class Comment {

    @Column("id_commentator")
    private Long commentatorId; // id_commentator
    @Column("id_addressee")
    private Long addresseeId;   // id_addressee
    @Column("rating")
    private Integer rating;
    @Column("review")
    private String review;
    @Column("data")
    private LocalDateTime date;

    public Comment() {
    }

    public Comment(Long commentatorId, Long addresseeId,
                   Integer rating, String review, LocalDateTime date) {
        this.commentatorId = commentatorId;
        this.addresseeId = addresseeId;
        this.rating = rating;
        this.review = review;
        this.date = date;
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
