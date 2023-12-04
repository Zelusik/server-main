package com.zelusik.eatery.global.kakao.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KakaoServerException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
    private final Throwable cause;

    public KakaoServerException(HttpStatus httpStatus, Integer errorCode, String errorMessage, Throwable cause) {
        CustomExceptionType exceptionType = CustomExceptionType.KAKAO_SERVER;
        this.httpStatus = httpStatus;
        this.code = exceptionType.getCode() + errorCode;
        this.message = exceptionType.getMessage() + " " + errorMessage;
        this.cause = cause;
    }

    public KakaoServerException(HttpStatus httpStatus, CustomExceptionType exceptionType, Integer errorCode, String errorMessage, Throwable cause) {
        this.httpStatus = httpStatus;
        this.code = exceptionType.getCode() + errorCode;
        this.message = exceptionType.getMessage() + " " + errorMessage;
        this.cause = cause;
    }
}
