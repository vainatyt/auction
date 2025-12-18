package com.project.auction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSchedulerTest {

    @Mock
    private LotService lotService;

    @InjectMocks
    private AuctionScheduler auctionScheduler;

    @Test
    void checkLots_callsLotServiceCheckAndCloseExpiredLots() {
        // when
        auctionScheduler.checkLots();

        // then
        verify(lotService).checkAndCloseExpiredLots();
        verifyNoMoreInteractions(lotService);
    }

    @Test
    void checkLots_runsWithoutErrors() {
        // when
        auctionScheduler.checkLots();

        // then - тест просто проходит без исключений
    }
}
