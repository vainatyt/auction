package com.project.auction.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.auction.models.Lot;
import com.project.auction.models.MetaDataLot;

public class LotResponse {
    // данные по товару (metadata)
    private String name;
    private String description;

    // данные по лоту (Lots)
    private BigDecimal currentCost;
    private BigDecimal rateStep;
    private LocalDateTime startAuction;
    private LocalDateTime endAuction;
    
    public LotResponse() {
    }

    public LotResponse(Lot lot, MetaDataLot meta) {
        name = meta.getName();
        description = meta.getDescription();
        currentCost = lot.getCurrentCost();
        rateStep = lot.getRateStep();
        startAuction = lot.getStartAuction();
        endAuction = lot.getEndAuction();
    }

    public LotResponse(String name, String discription, BigDecimal currentCost,
                         BigDecimal rateStep, LocalDateTime startAuction, LocalDateTime endAuction) {
        this.name = name;
        this.description = discription;
        this.currentCost = currentCost;
        this.rateStep = rateStep;
        this.startAuction = startAuction;
        this.endAuction = endAuction;
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ----- lot -----

    public BigDecimal  getRateStep() {
        return rateStep;
    }

    public void setRateStep(BigDecimal  rateStep) {
        this.rateStep = rateStep;
    }

    public BigDecimal getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(BigDecimal currentCost) {
        this.currentCost = currentCost;
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
}
