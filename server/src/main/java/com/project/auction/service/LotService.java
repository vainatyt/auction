package com.project.auction.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.auction.models.Lot;
import com.project.auction.models.MetaDataLot;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.repository.LotRepository;
import com.project.auction.repository.MetaDataLotRepository;

@Service
public class LotService {

    @Autowired
    private LotRepository lotRepository;

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


}
