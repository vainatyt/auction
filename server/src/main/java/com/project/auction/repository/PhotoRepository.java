package com.project.auction.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.auction.models.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    void deleteByLotId(Long lotId);

    @Query("SELECT p.uuid FROM Photo p WHERE p.lotId = :lotId")
    UUID findUuidByLotId(@Param("lotId") Long lotId);

    Photo findByLotId(Long lotId);
}
