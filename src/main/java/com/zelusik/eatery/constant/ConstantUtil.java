package com.zelusik.eatery.constant;

import com.zelusik.eatery.controller.MeetingPlaceController;
import com.zelusik.eatery.dto.apple.AppleOAuthUserResponse;
import com.zelusik.eatery.dto.kakao.KakaoOAuthUserResponse;
import com.zelusik.eatery.repository.place.PlaceRepositoryJCustomImpl;

public class ConstantUtil {

    /**
     * @see KakaoOAuthUserResponse
     * @see AppleOAuthUserResponse
     */
    public static final String defaultProfileImageUrl = "https://eatery-s3-bucket.s3.ap-northeast-2.amazonaws.com/member/default-profile-image";
    public static final String defaultProfileThumbnailImageUrl = "https://eatery-s3-bucket.s3.ap-northeast-2.amazonaws.com/member/default-profile-image";

    /**
     * @see PlaceRepositoryJCustomImpl
     */
    public static final int MAX_NUM_OF_FILTERING_KEYWORDS = 8;

    /**
     * @see MeetingPlaceController
     */
    public static final int PAGE_SIZE_OF_SEARCHING_MEETING_PLACES = 15;
}
