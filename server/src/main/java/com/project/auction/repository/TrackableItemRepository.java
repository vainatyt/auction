package com.project.auction.repository;

import org.springframework.data.repository.CrudRepository;
import com.project.auction.models.TrackableItem;
import com.project.auction.models.TrackableItemId;
import com.project.auction.models.Lot;
import com.project.auction.models.User;
import java.util.List;
import java.util.Optional;

public interface TrackableItemRepository extends CrudRepository<TrackableItem, TrackableItemId> {

    List<TrackableItem> findByUser(User user);

    List<TrackableItem> findByLot(Lot lot);

    Optional<TrackableItem> findByUserAndLot(User user, Lot lot);

    void deleteByUserAndLot(User user, Lot lot);
}
