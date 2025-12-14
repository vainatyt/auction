package com.project.auction.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import com.project.auction.models.MetaDataLot;


public interface MetaDataLotRepository extends JpaRepository<MetaDataLot, Long> {
    Optional<MetaDataLot> findByLotId(Long lotId);
    Page<MetaDataLot> findByLotId(Long lotId, Pageable pageable);
    Page<MetaDataLot> findByLotIdIn(List<Long> lotIds, Pageable pageable);
    void deleteByLotId(Long lotId);
}
