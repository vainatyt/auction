package com.project.auction.service;

import com.project.auction.models.*;
import com.project.auction.pojo.BuyLotRequest;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotServiceTest {

    @Mock private LotRepository lotRepository;
    @Mock private MetaDataLotRepository metaDataLotRepository;
    @Mock private PhotoRepository photoRepository;
    @Mock private TrackableItemRepository trackableItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private MailService mailService;
    @Mock private ImageService imageService;

    @InjectMocks private LotService lotService;

    @Test
    void createLot_successful_returnsLot() {
        Long userId = 10L;
        CreateLotRequest request = new CreateLotRequest();
        request.setGoodsName("Phone");
        request.setGoodsDescription("iPhone");
        request.setCurrentCost(BigDecimal.valueOf(1000));
        request.setRateStep(BigDecimal.valueOf(50));

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "data".getBytes());

        Lot savedLot = new Lot();
        savedLot.setId(1L);

        MetaDataLot savedMeta = new MetaDataLot();
        savedMeta.setId(1L);

        when(lotRepository.save(any(Lot.class))).thenReturn(savedLot);
        when(metaDataLotRepository.save(any(MetaDataLot.class))).thenReturn(savedMeta);

        Lot result = lotService.createLot(userId, request, image);

        assertThat(result.getId()).isEqualTo(1L);
        verify(lotRepository).save(any(Lot.class));
        verify(metaDataLotRepository).save(any(MetaDataLot.class));
        verify(imageService).savePhoto(eq(1L), eq(image));
    }

@Test
void findUserLotsWithMetadata_returnsPage() {
    Long ownerId = 10L;
    PageRequest pageable = PageRequest.of(0, 10);

    List<Object[]> content = List.of(
        new Object[]{
            "Phone",                    // 0: m.name (String)
            "iPhone 14",                // 1: m.description (String)
            new BigDecimal("10.00"),    // 2: l.current_cost (BigDecimal)
            new BigDecimal("1.00"),     // 3: l.rate_step (BigDecimal)
            LocalDateTime.now(),        // 4: l.start_auction
            LocalDateTime.now().plusDays(1), // 5: l.end_auction
            1L,                         // 6: l.id_lot (Long)
            UUID.randomUUID()           // 7: p.uuid (UUID)
        },
        new Object[]{
            "TV", "Samsung", new BigDecimal("500.00"), new BigDecimal("25.00"),
            LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2L, UUID.randomUUID()
        }
    );
    Page<Object[]> rawPage = new PageImpl<>(content, pageable, 2);

    when(lotRepository.findUserLotsWithMetadata(ownerId, pageable)).thenReturn(rawPage);

    Page<LotResponse> result = lotService.findUserLotsWithMetadata(ownerId, pageable);

    assertThat(result.getContent()).hasSize(2);
    verify(lotRepository).findUserLotsWithMetadata(ownerId, pageable);
}

@Test
void findLotsWithMetadata_returnsPage() {
    PageRequest pageable = PageRequest.of(0, 10);

    List<Object[]> content = List.of(
        new Object[]{
            "TV", "Samsung", new BigDecimal("500.00"), new BigDecimal("25.00"),
            LocalDateTime.now(), LocalDateTime.now().plusDays(1), 2L, UUID.randomUUID()
        },
        new Object[]{
            "Phone", "iPhone 14", new BigDecimal("10.00"), new BigDecimal("1.00"),
            LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, UUID.randomUUID()
        }
    );
    Page<Object[]> rawPage = new PageImpl<>(content, pageable, 2);

    when(lotRepository.findLotsWithMetadata(pageable)).thenReturn(rawPage);

    Page<LotResponse> result = lotService.findLotsWithMetadata(pageable);

    assertThat(result.getContent()).hasSize(2);
    verify(lotRepository).findLotsWithMetadata(pageable);
}

    @Test
    void findLotWithMetadataById_found_returnsLotResponse() {
        Long lotId = 1L;
        Lot lot = new Lot();
        lot.setId(lotId);
        lot.setCurrentCost(BigDecimal.TEN);

        MetaDataLot meta = new MetaDataLot();
        meta.setLotId(lotId);
        meta.setName("Phone");
        meta.setDescription("iPhone");
        UUID uuid = UUID.randomUUID();

        when(lotRepository.findById(lotId)).thenReturn(Optional.of(lot));
        when(metaDataLotRepository.findByLotId(lotId)).thenReturn(Optional.of(meta));
        when(photoRepository.findUuidByLotId(lotId)).thenReturn(uuid);

        LotResponse result = lotService.findLotWithMetadataById(lotId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Phone");
        assertThat(result.getUuid()).isEqualTo(uuid);
    }

    @Test
    void findLotWithMetadataById_notFound_throwsException() {
        Long lotId = 999L;
        when(lotRepository.findById(lotId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lotService.findLotWithMetadataById(lotId))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void buyLot_validBid_updatesLot() {
        Long buyerId = 20L;
        BuyLotRequest request = new BuyLotRequest();
        request.setLotId(1L);
        request.setReqCost(BigDecimal.valueOf(150));

        Lot lot = new Lot();
        lot.setId(1L);
        lot.setCurrentCost(BigDecimal.valueOf(100));
        lot.setRateStep(BigDecimal.valueOf(50));

        when(lotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(lotRepository.save(any(Lot.class))).thenReturn(lot);

        Lot result = lotService.buyLot(buyerId, request);

        assertThat(result.getCurrentCost()).isEqualTo(BigDecimal.valueOf(150));
        assertThat(result.getBuyerId()).isEqualTo(buyerId);
    }

    @Test
    void buyLot_invalidBid_throwsException() {
        Long buyerId = 20L;
        BuyLotRequest request = new BuyLotRequest();
        request.setLotId(1L);
        request.setReqCost(BigDecimal.valueOf(140));

        Lot lot = new Lot();
        lot.setId(1L);
        lot.setCurrentCost(BigDecimal.valueOf(100));
        lot.setRateStep(BigDecimal.valueOf(50));

        when(lotRepository.findById(1L)).thenReturn(Optional.of(lot));

        assertThatThrownBy(() -> lotService.buyLot(buyerId, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Bid must be at least");
    }
}
