package com.project.auction.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trakable_items")
public class TrackableItem {
    @Id
    private Long id;
    @Column(name = "id_lot")
    private Long lotId;
    @Column(name = "id_user")
    private Long userId;

    public TrackableItem() {
    }

    public TrackableItem(Long lotId, Long userId) {
        this.lotId = lotId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
