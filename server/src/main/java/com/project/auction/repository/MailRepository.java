package com.project.auction.repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.auction.models.Mail;

public interface MailRepository extends JpaRepository<Mail, Long> {
    Page<Mail> findByUserId(Long userId, Pageable pageable);
    Optional<Mail> findById(Long id);
}
