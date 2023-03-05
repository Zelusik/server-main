package com.zelusik.eatery.app.domain.place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Address {

    @Column(nullable = false)
    private String sido;

    @Column(nullable = false)
    private String sgg;

    @Column(nullable = false)
    private String lotNumberAddress;

    @Column(nullable = false)
    private String roadAddress;

    public Address(String lotNumberAddress, String roadAddress) {
        String address;
        if (StringUtils.hasText(lotNumberAddress)) {
            address = lotNumberAddress;
        } else {
            address = roadAddress;
        }

        int sidoIdx = address.indexOf(" ");
        int sggIdx = address.indexOf(" ", sidoIdx + 1);

        this.sido = address.substring(0, sidoIdx);
        this.sgg = address.substring(sidoIdx + 1, sggIdx);
        this.lotNumberAddress = StringUtils.hasText(lotNumberAddress) ? lotNumberAddress.substring(sggIdx + 1) : null;
        this.roadAddress = StringUtils.hasText(roadAddress) ? roadAddress.substring(sggIdx + 1) : null;
    }
}

