package com.project.auction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.project.auction.models.Lot;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.repository.LotRepository;

@Service
public class LotService {

    @Autowired
    private LotRepository lotRepository;

    public Lot createLot(CreateLotRequest req) {
        Lot lot = new Lot();
        lot.setStartAuction(req.getStartAuction());
        lot.setEndAuction(req.getEndAuction());
<<<<<<< HEAD
        lot.setCurrentCost(req.getStartPrice());
=======
        lot.setCurrentCost(req.getCurrentCost());
>>>>>>> d081650869dd4266ebcea1ed93412813fcb2b3e9
        lot.setRateStep(req.getRateStep());
        lot.setGoodId(req.getGoodId());
        return lotRepository.save(lot);
    }

    public Page<Lot> getUserLots(Long buyerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // page=0, size=10
        return lotRepository.findByBuyerId(buyerId, pageable);
    }

}