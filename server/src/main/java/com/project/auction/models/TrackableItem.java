package com.project.auction.models;

public class TrackableItem {

    private Long lotId;   // id_lot
    private Long userId;  // id_user

    public TrackableItem() {
    }

    public TrackableItem(Long lotId, Long userId) {
        this.lotId = lotId;
        this.userId = userId;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
