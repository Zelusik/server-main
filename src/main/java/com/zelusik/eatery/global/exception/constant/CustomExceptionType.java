package com.zelusik.eatery.global.exception.constant;

import com.zelusik.eatery.domain.bookmark.entity.Bookmark;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.review.entity.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Error code 목록
 *
 * <ul>
 *     <li>1001 ~ 1999: 일반 예외. 아래 항목에 해당하지 않는 대부분의 예외가 여기에 해당한다.</li>
 *     <li>1350 ~ 1399: Scraping 서버(Flask) 관련 예외</li>
 *     <li>14XX: DB 관련 예외</li>
 *     <li>15XX: 인증 관련 예외</li>
 *     <li>2XXX: 회원({@link Member}) 관련 예외</li>
 *     <li>3000 ~ 3499: 장소 관련 예외</li>
 *     <li>3500 ~ 3999: 리뷰 관련 예외</li>
 *     <li>4300 ~ 4599: 북마크 관련 예외</li>
 *     <li>1XXXX: Kakao server 관련 예외</li>
 *     <li>2XXXX: Apple server/login 관련 예외</li>
 *     <li>3XXXX: Open AI 관련 예외</li>
 * </ul>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CustomExceptionType {

    /**
     * Global/Normal Exception
     */
    MULTIPART_FILE_NOT_READABLE(1001, "파일을 읽을 수 없습니다."),
    THUMBNAIL_IMAGE_CREATE(1002, "이미지 압축 과정에서 알 수 없는 에러가 발생했습니다."),
    MAPPER_IO_EXCEPTION(1003, "ObjectMapper가 값을 읽는 과정에서 IOException이 발생했습니다."),

    /**
     * Scraping Server 관련 예외
     */
    SCRAPING_SERVER_UNAVAILABLE(1350, "장소 정보를 받아오던 중 서버에서 에러가 발생했습니다."),

    /**
     * 로그인, 인증 관련 예외
     */
    ACCESS_DENIED(1500, "접근이 거부되었습니다."),
    UNAUTHORIZED(1501, "유효하지 않은 인증 정보로 인해 인증 과정에서 문제가 발생하였습니다."),
    TOKEN_VALIDATE(1502, "유효하지 않은 token입니다. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신이 필요합니다."),
    ACCESS_TOKEN_VALIDATE(1503, "유효하지 않은 access token입니다. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신이 필요합니다."),
    REFRESH_TOKEN_VALIDATE(1504, "유효하지 않은 refresh token입니다. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신이 필요합니다."),

    /**
     * 회원({@link Member}) 관련 예외
     */
    MEMBER_NOT_FOUND(2000, "회원을 찾을 수 없습니다."),

    /**
     * 장소({@link Place}) 관련 예외
     */
    PLACE_ALREADY_EXISTS(3000, "동일한 장소 데이터가 이미 존재합니다."),
    PLACE_NOT_FOUND(3001, "장소를 찾을 수 없습니다."),
    NOT_ACCEPTABLE_PLACE_SEARCH_KEYWORD(3002, "유효하지 않은 검색 키워드입니다."),
    PLACE_MENUS_NOT_FOUND(3004, "일치하는 장소의 메뉴 데이터를 찾을 수 없습니다."),
    PLACE_MENUS_ALREADY_EXISTS(3005, "장소의 메뉴 데이터가 이미 존재합니다. 추가로 데이터를 생성/저장할 수 없습니다."),
    CONTAINS_DUPLICATE_MENUS(3006, "전달받은 메뉴 목록에 중복된 메뉴가 존재합니다."),

    /**
     * 리뷰({@link Review}) 관련 예외
     */
    NOT_ACCEPTABLE_REVIEW_KEYWORD(3500, "유효하지 않은 리뷰 키워드입니다."),
    REVIEW_NOT_FOUND_BY_ID(3501, "리뷰를 찾을 수 없습니다."),
    REVIEW_DELETE_PERMISSION_DENIED(3502, "리뷰를 삭제할 권한이 없습니다."),
    REVIEW_UPDATE_PERMISSION_DENIED(3503, "리뷰를 수정할 권한이 없습니다."),
    MISMATCHED_MENU_KEYWORD_COUNT(3504, "요청 데이터가 잘못되었습니다. 메뉴와 메뉴에 대한 키워드의 개수가 일치하지 않습니다."),
    INVALID_TYPE_OF_REVIEW_KEYWORD_VALUE(3505, "잘못된 리뷰 키워드 값 유형입니다."),

    /**
     * 북마크({@link Bookmark} 관련 예외
     */
    ALREADY_MARKED_PLACE(4300, "이미 저장한 장소입니다."),
    BOOKMARK_NOT_FOUND(4301, "북마크 저장 이력을 찾을 수 없습니다"),

    /**
     * Kakao server 관련 예외
     */
    KAKAO_SERVER(10000, "카카오 서버와의 통신에서 에러가 발생했습니다."),
    KAKAO_TOKEN_VALIDATE(10000, "유효하지 않은 kakao access token입니다. 요청 데이터가 잘못 되었거나 토큰이 만료되지 않았는지 확인해주세요."),

    /**
     * Apple server/login 관련 예외
     */
    APPLE_OAUTH_LOGIN(20000, "애플 로그인 과정에서 알 수 없는 에러가 발생했습니다."),

    /**
     * Open AI 관련 예외
     */
    OPEN_AI_SERVER(30000, "API 통신 중 문제가 발생했습니다."),
    ;

    private final Integer code;
    private final String message;
}
