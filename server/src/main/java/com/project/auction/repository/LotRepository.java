package com.project.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.auction.models.Lot;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
}
