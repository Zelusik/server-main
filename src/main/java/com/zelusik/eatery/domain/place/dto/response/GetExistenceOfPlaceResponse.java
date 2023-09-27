package com.zelusik.eatery.domain.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GetExistenceOfPlaceResponse {

    @Schema(description = "장소의 존재 여부", example = "true")
    private boolean existenceOfPlace;
}
