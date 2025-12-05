package com.project.auction.repository;
import org.springframework.data.repository.CrudRepository;
import com.project.auction.models.Photo;

public interface PhotoRepository extends CrudRepository<Photo, Long> {
}
