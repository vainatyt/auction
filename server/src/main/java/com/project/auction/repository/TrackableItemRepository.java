package com.project.auction.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.auction.models.TrackableItem;
import com.project.auction.models.Lot;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackableItemRepository extends JpaRepository<TrackableItem, Long> {

    List<TrackableItem> findByUserId(Long id);

    List<TrackableItem> findByLotId(Lot lot);

    @Query("SELECT t FROM TrackableItem t WHERE t.id.userId = :userId AND t.id.lotId = :lotId")
    Optional<TrackableItem> findByUserIdAndLotId(@Param("userId") Long userId, @Param("lotId") Long lotId);

    @Modifying
    @Query("DELETE FROM TrackableItem t WHERE t.id.userId = :userId AND t.id.lotId = :lotId")
    void deleteByUserIdAndLotId(@Param("userId") Long userId, @Param("lotId") Long lotId);

    @Query(value = "SELECT m.name, m.description, l.current_cost, l.rate_step, " +
               "l.start_auction, l.end_auction, l.id_lot " +
               "FROM lots l " +
               "LEFT JOIN metadata_lot m ON l.id_lot = m.id_lot " +
               "INNER JOIN trackable_items t ON l.id_lot = t.id_lot " +  // ✅ INNER для отслеживаемых
               "WHERE t.id_user = :userId",
       countQuery = "SELECT COUNT(DISTINCT l.id_lot) " +           // ✅ ФИКС countQuery!
                    "FROM lots l " +
                    "INNER JOIN trackable_items t ON l.id_lot = t.id_lot " +
                    "WHERE t.id_user = :userId", 
       nativeQuery = true)
    Page<Object[]> findTrackedLotsByUserId(
        @Param("userId") Long userId,
        Pageable pageable
    );
    void deleteById_LotId(Long lotId);
}
