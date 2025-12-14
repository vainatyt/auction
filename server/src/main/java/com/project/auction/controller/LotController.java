package com.project.auction.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.project.auction.models.Lot;
import com.project.auction.models.User;
import com.project.auction.pojo.BuyLotRequest;
import com.project.auction.pojo.CreateLotRequest;
import com.project.auction.pojo.LotResponse;
import com.project.auction.pojo.MessageResponse;
import com.project.auction.repository.UserRepository;
import com.project.auction.service.LotService;

@RestController
@RequestMapping("/lots")
public class LotController {

    private static final Logger log = LoggerFactory.getLogger(LotController.class);
    
    private final UserRepository userRepository;
    private final LotService lotService;

    public LotController(UserRepository userRepository, LotService lotService) {
        this.userRepository = userRepository;
        this.lotService = lotService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createLot(
            @RequestPart("request") CreateLotRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        
        log.debug("Creating lot with image: {}", image != null ? image.getOriginalFilename() : "none");
        
        User user = getCurrentUser();
        
        try {
            lotService.createLot(user.getId(), request, image);
            log.info("Lot created by user {}", user.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Lot created successfully"));
                
        } catch (IllegalArgumentException e) {
            log.warn("Invalid lot data from user {}: {}", user.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Failed to create lot for user {}", user.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getmy")
    public ResponseEntity<Page<LotResponse>> getMyLots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User user = getCurrentUser();
        
        try {
            Page<LotResponse> myLots = lotService.findUserLotsWithMetadata(
                user.getId(), PageRequest.of(page, size));
            return ResponseEntity.ok(myLots);
            
        } catch (RuntimeException e) {
            log.error("Failed to get lots for user {}", user.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load lots");
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<Page<LotResponse>> getAllLots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Page<LotResponse> lots = lotService.findLotsWithMetadata(PageRequest.of(page, size));
            return ResponseEntity.ok(lots);
            
        } catch (RuntimeException e) {
            log.error("Failed to get all lots", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load lots");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotResponse> getLot(@PathVariable Long id) {
        try {
            LotResponse lot = lotService.findLotWithMetadataById(id);
            return ResponseEntity.ok(lot);
            
        } catch (IllegalArgumentException e) {
            log.warn("Lot not found: id={}", id);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Failed to get lot {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/buy")
    public ResponseEntity<Lot> buyLot(@RequestBody BuyLotRequest buyLotRequest) {
        User user = getCurrentUser();
        
        try {
            Lot lot = lotService.buyLot(user.getId(), buyLotRequest);
            log.info("User {} bought lot {}", user.getName(), lot.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(lot);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid buy request from user {}: {}", user.getName(), e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Failed to process buy for user {}", user.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
