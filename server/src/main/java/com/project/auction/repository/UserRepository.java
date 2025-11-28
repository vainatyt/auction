package com.project.auction.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.auction.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByName(String name);
    Boolean existsByName(String name);
    User findByEmail(String email);
    Boolean existsByEmail(String email);
}
