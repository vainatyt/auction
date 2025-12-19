package com.project.auction.service;

import com.project.auction.models.Lot;
import com.project.auction.models.TrackableItem;
import com.project.auction.models.User;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.LotRepository;
import com.project.auction.repository.TrackableItemRepository;
import com.project.auction.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackableItemServiceTest {

    @Mock
    private TrackableItemRepository trackableItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LotRepository lotRepository;

    @InjectMocks
    private TrackableItemService trackableItemService;

    // ---------- addToTracking ----------

    @Test
    void addToTracking_notTracked_savesNewItem() {
        Long userId = 1L;
        Long lotId = 2L;

        User user = new User();
        user.setId(userId);

        Lot lot = new Lot();
        lot.setId(lotId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lotRepository.findById(lotId)).thenReturn(Optional.of(lot));
        when(trackableItemRepository.findByUserIdAndLotId(userId, lotId))
                .thenReturn(Optional.empty());

        trackableItemService.addToTracking(userId, lotId);

        verify(trackableItemRepository).save(
                argThat(item ->
                        item.getUserId().equals(userId) &&
                        item.getLotId().equals(lotId)
                )
        );
    }

    @Test
    void addToTracking_alreadyTracked_doesNotSave() {
        Long userId = 1L;
        Long lotId = 2L;

        User user = new User();
        user.setId(userId);

        Lot lot = new Lot();
        lot.setId(lotId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lotRepository.findById(lotId)).thenReturn(Optional.of(lot));
        when(trackableItemRepository.findByUserIdAndLotId(userId, lotId))
                .thenReturn(Optional.of(new TrackableItem()));

        trackableItemService.addToTracking(userId, lotId);

        verify(trackableItemRepository, never()).save(any());
    }

    @Test
    void addToTracking_userNotFound_throwsUnauthorized() {
        Long userId = 1L;
        Long lotId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackableItemService.addToTracking(userId, lotId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED);
    }

    @Test
    void addToTracking_lotNotFound_throwsUnauthorized() {
        Long userId = 1L;
        Long lotId = 2L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lotRepository.findById(lotId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackableItemService.addToTracking(userId, lotId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED);
    }

    // ---------- removeFromTracking ----------

    @Test
    void removeFromTracking_deletesItem() {
        Long userId = 1L;
        Long lotId = 2L;

        User user = new User();
        user.setId(userId);

        Lot lot = new Lot();
        lot.setId(lotId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lotRepository.findById(lotId)).thenReturn(Optional.of(lot));

        trackableItemService.removeFromTracking(userId, lotId);

        verify(trackableItemRepository).deleteByUserIdAndLotId(userId, lotId);
    }

    @Test
    void removeFromTracking_userNotFound_throwsUnauthorized() {
        Long userId = 1L;
        Long lotId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackableItemService.removeFromTracking(userId, lotId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED);
    }

    @Test
    void removeFromTracking_lotNotFound_throwsUnauthorized() {
        Long userId = 1L;
        Long lotId = 2L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lotRepository.findById(lotId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackableItemService.removeFromTracking(userId, lotId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED);
    }

    // ---------- getTrackedLots ----------

    @Test
        void getTrackedLots_mapsPageCorrectly() {
        Long userId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<Object[]> content = List.of(
                new Object[] {
                        "Phone",                      // 0 name
                        "iPhone",                     // 1 description
                        BigDecimal.TEN,               // 2 currentPrice
                        BigDecimal.ONE,               // 3 startPrice
                        LocalDateTime.now(),          // 4 startDate
                        LocalDateTime.now().plusDays(1), // 5 endDate
                        1L,                           // 6 biddersCount
                        UUID.randomUUID()             // 7 lotUuid
                },
                new Object[] {
                        "Phone",                      // 0 name
                        "Android",                     // 1 description
                        BigDecimal.TEN,               // 2 currentPrice
                        BigDecimal.ONE,               // 3 startPrice
                        LocalDateTime.now(),          // 4 startDate
                        LocalDateTime.now().plusDays(1), // 5 endDate
                        1L,                           // 6 biddersCount
                        UUID.randomUUID()             // 7 lotUuid
                }
        );

        Page<Object[]> rawPage = new PageImpl<>(content, pageable, content.size());

        when(trackableItemRepository.findTrackedLotsByUserId(userId, pageable))
                .thenReturn(rawPage);

        Page<LotResponse> result = trackableItemService.getTrackedLots(userId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        LotResponse first = result.getContent().get(0);
        LotResponse second = result.getContent().get(1);

        assertThat(first.getDescription()).isEqualTo("iPhone");
        assertThat(second.getDescription()).isEqualTo("Android");

        }

    @Test
    void getTrackedLots_userNotFound_throwsUnauthorized() {
        Long userId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackableItemService.getTrackedLots(userId, pageable))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED);
    }

    // ---------- isTracked ----------

    @Test
    void isTracked_returnsTrue_whenPresent() {
        Long userId = 1L;
        Long lotId = 2L;

        when(trackableItemRepository.findByUserIdAndLotId(userId, lotId))
                .thenReturn(Optional.of(new TrackableItem()));

        boolean result = trackableItemService.isTracked(userId, lotId);

        assertThat(result).isTrue();
    }

    @Test
    void isTracked_returnsFalse_whenEmpty() {
        Long userId = 1L;
        Long lotId = 2L;

        when(trackableItemRepository.findByUserIdAndLotId(userId, lotId))
                .thenReturn(Optional.empty());

        boolean result = trackableItemService.isTracked(userId, lotId);

        assertThat(result).isFalse();
    }
}
