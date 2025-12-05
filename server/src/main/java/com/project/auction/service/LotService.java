package com.project.auction.service;

import org.springframework.stereotype.Service;
import com.project.auction.models.*;
import com.project.auction.payload.request.*;
import com.project.auction.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;

@Service
public class LotService {

    private final LotRepository lotRepository;
    private final GoodsRepository goodsRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    public LotService(LotRepository lotRepository,
                      GoodsRepository goodsRepository,
                      PhotoRepository photoRepository,
                      UserRepository userRepository) {
        this.lotRepository = lotRepository;
        this.goodsRepository = goodsRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }
    public List<Lot> getAll() {
        return lotRepository.findAll();
    }
    @Transactional
    public Lot createLot(CreateLotRequest request, Long ownerId) {
        // 1. владелец
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. товар
        Goods goods = new Goods();
        goods.setOwner(owner);
        goods.setName(request.getGoodsName());
        goods.setDescription(request.getGoodsDescription());
        goods = goodsRepository.save(goods);

        // 3. фото (если присылаешь список UUID строк)
        for (String uuidStr : request.getPhotoUuids()) {
            Photo photo = new Photo();
            photo.setGoods(goods);
            photo.setUuid(UUID.fromString(uuidStr));
            photoRepository.save(photo);
        }

        // 4. лот
        Lot lot = new Lot();
        lot.setGoods(goods);
        lot.setStartAuction(request.getStartAuction());
        lot.setEndAuction(request.getEndAuction());
        lot.setCurrentCost(request.getStartPrice());
        lot.setRateStep(request.getRateStep());
        // buyer пока null

        return lotRepository.save(lot);
    }
}
