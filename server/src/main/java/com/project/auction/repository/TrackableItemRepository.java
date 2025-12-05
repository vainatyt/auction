package com.project.auction.repository;

import com.project.auction.models.TrackableItem;
import com.project.auction.models.TrackableItemId;
import com.project.auction.models.Lot;
import com.project.auction.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackableItemRepository extends JpaRepository<TrackableItem, TrackableItemId> {

    List<TrackableItem> findByUser(User user);

    List<TrackableItem> findByLot(Lot lot);

    Optional<TrackableItem> findByUserAndLot(User user, Lot lot);

    void deleteByUserAndLot(User user, Lot lot);
}
