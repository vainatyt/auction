package com.project.auction.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lots")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lot")
    private Long id;

    @Column(name = "start_auction", nullable = false)
    private LocalDateTime startAuction;

    @Column(name = "end_auction", nullable = false)
    private LocalDateTime endAuction;

    @Column(name = "current_cost", nullable = false)
    private BigDecimal currentCost;

    @Column(name = "rate_step", nullable = false)
    private BigDecimal rateStep;

    @ManyToOne
    @JoinColumn(name = "id_buyer")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "id_good", nullable = false)
    private Goods goods;


    // пустой конструктор обязателен для JPA
    public Lot() {
    }

    // при желании можешь добавить конструктор со всеми полями

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartAuction() {
        return startAuction;
    }

    public void setStartAuction(LocalDateTime startAuction) {
        this.startAuction = startAuction;
    }

    public LocalDateTime getEndAuction() {
        return endAuction;
    }

    public void setEndAuction(LocalDateTime endAuction) {
        this.endAuction = endAuction;
    }

    public BigDecimal getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(BigDecimal currentCost) {
        this.currentCost = currentCost;
    }

    public BigDecimal getRateStep() {
        return rateStep;
    }

    public void setRateStep(BigDecimal rateStep) {
        this.rateStep = rateStep;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}
