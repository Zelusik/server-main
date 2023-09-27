package com.zelusik.eatery.global.kakao.exception;

import org.springframework.http.HttpStatus;

public class KakaoTokenValidateException extends KakaoServerException {

    public KakaoTokenValidateException(Integer errorCode, String errorMessage, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, errorCode, errorMessage, cause);
    }
}
