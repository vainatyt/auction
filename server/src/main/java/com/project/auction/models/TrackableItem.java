package com.project.auction.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "trackable_items")
public class TrackableItem {
    @EmbeddedId
    private TrackableItemId id = new TrackableItemId();

    public TrackableItem() {
    }

    public TrackableItem(Long lotId, Long userId) {
        this.id = new TrackableItemId(); 
        id.setLotId(lotId);
        id.setUserId(userId);
    }

    public Long getLotId() {
        return id.getLotId();
    }

    public void setLotId(Long lotId) {
        id.setLotId(lotId);
    }

    public Long getUserId() {
        return id.getUserId();
    }

    public void setUserId(Long userId) {
        id.setUserId(userId);
    }
}
