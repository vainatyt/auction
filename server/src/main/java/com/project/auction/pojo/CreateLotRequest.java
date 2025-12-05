package com.project.auction.payload.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreateLotRequest {

    // данные по товару (Goods)
    private String goodsName;
    private String goodsDescription;

    // данные по лоту (Lots)
    private BigDecimal startPrice;
    private BigDecimal rateStep;
    private LocalDateTime startAuction;
    private LocalDateTime endAuction;

    // список UUID фотографий (photo.uuid) в виде строк
    private List<String> photoUuids;

    public CreateLotRequest() {
    }

    // ----- goods -----

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    // ----- lot -----

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public BigDecimal getRateStep() {
        return rateStep;
    }

    public void setRateStep(BigDecimal rateStep) {
        this.rateStep = rateStep;
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

    // ----- photos -----

    public List<String> getPhotoUuids() {
        return photoUuids;
    }

    public void setPhotoUuids(List<String> photoUuids) {
        this.photoUuids = photoUuids;
    }
}
