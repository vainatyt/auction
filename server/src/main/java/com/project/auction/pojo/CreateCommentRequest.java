package com.project.auction.payload.request;

public class CreateCommentRequest {

    private Long addresseeId;
    private Integer rating;
    private String review;

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
}
