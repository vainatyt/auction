package com.project.auction.repository;

import java.util.List;
import com.project.auction.models.BuyLot;
import com.project.auction.pojo.BuyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyLotRepository extends JpaRepository<BuyLot, Long> {
    List<BuyLot> findByStatusOrderByDateAsc(BuyStatus status);
    List<BuyLot> findByLotId(Long lotId);
    void deleteByLotIdIn(List<Long> lotIds);
}
