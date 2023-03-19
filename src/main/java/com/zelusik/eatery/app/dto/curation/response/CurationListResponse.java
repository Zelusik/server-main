package com.zelusik.eatery.app.dto.curation.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationListResponse {

    private List<CurationResponse> curations;

    public static CurationListResponse of(List<CurationResponse> curations) {
        return new CurationListResponse(curations);
    }
}
