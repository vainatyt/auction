package com.project.auction.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.auction.models.BuyLot;
import com.project.auction.models.Lot;
import com.project.auction.models.MetaDataLot;
import com.project.auction.models.User;
import com.project.auction.pojo.BuyLotRequest;
import com.project.auction.pojo.BuyStatus;
import com.project.auction.repository.BuyLotRepository;
import com.project.auction.repository.MetaDataLotRepository;
import com.project.auction.repository.UserRepository;

import jakarta.transaction.Transactional;

@Component
public class BuyLotScheduler {
    private final LotService lotService;
    private final BuyLotRepository buyLotRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final MetaDataLotRepository metaDataLotRepository;

    private static final Logger log = LoggerFactory.getLogger(BuyLotRepository.class);

    public BuyLotScheduler(LotService lotService,
                            BuyLotRepository buyLotRepository,
                            MailService mailService,
                            UserRepository userRepository,
                            MetaDataLotRepository metaDataLotRepository){
        this.lotService = lotService;
        this.buyLotRepository = buyLotRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.metaDataLotRepository = metaDataLotRepository;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void checkBuyStatus(){
        List<BuyLot> pending = buyLotRepository.findByStatusOrderByDateAsc(BuyStatus.PENDING);
        log.info("find {} pending requests",pending.size());
        for(BuyLot req: pending){
            User user = userRepository.findById(req.getUserId()).orElseThrow(()->{
                log.warn("user={} not found", req.getUserId());
                return new RuntimeException("user not found");
            });
            MetaDataLot metaDataLot = metaDataLotRepository.findByLotId(req.getLotId()).orElseThrow(()->{
                log.warn("lot={} not found", req.getLotId());
                return new RuntimeException("meta data not found");
            });
            BuyLotRequest buyLotRequest = new BuyLotRequest(req.getLotId(),req.getReqCost());
            try {
                Lot lot = lotService.buyLot(req.getUserId(), buyLotRequest);
                req.setStatus(BuyStatus.PROCESSED);
                buyLotRepository.save(req);
                log.info("User {} bought lot {}", req.getUserId(), lot.getId());
                mailService.notifyBuyReq(user, metaDataLot, BuyStatus.PROCESSED);
            } catch (IllegalArgumentException e) {
                log.warn("Failed to process buy lot {} for user {}",req.getLotId(), user.getName(), e);
                req.setStatus(BuyStatus.REJECTED);
                buyLotRepository.save(req);
                mailService.notifyBuyReq(user, metaDataLot, BuyStatus.REJECTED);
            } catch (RuntimeException e) {
                log.error("Failed to process buy lot {} for user {}",req.getLotId(), user.getName(), e);
                req.setStatus(BuyStatus.REJECTED);
                buyLotRepository.save(req);
                mailService.notifyBuyReq(user, metaDataLot, BuyStatus.REJECTED);
            }
        }
    }
}
