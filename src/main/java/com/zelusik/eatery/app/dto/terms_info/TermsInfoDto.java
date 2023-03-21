package com.zelusik.eatery.app.dto.terms_info;

import com.zelusik.eatery.app.domain.TermsInfo;

import java.time.LocalDateTime;

public record TermsInfoDto(
        Long id,
        Boolean isNotMinor,
        Boolean service,
        LocalDateTime serviceUpdatedAt,
        Boolean userInfo,
        LocalDateTime userInfoUpdatedAt,
        Boolean locationInfo,
        LocalDateTime locationInfoUpdatedAt,
        Boolean marketingReception,
        LocalDateTime marketingReceptionUpdatedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TermsInfoDto of(Long id, Boolean isNotMinor, Boolean service, LocalDateTime serviceUpdatedAt, Boolean userInfo, LocalDateTime userInfoUpdatedAt, Boolean locationInfo, LocalDateTime locationInfoUpdatedAt, Boolean marketingReception, LocalDateTime marketingReceptionUpdatedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new TermsInfoDto(id, isNotMinor, service, serviceUpdatedAt, userInfo, userInfoUpdatedAt, locationInfo, locationInfoUpdatedAt, marketingReception, marketingReceptionUpdatedAt, createdAt, updatedAt);
    }

    public static TermsInfoDto from(TermsInfo entity) {
        if (entity == null) {
            return null;
        }

        return of(
                entity.getId(),
                entity.getIsNotMinor(),
                entity.getService(),
                entity.getServiceUpdatedAt(),
                entity.getUserInfo(),
                entity.getUserInfoUpdatedAt(),
                entity.getLocationInfo(),
                entity.getLocationInfoUpdatedAt(),
                entity.getMarketingReception(),
                entity.getMarketingReceptionUpdatedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
