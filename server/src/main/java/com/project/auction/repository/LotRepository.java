package com.project.auction.repository;

import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.project.auction.models.Lot;

@Repository
public interface LotRepository extends CrudRepository<Lot, Long> {
    Page<Lot> findByBuyerId(Long buyerId, Pageable pageable);
}
