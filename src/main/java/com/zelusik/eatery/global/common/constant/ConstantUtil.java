package com.zelusik.eatery.global.common.constant;

import com.zelusik.eatery.global.apple.dto.AppleOAuthUserInfo;
import com.zelusik.eatery.global.kakao.dto.KakaoOAuthUserInfo;

public class ConstantUtil {

    /**
     * @see KakaoOAuthUserInfo
     * @see AppleOAuthUserInfo
     */
    public static final String defaultProfileImageUrl = "https://eatery-s3-bucket.s3.ap-northeast-2.amazonaws.com/member/default-profile-image";
    public static final String defaultProfileThumbnailImageUrl = "https://eatery-s3-bucket.s3.ap-northeast-2.amazonaws.com/member/default-profile-image";

    public static final String API_MINOR_VERSION_HEADER_NAME = "Eatery-API-Minor-Version";
}
