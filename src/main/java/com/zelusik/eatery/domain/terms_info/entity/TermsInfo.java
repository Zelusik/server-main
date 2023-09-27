package com.zelusik.eatery.domain.terms_info.entity;

import com.zelusik.eatery.global.common.entity.BaseTimeEntity;
import com.zelusik.eatery.domain.member.entity.Member;
import lombok.AccessLevel;
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

    @JoinColumn(name = "member_id", unique = true, nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

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

    public static TermsInfo of(Member member, Boolean isNotMinor, Boolean service, LocalDateTime serviceUpdatedAt, Boolean userInfo, LocalDateTime userInfoUpdatedAt, Boolean locationInfo, LocalDateTime locationInfoUpdatedAt, Boolean marketingReception, LocalDateTime marketingReceptionUpdatedAt) {
        return of(null, member, isNotMinor, service, serviceUpdatedAt, userInfo, userInfoUpdatedAt, locationInfo, locationInfoUpdatedAt, marketingReception, marketingReceptionUpdatedAt, null, null);
    }

    public static TermsInfo of(Long id, Member member, Boolean isNotMinor, Boolean service, LocalDateTime serviceUpdatedAt, Boolean userInfo, LocalDateTime userInfoUpdatedAt, Boolean locationInfo, LocalDateTime locationInfoUpdatedAt, Boolean marketingReception, LocalDateTime marketingReceptionUpdatedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new TermsInfo(id, member, isNotMinor, service, serviceUpdatedAt, userInfo, userInfoUpdatedAt, locationInfo, locationInfoUpdatedAt, marketingReception, marketingReceptionUpdatedAt, createdAt, updatedAt);
    }

    private TermsInfo(Long id, Member member, Boolean isNotMinor, Boolean service, LocalDateTime serviceUpdatedAt, Boolean userInfo, LocalDateTime userInfoUpdatedAt, Boolean locationInfo, LocalDateTime locationInfoUpdatedAt, Boolean marketingReception, LocalDateTime marketingReceptionUpdatedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.member = member;
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
