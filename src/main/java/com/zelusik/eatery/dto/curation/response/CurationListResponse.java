package com.zelusik.eatery.dto.curation.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationListResponse {

    private List<CurationResponse> curations;

    public static CurationListResponse of(List<CurationResponse> curations) {
        return new CurationListResponse(curations);
    }
}
