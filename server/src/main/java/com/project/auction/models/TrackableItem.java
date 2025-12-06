package com.project.auction.models;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("trakable_items")
public class TrackableItem {

    @Column("id_lot")
    private Long lotId;
    @Column("id_user")
    private Long userId;

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
