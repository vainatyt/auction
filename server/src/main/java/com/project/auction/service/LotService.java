package com.project.auction.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import com.project.auction.pojo.BuyLotRequest;
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

    @Transactional
    public Page<Lot> getUserLots(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lot> pageLot = lotRepository.findByOwnerId(userId, pageable);
        return pageLot;
    }

    @Transactional
    public Page<Lot> getUserLots(Long userId, Pageable pageable) {
        Page<Lot> pageLot = lotRepository.findByOwnerId(userId, pageable);
        return pageLot;
    }

    @Transactional
    public Page<LotResponse> findUserLotsWithMetadata(Long ownerId, Pageable pageable) {
        Page<Object[]> rawPage = lotRepository.findUserLotsWithMetadata(ownerId, pageable);
        Page<LotResponse> result = rawPage.map(row -> new LotResponse(
            (String)row[0], (String)row[1], (BigDecimal)row[2],
            (BigDecimal)row[3], (Instant)row[4], (Instant)row[5], ((Number)row[6]).longValue()));
        return result;
    }

    @Transactional
     public Page<LotResponse> findLotsWithMetadata(Pageable pageable) {
        Page<Object[]> rawPage = lotRepository.findLotsWithMetadata(pageable);
        Page<LotResponse> result = rawPage.map(row -> new LotResponse(
            (String)row[0], (String)row[1], (BigDecimal)row[2],
            (BigDecimal)row[3], (Instant)row[4], (Instant)row[5], ((Number)row[6]).longValue()));
        return result;
    }

    @Transactional
    public LotResponse findLotWithMetadataById(Long id){
        Optional<Lot> lot = lotRepository.findById(id);
        Optional<MetaDataLot> meta = metaDataLotRepository.findByLotId(id);
        return new LotResponse(lot.get(), meta.get());
    }

    @Transactional
    public Lot buyLot(Long userId, BuyLotRequest buyLotRequest){
        Lot lot = lotRepository.findById(buyLotRequest.getLotId()).get();
        lot.setCurrentCost(buyLotRequest.getReqCost());
        return lotRepository.save(lot);
    }
}
