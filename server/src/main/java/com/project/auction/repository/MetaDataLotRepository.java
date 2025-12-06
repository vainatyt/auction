package com.project.auction.repository;

import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import com.project.auction.models.MetaDataLot;


public interface MetaDataLotRepository extends CrudRepository<MetaDataLot, Long> {
    Page<MetaDataLot> findByLotId(Long lotId, Pageable pageable);
}
