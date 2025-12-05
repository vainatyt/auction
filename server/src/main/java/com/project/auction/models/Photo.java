package com.project.auction.models;

import jakarta.persistence.*;

@Entity
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_good", nullable = false)
    private Goods goods;

    @Column(name = "uuid", columnDefinition = "uuid", nullable = false)
    private java.util.UUID uuid;

    public Photo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public java.util.UUID getUuid() {
        return uuid;
    }

    public void setUuid(java.util.UUID uuid) {
        this.uuid = uuid;
    }
}
