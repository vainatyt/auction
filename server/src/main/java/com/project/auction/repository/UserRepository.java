package com.project.auction.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import com.project.auction.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
    Optional<User> findByName(String name);
    Boolean existsByName(String name);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
