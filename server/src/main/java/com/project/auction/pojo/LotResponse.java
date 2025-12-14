package com.project.auction.pojo;

import java.math.BigDecimal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import com.project.auction.models.Lot;
import com.project.auction.models.MetaDataLot;

public class LotResponse {

    private Long id;
    // данные по товару (metadata)
    private String name;
    private String description;

    // данные по лоту (Lots)
    private BigDecimal currentCost;
    private BigDecimal rateStep;
    private LocalDateTime startAuction;
    private LocalDateTime endAuction;

    private UUID uuid;
    
    public LotResponse() {
    }

    public LotResponse(Lot lot, MetaDataLot meta, UUID uuid){
        this.id = lot.getId();
        this.name = meta.getName();
        this.description = meta.getDescription();
        this.currentCost = lot.getCurrentCost();
        this.rateStep = lot.getRateStep();
        this.startAuction = lot.getStartAuction();
        this.endAuction = lot.getEndAuction();
        this.uuid = uuid;
    }

    public LotResponse(String name, String discription, BigDecimal currentCost,
                         BigDecimal rateStep, LocalDateTime startAuction,
                          LocalDateTime endAuction, Long id, UUID uuid) {
        this.name = name;
        this.description = discription;
        this.currentCost = currentCost;
        this.rateStep = rateStep;
        this.startAuction = startAuction;
        this.endAuction = endAuction;
        this.id = id;
        this.uuid = uuid;
    }
    
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
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

    public UUID getUuid(){
        return uuid;
    }

    public void setUuid(UUID uuid){
        this.uuid = uuid;
    }
}
