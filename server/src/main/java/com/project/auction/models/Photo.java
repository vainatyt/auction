package com.project.auction.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("photo")
public class Photo {
    @Id
    @Column("id_photo")
    private Long id;       // id в таблице, если есть автоинкремент PK
    @Column("id_good")
    private Long goodId;    // колонка id_good
    @Column("uuid")
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
