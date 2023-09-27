package com.zelusik.eatery.domain.place.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Address {

    @Schema(description = "시/도", example = "서울")
    @NonNull
    @Column(nullable = false)
    private String sido;

    @Schema(description = "시/군/구", example = "마포구")
    @NonNull
    @Column(nullable = false)
    private String sgg;

    @Schema(description = "지번주소", example = "연남동 568-26")
    @Nullable
    private String lotNumberAddress;

    @Schema(description = "도로명주소", example = "월드컵북로6길 61")
    @Nullable
    private String roadAddress;

    public static Address of(String lotNumberAddress, String roadAddress) {
        String address;
        if (StringUtils.hasText(lotNumberAddress)) {
            address = lotNumberAddress;
        } else {
            address = roadAddress;
        }

        int sidoIdx = address.indexOf(" ");
        int sggIdx = address.indexOf(" ", sidoIdx + 1);

        return new Address(
                address.substring(0, sidoIdx),
                address.substring(sidoIdx + 1, sggIdx),
                StringUtils.hasText(lotNumberAddress) ? lotNumberAddress.substring(sggIdx + 1) : null,
                StringUtils.hasText(roadAddress) ? roadAddress.substring(sggIdx + 1) : null
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address that)) return false;
        return Objects.equals(this.getSido(), that.getSido())
               && Objects.equals(this.getSgg(), that.getSgg())
               && Objects.equals(this.getLotNumberAddress(), that.getLotNumberAddress())
               && Objects.equals(this.getRoadAddress(), that.getRoadAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSido(), getSgg(), getLotNumberAddress(), getRoadAddress());
    }
}

