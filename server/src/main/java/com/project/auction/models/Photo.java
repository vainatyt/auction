package com.project.auction.models;

import java.util.UUID;

public class Photo {

    private Long id;        // id в таблице, если есть автоинкремент PK
    private Long goodId;    // колонка id_good
    private UUID uuid;      // колонка uuid

    public Photo() {
    }

    public Photo(Long id, Long goodId, UUID uuid) {
        this.id = id;
        this.goodId = goodId;
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
