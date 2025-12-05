package com.project.auction.service;

import org.springframework.stereotype.Service;
import com.project.auction.models.Lot;
import com.project.auction.payload.request.CreateLotRequest;
import com.project.auction.repository.LotRepository;
import java.util.List;

@Service
public class LotService {

    private final LotRepository lotRepository;

    public LotService(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    public Lot createLot(CreateLotRequest req) {
    Lot lot = new Lot();
    lot.setStartAuction(req.getStartAuction());
    lot.setEndAuction(req.getEndAuction());
    lot.setCurrentCost(req.getStartCost());
    lot.setRateStep(req.getRateStep());
    lot.setGoodId(req.getGoodId());
    return lotRepository.save(lot);
}
public List<Lot> getAll() {
        return lotRepository.findAll();
    }

}
