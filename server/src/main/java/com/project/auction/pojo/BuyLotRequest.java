package com.project.auction.pojo;

import java.math.BigDecimal;

public class BuyLotRequest {
    private Long lotId;
    private BigDecimal reqCost;
    
    public BuyLotRequest() {}
    
    public BuyLotRequest(Long lotId, BigDecimal reqCost) {
        this.lotId = lotId;
        this.reqCost = reqCost;
    }
    
    public Long getLotId() {
        return lotId;
    }
    
    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }
    
    public BigDecimal getReqCost() {
        return reqCost;
    }
    
    public void setReqCost(BigDecimal reqCost) {
        this.reqCost = reqCost;
    }
}
