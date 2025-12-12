package com.project.auction.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.project.auction.models.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    void deleteByLotId(Long lotId);
}
