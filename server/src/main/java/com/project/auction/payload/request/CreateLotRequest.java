package com.project.auction.payload.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateLotRequest {

    private LocalDateTime startAuction;
    private LocalDateTime endAuction;
    private BigDecimal startCost;
    private BigDecimal rateStep;
    private Long goodId;

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

    public BigDecimal getStartCost() {
        return startCost;
    }

    public void setStartCost(BigDecimal startCost) {
        this.startCost = startCost;
    }

    public BigDecimal getRateStep() {
        return rateStep;
    }

    public void setRateStep(BigDecimal rateStep) {
        this.rateStep = rateStep;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }
}
