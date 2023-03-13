package com.zelusik.eatery.global.exception.kakao;

import com.zelusik.eatery.global.exception.ExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KakaoServerException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
    private final Throwable cause;

    public KakaoServerException(HttpStatus httpStatus, Integer errorCode, String errorMessage, Throwable cause) {
        ExceptionType exceptionType = ExceptionType.from(this.getClass()).orElse(ExceptionType.KAKAO_SERVER);
        this.httpStatus = httpStatus;
        this.code = exceptionType.getCode() + errorCode;
        this.message = exceptionType.getMessage() + " " + errorMessage;
        this.cause = cause;
    }
}
