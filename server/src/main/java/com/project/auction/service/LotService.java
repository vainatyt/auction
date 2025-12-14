package com.project.auction.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.auction.models.Lot;
import com.project.auction.models.MetaDataLot;
import com.project.auction.models.TrackableItem;
import com.project.auction.pojo.BuyLotRequest;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.pojo.LotResponse;
import com.project.auction.repository.LotRepository;
import com.project.auction.repository.MetaDataLotRepository;
import com.project.auction.repository.PhotoRepository;
import com.project.auction.repository.TrackableItemRepository;
import com.project.auction.repository.UserRepository;

@Service
public class LotService {

    private static final Logger log = LoggerFactory.getLogger(LotService.class);
    
    private final LotRepository lotRepository;
    private final MetaDataLotRepository metaDataLotRepository;
    private final PhotoRepository photoRepository;
    private final TrackableItemRepository trackableItemRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final ImageService imageService;

    public LotService(
            LotRepository lotRepository,
            MetaDataLotRepository metaDataLotRepository,
            PhotoRepository photoRepository,
            TrackableItemRepository trackableItemRepository,
            UserRepository userRepository,
            MailService mailService,
            ImageService imageService) {
        this.lotRepository = lotRepository;
        this.metaDataLotRepository = metaDataLotRepository;
        this.photoRepository = photoRepository;
        this.trackableItemRepository = trackableItemRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.imageService = imageService;
    }

    @Transactional
    public Lot createLot(Long userId, CreateLotRequest req, MultipartFile image) {
        log.info("Creating lot for userId: {}", userId);
        
        try {
            Lot lot = new Lot(userId, req);
            lot = lotRepository.save(lot);
            
            MetaDataLot metadata = new MetaDataLot(lot.getId(),req.getGoodsName(),req.getGoodsDescription());
            metaDataLotRepository.save(metadata);
            
            imageService.savePhoto(lot.getId(), image);
            
            log.info("Lot created successfully: id={}, userId={}", lot.getId(), userId);
            return lot;
            
        } catch (Exception e) {
            log.error("Failed to create lot for userId={}", userId, e);
            throw new RuntimeException("Failed to create lot", e);
        }
    }

    @Transactional
    public void checkAndCloseExpiredLots() {
        log.info("Checking expired lots");
        LocalDateTime now = LocalDateTime.now();
        List<Lot> expiredLots = lotRepository.findExpiredLots(now);
        
        log.info("Found {} expired lots", expiredLots.size());
        
        for (Lot lot : expiredLots) {
            try {
                closeLot(lot);
            } catch (Exception e) {
                log.error("Failed to close lotId={}", lot.getId(), e);
            }
        }
    }
    
    private void closeLot(Lot lot) {
        Long lotId = lot.getId();
        log.debug("Closing lot: {}", lotId);
        
        // Уведомляем продавца
        userRepository.findById(lot.getOwnerId()).ifPresent(seller -> {
            if (seller.getEmail() != null) {
                mailService.sendLotFinishedMail(seller.getEmail(), lotId, lot.getCurrentCost(), true);
                log.debug("Notified seller: {} for lot {}", seller.getEmail(), lotId);
            }
        });
        
        // Уведомляем трекеров
        List<TrackableItem> trackers = trackableItemRepository.findByLotId(lotId);
        for (TrackableItem tracker : trackers) {
            userRepository.findById(tracker.getUserId()).ifPresent(user -> {
                if (user.getEmail() != null) {
                    mailService.sendLotFinishedMail(user.getEmail(), lotId, lot.getCurrentCost(), false);
                    log.debug("Notified tracker: {} for lot {}", user.getEmail(), lotId);
                }
            });
        }
        
        lotRepository.delete(lot);
        log.info("Lot {} closed and deleted", lotId);
    }

    @Transactional(readOnly = true)
    public Page<LotResponse> findUserLotsWithMetadata(Long ownerId, Pageable pageable) {
        log.debug("Finding lots for sellerId={}, page={}", ownerId, pageable.getPageNumber());
        Page<Object[]> rawPage = lotRepository.findUserLotsWithMetadata(ownerId, pageable);
        Page<LotResponse> result = rawPage.map(row -> new LotResponse(
            (String)row[0], (String)row[1], (BigDecimal)row[2],
            (BigDecimal)row[3], (LocalDateTime)row[4], (LocalDateTime)row[5],
            ((Number)row[6]).longValue(), (UUID)row[7]));
        return result;
    }

    @Transactional(readOnly = true)
    public Page<LotResponse> findLotsWithMetadata(Pageable pageable) {
        log.debug("Finding all lots, page={}", pageable.getPageNumber());
        Page<Object[]> rawPage = lotRepository.findLotsWithMetadata(pageable);
        Page<LotResponse> result = rawPage.map(row -> new LotResponse(
            (String)row[0], (String)row[1], (BigDecimal)row[2],
            (BigDecimal)row[3], (LocalDateTime)row[4], (LocalDateTime)row[5],
            ((Number)row[6]).longValue(), (UUID)row[7]));
        return result;
    }

    @Transactional(readOnly = true)
    public LotResponse findLotWithMetadataById(Long id) {
        log.debug("Finding lot by id: {}", id);
        Lot lot = lotRepository.findById(id).orElseThrow(() -> {
                log.warn("Lot not found: id={}", id);
                return new IllegalArgumentException("Lot not found: " + id);
            });
        MetaDataLot meta = metaDataLotRepository.findByLotId(id).orElseThrow(() -> {
                log.warn("Meta data of lot not found: id={}", id);
                return new IllegalArgumentException("Meta data of lot not found: " + id);
            });
        return new LotResponse(lot, meta, photoRepository.findUuidByLotId(id));
            
    }

    @Transactional
    public Lot buyLot(Long buyerId, BuyLotRequest request) {
        log.info("Buy attempt: buyerId={}, lotId={}", buyerId, request.getLotId());
        
        Lot lot = lotRepository.findById(request.getLotId())
            .orElseThrow(() -> {
                log.warn("Lot not found for buy: id={}", request.getLotId());
                return new IllegalArgumentException("Lot not found");
            });
        
        validateBid(lot, request.getReqCost());
        
        lot.setCurrentCost(request.getReqCost());
        lot.setBuyerId(buyerId);
        
        Lot savedLot = lotRepository.save(lot);
        log.info("Lot {} bought by user {}", savedLot.getId(), buyerId);
        return savedLot;
    }
    
    private void validateBid(Lot lot, BigDecimal bid) {
        if (bid.compareTo(lot.getCurrentCost().add(lot.getRateStep())) < 0) {
            log.warn("Invalid bid: lotId={}, bid={}, minRequired={}", 
                    lot.getId(), bid, lot.getCurrentCost().add(lot.getRateStep()));
            throw new IllegalArgumentException("Bid must be at least " + lot.getRateStep() + " higher than current price");
        }
    }
}
