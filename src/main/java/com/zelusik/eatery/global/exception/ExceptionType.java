package com.zelusik.eatery.global.exception;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.global.exception.auth.RedisRefreshTokenNotFoundException;
import com.zelusik.eatery.global.exception.auth.TokenValidateException;
import com.zelusik.eatery.global.exception.constant.ValidationErrorCode;
import com.zelusik.eatery.global.exception.file.MultipartFileNotReadableException;
import com.zelusik.eatery.global.exception.member.MemberIdNotFoundException;
import com.zelusik.eatery.global.exception.scraping.OpeningHoursUnexpectedFormatException;
import com.zelusik.eatery.global.exception.scraping.ScrapingServerInternalError;
import com.zelusik.eatery.global.log.LogUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.*;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Error code 목록
 *
 * <ul>
 *     <li>1000 ~ 1999: 일반 예외. 아래 항목에 해당하지 않는 대부분의 예외가 여기에 해당한다</li>
 *     <li>120X: Validation 관련 예외</li>
 *     <li>1210 ~ 1299: 구체적인 Validation content에 대한 exception. 해당 내용은 {@link ValidationErrorCode}, {@link GlobalExceptionHandler} 참고)</li>
 *     <li>1300 ~ 1349: API/Controller 관련 예외</li>
 *     <li>1350 ~ 1399: Scraping 서버(Flask) 관련 예외</li>
 *     <li>14XX: DB 관련 예외</li>
 *     <li>15XX: 인증 관련 예외</li>
 *     <li>2XXX: 회원({@link Member}) 관련 예외</li>
 *     <li>3XXX: 장소 관련 예외</li>
 * </ul>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@Getter
public enum ExceptionType {

    /**
     * Global/Normal Exception
     */
    UNHANDLED(1000, "알 수 없는 서버 에러가 발생했습니다.", null),
    MULTIPART_FILE_NOT_READABLE(1001, "파일을 읽을 수 없습니다.", MultipartFileNotReadableException.class),

    /**
     * Validation Exception
     *
     * @see ValidationErrorCode
     */
    METHOD_ARGUMENT_NOT_VALID(1200, "요청 데이터가 잘못되었습니다.", MethodArgumentNotValidException.class),
    CONSTRAINT_VIOLATION(1201, "요청 데이터가 잘못되었습니다.", ConstraintViolationException.class),

    /**
     * Spring MVC Default Exception
     */
    HTTP_REQUEST_METHOD_NOT_SUPPORTED(1300, "지원하지 않는 요청 방식입니다.", HttpRequestMethodNotSupportedException.class),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED(1301, "지원하지 않는 요청 데이터 타입입니다.", HttpMediaTypeNotSupportedException.class),
    HTTP_MEDIA_TYPE_NOT_ACCEPTABLE(1302, "요청한 데이터 타입으로 응답을 만들어 낼 수 없습니다.", HttpMediaTypeNotAcceptableException.class),
    MISSING_PATH_VARIABLE(1303, "필요로 하는 path variable이 누락 되었습니다.", MissingPathVariableException.class),
    MISSING_SERVLET_REQUEST_PARAMETER(1304, "필요로 하는 request parameter가 누락 되었습니다.", MissingServletRequestParameterException.class),
    MISSING_REQUEST_HEADER(1305, "필요로 하는 request header가 누락 되었습니다.", MissingRequestHeaderException.class),
    SERVLET_REQUEST_BINDING(1306, "복구 불가능한 fatal binding exception이 발생했습니다.", ServletRequestBindingException.class),
    CONVERSION_NOT_SUPPORTED(1307, "Bean property에 대해 적절한 editor 또는 convertor를 찾을 수 없습니다.", ConversionNotSupportedException.class),
    TYPE_MISMATCH(1308, "Bean property를 설정하던 중 type mismatch로 인한 예외가 발생했습니다.", TypeMismatchException.class),
    HTTP_MESSAGE_NOT_READABLE(1309, "읽을 수 없는 요청입니다. 요청 정보가 잘못되지는 않았는지 확인해주세요.", HttpMessageNotReadableException.class),
    HTTP_MESSAGE_NOT_WRITABLE(1310, "응답 데이터를 생성할 수 없습니다.", HttpMessageNotWritableException.class),
    MISSING_SERVLET_REQUEST_PART(1311, "multipart/form-data 형식의 요청 데이터에 대해 일부가 손실되거나 누락되었습니다.", MissingServletRequestPartException.class),
    NO_HANDLER_FOUND(1312, "알 수 없는 에러가 발생했으며, 에러를 처리할 handler를 찾지 못했습니다.", NoHandlerFoundException.class),
    ASYNC_REQUEST_TIMEOUT(1313, "요청에 대한 응답 시간이 초과되었습니다.", AsyncRequestTimeoutException.class),

    /**
     * Scraping Server 관련 예외
     */
    SCRAPING_SERVER_UNAVAILABLE(1350, "장소 정보를 받아오던 중 서버에서 에러가 발생했습니다.", ScrapingServerInternalError.class),

    /**
     * 로그인, 인증 관련 예외
     */
    ACCESS_DENIED(1500, "접근이 거부되었습니다.", null),
    UNAUTHORIZED(1501, "유효하지 않은 인증 정보로 인해 인증 과정에서 문제가 발생하였습니다.", null),
    JWT_UNSUPPORTED(1502, "처리할 수 없는 token입니다.", UnsupportedJwtException.class),
    JWT_MALFORMED(1503, "유효하지 않은 token입니다.", MalformedJwtException.class),
    JWT_INVALID_SIGNATURE(1504, "Token의 서명이 잘못되었습니다.", SignatureException.class),
    JWT_EXPIRED(1505, "Token이 만료되었습니다. Token을 갱신하거나 다시 로그인 해주세요.", ExpiredJwtException.class),
    TOKEN_VALIDATE(1506, "Token의 유효성을 검증하는 과정에서 문제가 발생했습니다. 관리자에게 문의해주세요.", TokenValidateException.class),
    REDIS_REFRESH_TOKEN_NOT_FOUND(1507, "로그인 이력을 찾을 수 없습니다. 다시 로그인 해주세요.", RedisRefreshTokenNotFoundException.class),

    /**
     * 회원({@link Member}) 관련 예외
     */
    MEMBER_ID_NOT_FOUND(2000, "회원을 찾을 수 없습니다.", MemberIdNotFoundException.class),

    /**
     * 장소({@link Place}) 관련 예외
     */
    OPENING_HOURS_UNEXPECTED_FORMAT(3000, "가게 영업시간이 처리할 수 없는 형태입니다. 서버 관리자에게 문의해주세요.게", OpeningHoursUnexpectedFormatException.class),
    ;

    private final Integer code;
    private final String message;
    private final Class<? extends Exception> type;

    public static ExceptionType from(Class<? extends Exception> classType) {
        Optional<ExceptionType> exceptionType = Arrays.stream(values())
                .filter(ex -> ex.getType() != null && ex.getType().equals(classType))
                .findFirst();

        if (exceptionType.isEmpty()) {
            log.error("[{}] 정의되지 않은 exception이 발생하였습니다. Type of exception={}", LogUtils.getLogTraceId(), classType);
        }

        return exceptionType.orElse(UNHANDLED);
    }
}
