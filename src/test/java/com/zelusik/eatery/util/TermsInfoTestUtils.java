package com.zelusik.eatery.util;

import com.zelusik.eatery.app.domain.member.TermsInfo;

import java.time.LocalDateTime;

public class TermsInfoTestUtils {

    public static TermsInfo createTermsInfo() {
        LocalDateTime now = LocalDateTime.now();
        return TermsInfo.of(
                1L,
                false,
                true, now,
                true, now,
                true, now,
                true, now,
                now,
                now
        );
    }
}
