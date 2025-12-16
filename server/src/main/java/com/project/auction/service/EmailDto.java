package com.project.auction.service;

import java.time.LocalDateTime;

public record EmailDto(String to, String subject, String text,LocalDateTime timestamp,Long ownerId, String ownerName) {}

