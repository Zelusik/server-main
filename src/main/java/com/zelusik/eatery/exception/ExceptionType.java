package com.zelusik.eatery.exception;

import com.zelusik.eatery.domain.Bookmark;
import com.zelusik.eatery.domain.curation.Curation;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.constant.exception.ValidationErrorCode;
import com.zelusik.eatery.exception.auth.AccessTokenValidateException;
import com.zelusik.eatery.exception.auth.AppleOAuthLoginException;
import com.zelusik.eatery.exception.auth.RefreshTokenValidateException;
import com.zelusik.eatery.exception.auth.TokenValidateException;
import com.zelusik.eatery.exception.bookmark.AlreadyMarkedPlaceException;
import com.zelusik.eatery.exception.bookmark.BookmarkNotFoundException;
import com.zelusik.eatery.exception.curation.CurationNotFoundException;
import com.zelusik.eatery.exception.file.MultipartFileNotReadableException;
import com.zelusik.eatery.exception.kakao.KakaoTokenValidateException;
import com.zelusik.eatery.exception.member.MemberIdNotFoundException;
import com.zelusik.eatery.exception.place.NotAcceptableFoodCategory;
import com.zelusik.eatery.exception.place.NotAcceptablePlaceSearchKeyword;
import com.zelusik.eatery.exception.place.PlaceMenusNotFoundByPlaceIdException;
import com.zelusik.eatery.exception.place.PlaceNotFoundException;
import com.zelusik.eatery.exception.review.NotAcceptableReviewKeyword;
import com.zelusik.eatery.exception.review.ReviewDeletePermissionDeniedException;
import com.zelusik.eatery.exception.review.ReviewNotFoundException;
import com.zelusik.eatery.exception.review.ReviewUpdatePermissionDeniedException;
import com.zelusik.eatery.exception.scraping.OpeningHoursUnexpectedFormatException;
import com.zelusik.eatery.exception.scraping.ScrapingServerInternalError;
import com.zelusik.eatery.exception.member.ProfileImageNotFoundException;
import com.zelusik.eatery.log.LogUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
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
 *     <li>3000 ~ 3499: 장소 관련 예외</li>
 *     <li>3500 ~ 3999: 리뷰 관련 예외</li>
 *     <li>4000 ~ 4299: 큐레이션 관련 예외</li>
 *     <li>4300 ~ 4599: 북마크 관련 예외</li>
 *     <li>1XXXX: Kakao server 관련 예외</li>
 *     <li>2XXXX: Apple server/login 관련 예외</li>
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
    THUMBNAIL_IMAGE_CREATE(1002, "이미지 압축 과정에서 알 수 없는 에러가 발생했습니다.", ThumbnailImageCreateException.class),
    MAPPER_IOEXCEPTION(1003, "ObjectMapper가 값을 읽는 과정에서 IOException이 발생했습니다.", MapperIOException.class),

    /**
     * Validation Exception
     *
     * @see ValidationErrorCode
     */
    METHOD_ARGUMENT_NOT_VALID(1200, "요청 데이터가 잘못되었습니다.", MethodArgumentNotValidException.class),
    CONSTRAINT_VIOLATION(1200, "요청 데이터가 잘못되었습니다.", ConstraintViolationException.class),

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
    BIND(1314, "Request binding에 실패했습니다. 요청 데이터를 확인해주세요.", BindException.class),

    /**
     * Scraping Server 관련 예외
     */
    SCRAPING_SERVER_UNAVAILABLE(1350, "장소 정보를 받아오던 중 서버에서 에러가 발생했습니다.", ScrapingServerInternalError.class),

    /**
     * 로그인, 인증 관련 예외
     */
    ACCESS_DENIED(1500, "접근이 거부되었습니다.", null),
    UNAUTHORIZED(1501, "유효하지 않은 인증 정보로 인해 인증 과정에서 문제가 발생하였습니다.", null),
    TOKEN_VALIDATE(1502, "유효하지 않은 token입니다. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신이 필요합니다.", TokenValidateException.class),
    ACCESS_TOKEN_VALIDATE(1503, "유효하지 않은 access token입니다. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신이 필요합니다.", AccessTokenValidateException.class),
    REFRESH_TOKEN_VALIDATE(1504, "유효하지 않은 refresh token입니다. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신이 필요합니다.", RefreshTokenValidateException.class),

    /**
     * 회원({@link Member}) 관련 예외
     */
    MEMBER_ID_NOT_FOUND(2000, "회원을 찾을 수 없습니다.", MemberIdNotFoundException.class),
    PROFILE_IMAGE_NOT_FOUND(2001, "프로필 이미지를 찾을 수 없습니다.", ProfileImageNotFoundException.class),

    /**
     * 장소({@link Place}) 관련 예외
     */
    OPENING_HOURS_UNEXPECTED_FORMAT(3000, "장소 영업시간이 처리할 수 없는 형태입니다. 서버 관리자에게 문의해주세요.게", OpeningHoursUnexpectedFormatException.class),
    PLACE_NOT_FOUND(3001, "장소를 찾을 수 없습니다.", PlaceNotFoundException.class),
    NOT_ACCEPTABLE_PLACE_SEARCH_KEYWORD(3002, "유효하지 않은 검색 키워드입니다.", NotAcceptablePlaceSearchKeyword.class),
    NOT_ACCEPTABLE_FOOD_CATEGORY(3003, "유효하지 않은 음식 카테고리입니다.", NotAcceptableFoodCategory.class),
    PLACE_MENUS_NOT_FOUND_BY_PLACE_ID(3004, "일치하는 장소의 메뉴 데이터를 찾을 수 없습니다.", PlaceMenusNotFoundByPlaceIdException.class),

    /**
     * 리뷰({@link Review}) 관련 예외
     */
    NOT_ACCEPTABLE_REVIEW_KEYWORD(3500, "유효하지 않은 리뷰 키워드입니다.", NotAcceptableReviewKeyword.class),
    REVIEW_NOT_FOUND(3501, "리뷰를 찾을 수 없습니다.", ReviewNotFoundException.class),
    REVIEW_DELETE_PERMISSION_DENIED(3502, "리뷰를 삭제할 권한이 없습니다.", ReviewDeletePermissionDeniedException.class),
    REVIEW_UPDATE_PERMISSION_DENIED(3503, "리뷰를 수정할 권한이 없습니다.", ReviewUpdatePermissionDeniedException.class),

    /**
     * 큐레이션({@link Curation}) 관련 예외
     */
    CURATION_NOT_FOUND(4000, "큐레이션을 찾을 수 없습니다.", CurationNotFoundException.class),

    /**
     * 북마크({@link Bookmark} 관련 예외
     */
    ALREADY_MARKED_PLACE(4300, "이미 저장한 장소입니다.", AlreadyMarkedPlaceException.class),
    BOOKMARK_NOT_FOUND(4301, "북마크 저장 이력을 찾을 수 없습니다", BookmarkNotFoundException.class),

    /**
     * Kakao server 관련 예외
     */
    KAKAO_SERVER(10000, "카카오 서버와의 통신에서 에러가 발생했습니다.", null),
    KAKAO_TOKEN_VALIDATE(10000, "유효하지 않은 kakao access token입니다. 요청 데이터가 잘못 되었거나 토큰이 만료되지 않았는지 확인해주세요.", KakaoTokenValidateException.class),

    /**
     * Apple server/login 관련 예외
     */
    APPLE_OAUTH_LOGIN(20000, "애플 로그인 과정에서 알 수 없는 에러가 발생했습니다.", AppleOAuthLoginException.class),
    ;

    private final Integer code;
    private final String message;
    private final Class<? extends Exception> type;

    public static Optional<ExceptionType> from(Class<? extends Exception> classType) {
        Optional<ExceptionType> exceptionType = Arrays.stream(values())
                .filter(ex -> ex.getType() != null && ex.getType().isAssignableFrom(classType))
                .findFirst();

        if (exceptionType.isEmpty()) {
            log.error("[{}] 정의되지 않은 exception이 발생하였습니다. Type of exception={}", LogUtils.getLogTraceId(), classType);
        }

        return exceptionType;
    }
}
