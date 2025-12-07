package com.project.auction.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.auction.models.Lot;
import com.project.auction.models.MetaDataLot;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.LotRepository;
import com.project.auction.repository.MetaDataLotRepository;

@Service
public class LotService {

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private MetaDataLotRepository metaDataLotRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Transactional
public Lot createLot(Long userId, CreateLotRequest req) {
    Lot lot = new Lot(userId, req);
    lot = lotRepository.save(lot);

    String sql = "INSERT INTO metadata_lot (id_lot, name, description) VALUES (?, ?, ?)";

    jdbcTemplate.update(sql, lot.getId(), req.getGoodsName(), req.getGoodsDescription());

    return lot;
}

    public Page<Lot> getUserLots(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lot> pageLot = lotRepository.findByOwnerId(userId, pageable);
        return pageLot;
    }

    public Page<Lot> getUserLots(Long userId, Pageable pageable) {
        Page<Lot> pageLot = lotRepository.findByOwnerId(userId, pageable);
        return pageLot;
    }

    public Page<LotResponse> findUserLotsWithMetadata(Long ownerId, Pageable pageable) {
        // 1. Получаем ЛОТЫ пользователя (пагинация!)
        Page<Lot> lotsPage = lotRepository.findByOwnerId(ownerId, pageable);
        
        // 2. Извлекаем lotId ТОЛЬКО текущей страницы
        List<Long> lotIds = lotsPage.getContent()
            .stream()
            .map(Lot::getId)
            .collect(Collectors.toList());
        
        // 3. Метаданные для ЭТИХ лотов (та же пагинация!)
        Page<MetaDataLot> metaPage = metaDataLotRepository.findByLotIdIn(lotIds, pageable);
        
        // 4. Объединяем → Page<LotResponse> с пагинацией!
        List<LotResponse> combined = IntStream.range(0, lotsPage.getContent().size())
            .mapToObj(i -> new LotResponse(
                lotsPage.getContent().get(i),
                metaPage.getContent().get(i)
            ))
            .collect(Collectors.toList());
        
        return new PageImpl<>(combined, pageable, lotsPage.getTotalElements());
    }


}
