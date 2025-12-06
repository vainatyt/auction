package com.project.auction.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.project.auction.pojo.CreateLotRequest;

import org.springframework.data.relational.core.mapping.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "lots")
public class Lot {

    @Id
    @Column("id_lot")
    private Long id;

    @Column("start_auction")
    private LocalDateTime startAuction;

    @Column("end_auction")
    private LocalDateTime endAuction;

    @Column("current_cost")
    private BigDecimal currentCost;

    @Column("rate_step")
    private BigDecimal rateStep;

    @Column("id_buyer")
    private Long buyerId;

    @Column("id_owner")
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

    public void setGoodId(Long ownerId) {
        this.ownerId = ownerId;
    }
}