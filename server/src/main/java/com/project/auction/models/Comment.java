package com.project.auction.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comment")
    private Long id;

    // кто написал комментарий
    @ManyToOne
    @JoinColumn(name = "id_commentator", nullable = false)
    private User commentator;

    // кому адресован (владельцу товара/покупателю и т.п.)
    @ManyToOne
    @JoinColumn(name = "id_addressee", nullable = false)
    private User addressee;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review", nullable = false)
    private String review;

    @Column(name = "data", nullable = false)
    private LocalDateTime date;   // поле data в БД → date в Java

    public Comment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCommentator() {
        return commentator;
    }

    public void setCommentator(User commentator) {
        this.commentator = commentator;
    }

    public User getAddressee() {
        return addressee;
    }

    public void setAddressee(User addressee) {
        this.addressee = addressee;
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
