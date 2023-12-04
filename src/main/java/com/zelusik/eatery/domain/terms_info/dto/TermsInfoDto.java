package com.zelusik.eatery.domain.terms_info.dto;

import com.zelusik.eatery.domain.terms_info.entity.TermsInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class TermsInfoDto {

    private Long id;
    private Long memberId;
    private Boolean isNotMinor;
    private Boolean service;
    private LocalDateTime serviceUpdatedAt;
    private Boolean userInfo;
    private LocalDateTime userInfoUpdatedAt;
    private Boolean locationInfo;
    private LocalDateTime locationInfoUpdatedAt;
    private Boolean marketingReception;
    private LocalDateTime marketingReceptionUpdatedAt;

    public static TermsInfoDto from(TermsInfo entity) {
        if (entity == null) {
            return null;
        }

        return new TermsInfoDto(
                entity.getId(),
                entity.getMember().getId(),
                entity.getIsNotMinor(),
                entity.getService(),
                entity.getServiceUpdatedAt(),
                entity.getUserInfo(),
                entity.getUserInfoUpdatedAt(),
                entity.getLocationInfo(),
                entity.getLocationInfoUpdatedAt(),
                entity.getMarketingReception(),
                entity.getMarketingReceptionUpdatedAt()
        );
    }
}
