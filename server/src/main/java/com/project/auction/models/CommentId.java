package com.project.auction.models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CommentId implements Serializable {
    @Column(name = "id_commentator")
    private Long commentatorId;
    
    @Column(name = "id_addressee")
    private Long addresseeId;
    
    public CommentId() {}

    public CommentId(Long commentatorId, Long addresseeId) {
        this.commentatorId = commentatorId;
        this.addresseeId = addresseeId;
    }

    public Long getCommentatorId(){
        return commentatorId;
    }

    public void setCommentatorId(Long commentatorId){
        this.commentatorId = commentatorId;
    }

    public Long getAddresseeId(){
        return addresseeId;
    }

    public void setAddresseeId(Long addresseeId){
        this.addresseeId = addresseeId;
    }
    
    // equals() и hashCode() ОБЯЗАТЕЛЬНЫ!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentId that = (CommentId) o;
        return Objects.equals(commentatorId, that.commentatorId) &&
               Objects.equals(addresseeId, that.addresseeId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(commentatorId, addresseeId);
    }
}
