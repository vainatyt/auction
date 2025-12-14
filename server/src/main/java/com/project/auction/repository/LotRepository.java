package com.project.auction.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.auction.models.Lot;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    Optional<Lot> findById(Long id);
    Page<Lot> findByBuyerId(Long buyerId, Pageable pageable);
    Page<Lot> findByOwnerId(Long buyerId, Pageable pageable);

    @Query(value = "SELECT m.name, m.description, l.current_cost, l.rate_step, " +
                   "l.start_auction, l.end_auction, l.id_lot, p.uuid " +
                   "FROM lots l " +
                   "LEFT JOIN metadata_lot m ON l.id_lot = m.id_lot " +
                   "LEFT JOIN photo p ON l.id_lot = p.id_lot " +
                   "WHERE l.id_owner = :ownerId", 
          countQuery = "SELECT COUNT(*) FROM lots l LEFT JOIN metadata_lot m ON l.id_lot = m.id_lot WHERE l.id_owner = :ownerId",
          nativeQuery = true)
    Page<Object[]> findUserLotsWithMetadata(
        @Param("ownerId") Long ownerId, 
        Pageable pageable
    );

    @Query(value = "SELECT m.name, m.description, l.current_cost, l.rate_step, " +
                   "l.start_auction, l.end_auction, l.id_lot, p.uuid " +
                   "FROM lots l " +
                   "LEFT JOIN metadata_lot m ON l.id_lot = m.id_lot " +
                   "LEFT JOIN photo p ON l.id_lot = p.id_lot", 
          countQuery = "SELECT COUNT(*) FROM lots l LEFT JOIN metadata_lot m ON l.id_lot = m.id_lot",
          nativeQuery = true)
    Page<Object[]> findLotsWithMetadata(Pageable pageable);

    @Query(value = "SELECT m.name, m.description, l.current_cost, l.rate_step, " +
                   "l.start_auction, l.end_auction, l.id_lot, p.uuid " +
                   "FROM lots l " +
                   "LEFT JOIN metadata_lot m ON l.id_lot = m.id_lot " +
                   "LEFT JOIN photo p ON l.id_lot = p.id_lot " +
                   "WHERE l.id_lot = :id",
          nativeQuery = true)
    Object[] findLotWithMetadata(@Param("id") Long id);

    @Query("SELECT l FROM Lot l WHERE l.endAuction < :now")
    List<Lot> findExpiredLots(LocalDateTime now);
}
