package com.project.auction.repository;

import com.project.auction.models.TrackableItem;
import com.project.auction.models.TrackableItemId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TrackableItemRepositoryTest {

    @Autowired
    private TrackableItemRepository trackableItemRepository;

    @Test
    void findByUserId_returnsItemsForUser() {
        TrackableItem item1 = buildItem(1L, 100L);
        TrackableItem item2 = buildItem(1L, 200L);
        TrackableItem itemOtherUser = buildItem(2L, 300L);

        trackableItemRepository.save(item1);
        trackableItemRepository.save(item2);
        trackableItemRepository.save(itemOtherUser);

        List<TrackableItem> result = trackableItemRepository.findByUserId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLotId()).isIn(100L, 200L);
        assertThat(result.get(1).getLotId()).isIn(100L, 200L);


    }

    @Test
    void findByUserIdAndLotId_returnsSingleItem() {
        TrackableItem item = buildItem(5L, 500L);
        trackableItemRepository.save(item);

        Optional<TrackableItem> found =
                trackableItemRepository.findByUserIdAndLotId(5L, 500L);

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(5L);
        assertThat(found.get().getLotId()).isEqualTo(500L);
    }

    @Test
    @Transactional
    void deleteByUserIdAndLotId_removesRow() {
        TrackableItem item = buildItem(3L, 300L);
        trackableItemRepository.save(item);

        trackableItemRepository.deleteByUserIdAndLotId(3L, 300L);

        Optional<TrackableItem> found =
                trackableItemRepository.findByUserIdAndLotId(3L, 300L);
        assertThat(found).isEmpty();
    }

    @Test
    @Transactional
    void deleteById_LotId_removesAllWithLotId() {
        trackableItemRepository.save(buildItem(1L, 10L));
        trackableItemRepository.save(buildItem(2L, 10L)); // тот же lotId, другой user
        trackableItemRepository.save(buildItem(3L, 20L));

        trackableItemRepository.deleteById_LotId(10L);

        List<TrackableItem> forLot10 = trackableItemRepository.findByIdLotId(10L);
        List<TrackableItem> forLot20 = trackableItemRepository.findByIdLotId(20L);

        assertThat(forLot10).isEmpty();
        assertThat(forLot20).hasSize(1);
    }

    // Вспомогательный метод для создания сущности
    private TrackableItem buildItem(Long userId, Long lotId) {
        TrackableItemId id = new TrackableItemId();
        id.setUserId(userId);
        id.setLotId(lotId);

        TrackableItem item = new TrackableItem();
        item.setUserId(userId);
        item.setLotId(lotId);
        return item;
    }
}
