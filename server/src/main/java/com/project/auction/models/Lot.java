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

    @Column(name = "id_buyer")
    private Long buyerId;

    @Column(name = "id_good", nullable = false)
    private Long goodId;

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

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }
}
