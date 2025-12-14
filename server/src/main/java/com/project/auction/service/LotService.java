package com.project.auction.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private MetaDataLotRepository metaDataLotRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private TrackableItemRepository trackableItemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private ImageService imageService;

    @Transactional
    public Lot createLot(Long userId, CreateLotRequest req, MultipartFile image) {
        Lot lot = new Lot(userId, req);
        lot = lotRepository.save(lot);

        String sql = "INSERT INTO metadata_lot (id_lot, name, description) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql, lot.getId(), req.getGoodsName(), req.getGoodsDescription());

        imageService.savePhoto(lot.getId(),image);

        return lot;
    }

    @Transactional
    public void checkAndCloseExpiredLots() {
    LocalDateTime now = LocalDateTime.now();
    List<Lot> lots = lotRepository.findExpiredLots(now);

        for (Lot lot : lots) {
            Long lotId = lot.getId();

            // уведомляем владельца лота
            userRepository.findById(lot.getOwnerId()).ifPresent(owner -> {
                if (owner.getEmail() != null) {
                    mailService.sendLotFinishedMail(
                            owner.getEmail(),
                            lotId,
                            lot.getCurrentCost(),
                            true
                    );
                }
            });

        // уведомляем всех, кто отслеживает лот
        // предполагается метод репозитория, возвращающий все TrackableItem по lotId
        List<TrackableItem> trackables = trackableItemRepository.findByUserId(lotId);
        for (TrackableItem t : trackables) {
            Long userId = t.getUserId();
            userRepository.findById(userId).ifPresent(user -> {
                if (user.getEmail() != null) {
                    mailService.sendLotFinishedMail(
                            user.getEmail(),
                            lotId,
                            lot.getCurrentCost(),
                            false
                    );
                }
            });
        }

        // удалить метаданные, фото, трекаблы и сам лот
        metaDataLotRepository.deleteByLotId(lotId);
        photoRepository.deleteByLotId(lotId);
        trackableItemRepository.deleteById_LotId(lotId);
        lotRepository.delete(lot);
    }
}

  


    @Transactional
    public Page<Lot> getUserLots(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lot> pageLot = lotRepository.findByOwnerId(userId, pageable);
        return pageLot;
    }

    @Transactional
    public Page<Lot> getUserLots(Long userId, Pageable pageable) {
        Page<Lot> pageLot = lotRepository.findByOwnerId(userId, pageable);
        return pageLot;
    }

    @Transactional
    public Page<LotResponse> findUserLotsWithMetadata(Long ownerId, Pageable pageable) {
        Page<Object[]> rawPage = lotRepository.findUserLotsWithMetadata(ownerId, pageable);
        Page<LotResponse> result = rawPage.map(row -> new LotResponse(
            (String)row[0], (String)row[1], (BigDecimal)row[2],
            (BigDecimal)row[3], (LocalDateTime)row[4], (LocalDateTime)row[5],
            ((Number)row[6]).longValue(), (UUID)row[7]));
        return result;
    }

    @Transactional
     public Page<LotResponse> findLotsWithMetadata(Pageable pageable) {
        Page<Object[]> rawPage = lotRepository.findLotsWithMetadata(pageable);
        Page<LotResponse> result = rawPage.map(row -> new LotResponse(
            (String)row[0], (String)row[1], (BigDecimal)row[2],
            (BigDecimal)row[3], (LocalDateTime)row[4], (LocalDateTime)row[5],
            ((Number)row[6]).longValue(), (UUID)row[7]));
        return result;
    }

    @Transactional
    public LotResponse findLotWithMetadataById(Long id){
        Optional<Lot> lot = lotRepository.findById(id);
        Optional<MetaDataLot> meta = metaDataLotRepository.findByLotId(id);
        return new LotResponse(lot.get(), meta.get(), photoRepository.findUuidByLotId(id));
    }

    @Transactional
    public Lot buyLot(Long userId, BuyLotRequest buyLotRequest){
        Lot lot = lotRepository.findById(buyLotRequest.getLotId()).get();
        lot.setCurrentCost(buyLotRequest.getReqCost());
        lot.setBuyerId(userId);
        return lotRepository.save(lot);
    }
}
