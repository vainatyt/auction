package com.project.auction.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.auction.models.TrackableItem;
import com.project.auction.models.Lot;
import com.project.auction.models.User;
import com.project.auction.pojo.LotResponse;

import java.util.List;
import java.util.Optional;

public interface TrackableItemRepository extends JpaRepository<TrackableItem, Long> {

    List<TrackableItem> findByUserId(Long id);

    List<TrackableItem> findByLotId(Lot lot);

    Optional<TrackableItem> findByUserIdAndLotId(User user, Lot lot);

    void deleteByUserIdAndLotId(User user, Lot lot);

    @Query(value = "SELECT m.name, m.description, l.current_cost, l.rate_step, " +
       "l.start_auction, l.end_auction, l.id_lot " +
       "FROM lots l LEFT JOIN metadata_lot m ON l.id_lot = m.id_lot " +
       "LEFT JOIN trackable_items t ON l.id_lot = t.id_lot " +
       "WHERE t.id_user = :userId",
       countQuery = "SELECT COUNT(*) FROM lots l LEFT JOIN metadata_lot m ON l.id_lot = m.id_lot WHERE l.id_lot = :id",
       nativeQuery = true)
    Page<LotResponse> findLotAndMetadataByUserId(
        @Param("userId") Long userId,
        Pageable pageable
    );

}
