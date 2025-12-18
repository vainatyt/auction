package com.project.auction.repository;

import com.project.auction.models.Lot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LotRepositoryTest {

    @Autowired
    private LotRepository lotRepository;

    @Test
    void findByBuyerId_returnsLots() {
        Lot lot1 = createLot(1L, 100L); // buyerId=100
        Lot lot2 = createLot(1L, 100L);
        Lot lotOther = createLot(2L, 200L);
        
        lotRepository.saveAll(List.of(lot1, lot2, lotOther));

        Page<Lot> result = lotRepository.findByBuyerId(100L, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findByOwnerId_returnsLots() {
        Lot lot1 = createLot(1L, 10L); // ownerId=10
        Lot lot2 = createLot(1L, 10L);
        lotRepository.saveAll(List.of(lot1, lot2));

        Page<Lot> result = lotRepository.findByOwnerId(10L, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findExpiredLots_returnsOnlyExpired() {
        LocalDateTime now = LocalDateTime.now();
        
        Lot expired = createLot(1L, 10L);
        expired.setEndAuction(now.minusDays(1)); // expired
        
        Lot active = createLot(2L, 10L);
        active.setEndAuction(now.plusDays(1)); // active

        lotRepository.saveAll(List.of(expired, active));

        List<Lot> result = lotRepository.findExpiredLots(now);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEndAuction()).isBefore(now);
    }

    private Lot createLot(Long id, Long ownerId) {
        Lot lot = new Lot();
        lot.setId(id);
        lot.setOwnerId(ownerId);
        lot.setBuyerId(ownerId); // для теста
        lot.setCurrentCost(java.math.BigDecimal.TEN);
        lot.setRateStep(java.math.BigDecimal.ONE);
        lot.setStartAuction(LocalDateTime.now());
        lot.setEndAuction(LocalDateTime.now().plusDays(1));
        return lot;
    }
}
