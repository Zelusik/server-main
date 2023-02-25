package com.zelusik.eatery.app.domain;

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

    @Column(nullable = false)
    private Boolean isMinor;

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
}
