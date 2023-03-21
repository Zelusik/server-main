package com.zelusik.eatery.app.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class TermsInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terms_info_id")
    private Long id;

    @Column(nullable = false)
    private Boolean isNotMinor;

    @Column(nullable = false)
    private Boolean service;
    @Column(nullable = false)
    private LocalDateTime serviceUpdatedAt;

    @Column(nullable = false)
    private Boolean userInfo;
    @Column(nullable = false)
    private LocalDateTime userInfoUpdatedAt;

    @Column(nullable = false)
    private Boolean locationInfo;
    @Column(nullable = false)
    private LocalDateTime locationInfoUpdatedAt;

    @Column(nullable = false)
    private Boolean marketingReception;
    @Column(nullable = false)
    private LocalDateTime marketingReceptionUpdatedAt;

    public static TermsInfo of(Boolean isNotMinor, Boolean service, LocalDateTime serviceUpdatedAt, Boolean userInfo, LocalDateTime userInfoUpdatedAt, Boolean locationInfo, LocalDateTime locationInfoUpdatedAt, Boolean marketingReception, LocalDateTime marketingReceptionUpdatedAt) {
        return of(null, isNotMinor, service, serviceUpdatedAt, userInfo, userInfoUpdatedAt, locationInfo, locationInfoUpdatedAt, marketingReception, marketingReceptionUpdatedAt, null, null);
    }

    public static TermsInfo of(Long id, Boolean isNotMinor, Boolean service, LocalDateTime serviceUpdatedAt, Boolean userInfo, LocalDateTime userInfoUpdatedAt, Boolean locationInfo, LocalDateTime locationInfoUpdatedAt, Boolean marketingReception, LocalDateTime marketingReceptionUpdatedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return TermsInfo.builder()
                .id(id)
                .isNotMinor(isNotMinor)
                .service(service)
                .serviceUpdatedAt(serviceUpdatedAt)
                .userInfo(userInfo)
                .userInfoUpdatedAt(userInfoUpdatedAt)
                .locationInfo(locationInfo)
                .locationInfoUpdatedAt(locationInfoUpdatedAt)
                .marketingReception(marketingReception)
                .marketingReceptionUpdatedAt(marketingReceptionUpdatedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private TermsInfo(LocalDateTime createdAt, LocalDateTime updatedAt, Long id, Boolean isNotMinor, Boolean service, LocalDateTime serviceUpdatedAt, Boolean userInfo, LocalDateTime userInfoUpdatedAt, Boolean locationInfo, LocalDateTime locationInfoUpdatedAt, Boolean marketingReception, LocalDateTime marketingReceptionUpdatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.isNotMinor = isNotMinor;
        this.service = service;
        this.serviceUpdatedAt = serviceUpdatedAt;
        this.userInfo = userInfo;
        this.userInfoUpdatedAt = userInfoUpdatedAt;
        this.locationInfo = locationInfo;
        this.locationInfoUpdatedAt = locationInfoUpdatedAt;
        this.marketingReception = marketingReception;
        this.marketingReceptionUpdatedAt = marketingReceptionUpdatedAt;
    }
}
