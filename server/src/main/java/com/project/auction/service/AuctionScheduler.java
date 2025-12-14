package com.project.auction.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuctionScheduler {

    private final LotService lotService;

    public AuctionScheduler(LotService lotService) {
        this.lotService = lotService;
    }

    // запускается каждые 10 секунд
    @Scheduled(fixedRate = 10_000)
    public void checkLots() {
        System.out.println("Scheduler: проверка лотов...");
        lotService.checkAndCloseExpiredLots();
    }
}
