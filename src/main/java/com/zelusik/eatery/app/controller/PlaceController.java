package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.dto.place.response.PlaceResponse;
import com.zelusik.eatery.app.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/places")
@RestController
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/{placeId}")
    public PlaceResponse find(@PathVariable Long placeId) {
        return PlaceResponse.from(placeService.findDtoById(placeId));
    }
}
