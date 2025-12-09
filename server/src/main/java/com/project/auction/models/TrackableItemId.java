package com.project.auction.models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TrackableItemId implements Serializable{
    @Column(name = "id_lot")
    private Long lotId;
    @Column(name = "id_user")
    private Long userId;

    TrackableItemId(){}

    TrackableItemId(Long lotId, long userId){
        this.lotId = lotId;
        this.userId = userId;
    }

    public Long getLotId(){
        return lotId;
    }

    public void setLotId(Long lotId){
        this.lotId = lotId;
    }

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackableItemId that = (TrackableItemId) o;
        return Objects.equals(lotId, that.lotId) &&
               Objects.equals(userId, that.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(lotId, userId);
    }
}
