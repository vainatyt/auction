package com.project.auction.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.auction.models.Lot;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    Page<Lot> findByIdIn(List<Long> buyerId, Pageable pageable);
    Page<Lot> findByBuyerId(Long buyerId, Pageable pageable);
    Page<Lot> findByOwnerId(Long buyerId, Pageable pageable);

    @Query(value = "SELECT m.name, m.description, l.current_cost, l.rate_step, " +
       "l.start_auction, l.end_auction " +
       "FROM lots l LEFT JOIN metadata_lot m ON l.id_lot = m.lot_id " +
       "WHERE l.id_owner = :ownerId", 
       countQuery = "SELECT COUNT(*) FROM lots l LEFT JOIN metadata_lot m ON l.id_lot = m.lot_id WHERE l.id_owner = :ownerId",
       nativeQuery = true)
    Page<Object[]> findUserLotsWithMetadata(
        @Param("ownerId") Long ownerId, 
        Pageable pageable
    );
}