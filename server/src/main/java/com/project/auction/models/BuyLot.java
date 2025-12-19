package com.project.auction.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.project.auction.pojo.BuyStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "buy_lot_requests")
public class BuyLot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "id_lot")
    private Long lotId;
    
    @Column(name = "req_cost")
    private BigDecimal reqCost;
    
    @Column(name = "id_user")
    private Long userId;
    
    @Column(name = "date")
    private LocalDateTime date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BuyStatus status;

    public BuyLot() {
    }

    public BuyLot(Long lotId, BigDecimal reqCost, Long userId) {
        this.lotId = lotId;
        this.reqCost = reqCost;
        this.userId = userId;
        this.date = LocalDateTime.now();
    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
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
    
    public Long getUserId() { 
        return userId; 
    }
    public void setUserId(Long userId) { 
        this.userId = userId; 
    }
    
    public LocalDateTime getDate() { 
        return date; 
    }
    public void setDate(LocalDateTime date) { 
        this.date = date; 
    }
    
    public BuyStatus getStatus() { 
        return status; 
    }
    public void setStatus(BuyStatus status) { 
        this.status = status; 
    }
}
