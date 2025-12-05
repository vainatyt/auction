package com.project.auction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.auction.models.Lot;
import com.project.auction.payload.request.CreateLotRequest;
import com.project.auction.service.LotService;

@RestController
@RequestMapping("/api/lots")
public class LotController {

    private final LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @PostMapping
    public ResponseEntity<Lot> createLot(@RequestBody CreateLotRequest request) {
        Lot lot = lotService.createLot(request);
        return ResponseEntity.ok(lot);
    }
}
