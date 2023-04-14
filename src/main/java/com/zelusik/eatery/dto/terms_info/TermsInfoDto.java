package com.zelusik.eatery.dto.terms_info;

import com.zelusik.eatery.domain.member.TermsInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TermsInfoDto {

    private Long id;
    private Boolean isNotMinor;
    private Boolean service;
    private LocalDateTime serviceUpdatedAt;
    private Boolean userInfo;
    private LocalDateTime userInfoUpdatedAt;
    private Boolean locationInfo;
    private LocalDateTime locationInfoUpdatedAt;
    private Boolean marketingReception;
    private LocalDateTime marketingReceptionUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
