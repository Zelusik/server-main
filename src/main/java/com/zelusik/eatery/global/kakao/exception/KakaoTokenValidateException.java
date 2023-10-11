package com.zelusik.eatery.global.kakao.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import org.springframework.http.HttpStatus;

public class KakaoTokenValidateException extends KakaoServerException {

    public KakaoTokenValidateException(Integer errorCode, String errorMessage, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, CustomExceptionType.KAKAO_TOKEN_VALIDATE, errorCode, errorMessage, cause);
    }
}
