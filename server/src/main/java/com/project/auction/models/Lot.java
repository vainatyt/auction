package com.project.auction.models;


import com.project.auction.pojo.CreateLotRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "lots")
public class    Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lot")
    private Long id;

    @Column(name = "start_auction")
    private LocalDateTime startAuction;

    @Column(name = "end_auction")
    private LocalDateTime endAuction;

    @Column(name = "current_cost")
    private BigDecimal currentCost;

    @Column(name = "rate_step")
    private BigDecimal rateStep;

    @Column(name = "id_buyer")
    private Long buyerId;

    @Column(name = "id_owner")
    private Long ownerId;

    public Lot(){
    }

    public Lot(Long owId, CreateLotRequest lot) {
        startAuction = lot.getStartAuction();
        endAuction = lot.getEndAuction();
        currentCost = lot.getCurrentCost();
        rateStep = lot.getRateStep();
        ownerId = owId;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}