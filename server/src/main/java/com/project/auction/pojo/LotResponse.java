package com.project.auction.pojo;

import java.math.BigDecimal;
import java.time.Instant;

public class LotResponse {

    private Long id;
    // данные по товару (metadata)
    private String name;
    private String description;

    // данные по лоту (Lots)
    private BigDecimal currentCost;
    private BigDecimal rateStep;
    private Instant startAuction;
    private Instant endAuction;
    
    public LotResponse() {
    }

    public LotResponse(String name, String discription, BigDecimal currentCost,
                         BigDecimal rateStep, Instant startAuction, Instant endAuction, Long id) {
        this.name = name;
        this.description = discription;
        this.currentCost = currentCost;
        this.rateStep = rateStep;
        this.startAuction = startAuction;
        this.endAuction = endAuction;
        this.id = id;
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


    public Instant getStartAuction() {
        return startAuction;
    }

    public void setStartAuction(Instant startAuction) {
        this.startAuction = startAuction;
    }

    public Instant getEndAuction() {
        return endAuction;
    }

    public void setEndAuction(Instant endAuction) {
        this.endAuction = endAuction;
    }
}
