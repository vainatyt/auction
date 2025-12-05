package com.project.auction.models;

import java.io.Serializable;
import java.util.Objects;

public class TrackableItemId implements Serializable {

    private Long idLot;
    private Long idUser;

    public TrackableItemId() {
    }

    public TrackableItemId(Long idLot, Long idUser) {
        this.idLot = idLot;
        this.idUser = idUser;
    }

    public Long getIdLot() {
        return idLot;
    }

    public void setIdLot(Long idLot) {
        this.idLot = idLot;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackableItemId)) return false;
        TrackableItemId that = (TrackableItemId) o;
        return Objects.equals(idLot, that.idLot) &&
               Objects.equals(idUser, that.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLot, idUser);
    }
}
