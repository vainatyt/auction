package com.project.auction.models;

import jakarta.persistence.*;

@Entity
@Table(name = "trackable_items")
public class TrackableItem {

    @EmbeddedId
    private TrackableItemId id;

    @ManyToOne
    @MapsId("idLot")
    @JoinColumn(name = "id_lot", nullable = false)
    private Lot lot;

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    public TrackableItem() {
    }

    public TrackableItem(Lot lot, User user) {
        this.lot = lot;
        this.user = user;
        this.id = new TrackableItemId(lot.getId(), user.getId());
    }

    public TrackableItemId getId() {
        return id;
    }

    public void setId(TrackableItemId id) {
        this.id = id;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
