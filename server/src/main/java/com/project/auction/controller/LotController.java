package com.project.auction.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.project.auction.models.Lot;
import com.project.auction.models.User;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.pojo.MessageResponse;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.LotService;

@RestController
@RequestMapping("/lots")
public class LotController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LotService lotService;

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
    public ResponseEntity<?> getLots(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByName(username);
        Page<Lot> my_lots_page = lotService.getUserLots(user.get().getId(), page, size);
        return ResponseEntity.ok(my_lots_page);
    }

}