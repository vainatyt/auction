package com.project.auction.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.project.auction.models.Lot;
import com.project.auction.models.User;
import com.project.auction.pojo.BuyLotRequest;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.pojo.LotResponse;
import com.project.auction.pojo.MessageResponse;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.LotService;
import com.project.auction.service.TrackableItemService;

@RestController
@RequestMapping("/lots")
public class LotController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LotService lotService;

    @Autowired
    private TrackableItemService trackableItemService;

    @PostMapping("/create")
    public ResponseEntity<?> createLot(@RequestBody CreateLotRequest request) {
        System.out.println("start create lot");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username);
        lotService.createLot(user.get().getId(), request);
        return ResponseEntity.ok(new MessageResponse("lot is created"));
    }

    @GetMapping("/getmy")
    public ResponseEntity<?> getMyLots(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username);
        Page<LotResponse> my_lots_page = lotService.findUserLotsWithMetadata(user.get().getId(),PageRequest.of(page, size));
        return ResponseEntity.ok(my_lots_page);
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getAllLots(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size){
        Page<LotResponse> lots_page = lotService.findLotsWithMetadata(PageRequest.of(page, size));
        return ResponseEntity.ok(lots_page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLot(@PathVariable Long id) {
        System.out.println("get lot");
        LotResponse lot = lotService.findLotWithMetadataById(id);
        return ResponseEntity.ok(lot);
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyLot(BuyLotRequest buyLotRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username);
        lotService.buyLot(user.get().getId(), buyLotRequest);
        return ResponseEntity.ok("user buy lot "+buyLotRequest.getLotId());
    }


}