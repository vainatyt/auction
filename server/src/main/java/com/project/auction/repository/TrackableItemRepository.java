package com.project.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.auction.models.TrackableItem;
import com.project.auction.models.Lot;
import com.project.auction.models.User;
import java.util.List;
import java.util.Optional;

public interface TrackableItemRepository extends JpaRepository<TrackableItem, Long> {

    List<TrackableItem> findByUserId(User user);

    List<TrackableItem> findByLotId(Lot lot);

    Optional<TrackableItem> findByUserIdAndLotId(User user, Lot lot);

    void deleteByUserIdAndLotId(User user, Lot lot);
}
