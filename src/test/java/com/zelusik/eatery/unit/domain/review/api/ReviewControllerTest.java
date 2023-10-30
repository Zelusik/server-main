package com.zelusik.eatery.unit.domain.review.api;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.review.api.ReviewController;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
import com.zelusik.eatery.domain.review.dto.request.ReviewCreateRequest;
import com.zelusik.eatery.domain.review.service.ReviewCommandService;
import com.zelusik.eatery.domain.review.service.ReviewQueryService;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.domain.review_image.dto.request.ReviewImageCreateRequest;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.MenuTagPointCreateRequest;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.ReviewMenuTagCreateRequest;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.global.open_ai.service.OpenAIService;
import com.zelusik.eatery.global.auth.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.WRITER;
import static com.zelusik.eatery.domain.review.constant.ReviewKeywordValue.*;
import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Controller - Review")
@MockBean(JpaMetamodelMappingContext.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {

    @MockBean
    private ReviewCommandService reviewCommandService;
    @MockBean
    private ReviewQueryService reviewQueryService;
    @MockBean
    private OpenAIService openAIService;

    private final MockMvc mvc;

    public ReviewControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("생성할 리뷰 정보가 주어지고, 리뷰를 생성하면, 생성 후 저장된 리뷰가 반환된다.")
    @Test
    void givenReviewInfo_whenReviewCreate_thenReturnSavedReview() throws Exception {
        // given
        long loginMemberId = 1L;
        long reviewId = 2L;
        long writerId = 3L;
        long placeId = 4L;
        ReviewWithPlaceMarkedStatusDto savedReviewWithPlaceMarkedStatusDto = createReviewDto(reviewId, createMemberDto(writerId), createPlaceWithMarkedStatusDto(placeId));
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(placeId);
        given(reviewCommandService.create(eq(loginMemberId), any(ReviewCreateRequest.class))).willReturn(savedReviewWithPlaceMarkedStatusDto);

        // when & then
        mvc.perform(
                        multipart("/api/v1/reviews")
                                .file(createMockMultipartFile())
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .param("placeId", reviewCreateRequest.getPlaceId().toString())
                                .param("keywords", reviewCreateRequest.getKeywords().stream().map(ReviewKeywordValue::name).toArray(String[]::new))
                                .param("autoCreatedContent", reviewCreateRequest.getAutoCreatedContent())
                                .param("content", reviewCreateRequest.getContent())
                                .param("images[0].menuTags[0].content", reviewCreateRequest.getImages().get(0).getMenuTags().get(0).getContent())
                                .param("images[0].menuTags[0].point.x", reviewCreateRequest.getImages().get(0).getMenuTags().get(0).getPoint().getX())
                                .param("images[0].menuTags[0].point.y", reviewCreateRequest.getImages().get(0).getMenuTags().get(0).getPoint().getY())
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andDo(print());
        then(reviewCommandService).should().create(eq(loginMemberId), any(ReviewCreateRequest.class));
        then(reviewQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("리뷰의 id(PK)가 주어지고, 리뷰 상세 정보를 단건 조회하면, 조회된 리뷰 정보가 반환된다.")
    @Test
    void givenReviewId_whenFindReviewById_thenReturnReview() throws Exception {
        // given
        long loginMemberId = 1L;
        long reviewId = 2L;
        long writerId = 3L;
        ReviewWithPlaceMarkedStatusDto expectedResult = createReviewDto(reviewId, createMemberDto(writerId), createPlaceWithMarkedStatusDto(4L));
        given(reviewQueryService.findDtoById(loginMemberId, reviewId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/reviews/" + reviewId)
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.writer").exists())
                .andExpect(jsonPath("$.writer.isEqualLoginMember").value(loginMemberId == writerId))
                .andExpect(jsonPath("$.place").exists())
                .andExpect(jsonPath("$.keywords").isArray())
                .andExpect(jsonPath("$.keywords[0]").value(expectedResult.getKeywords().get(0).getContent()))
                .andExpect(jsonPath("$.content").value(expectedResult.getContent()))
                .andExpect(jsonPath("$.reviewImages").exists())
                .andDo(print());
        then(reviewQueryService).should().findDtoById(loginMemberId, reviewId);
        then(reviewQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void givenPlaceId_whenSearchReviewsOfCertainPlace_thenReturnReviews() throws Exception {
        // given
        long loginMemberId = 1L;
        SliceImpl<ReviewWithPlaceMarkedStatusDto> expectedResult = new SliceImpl<>(List.of(createReviewDto(2L, createMemberDto(3L), createPlaceWithMarkedStatusDto(4L))));
        given(reviewQueryService.findDtos(eq(loginMemberId), isNull(), isNull(), eq(List.of(WRITER, PLACE)), any(Pageable.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/reviews")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("embed", WRITER.name(), PLACE.name())
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasContent").value(true))
                .andDo(print());
        then(reviewQueryService).should().findDtos(eq(loginMemberId), isNull(), isNull(), eq(List.of(WRITER, PLACE)), any(Pageable.class));
        then(reviewQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("피드 목록을 조회한다.")
    @Test
    void given_whenSearchFeeds_thenReturnFeedResponses() throws Exception {
        // given
        long loginMemberId = 1L;
        long reviewId = 2L;
        Slice<ReviewWithPlaceMarkedStatusDto> expectedResult = new SliceImpl<>(List.of(createReviewDto(reviewId, createMemberDto(3L), createPlaceWithMarkedStatusDto(4L))));
        given(reviewQueryService.findReviewReed(eq(loginMemberId), any(Pageable.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(get("/api/v1/reviews/feed")
                        .header(API_MINOR_VERSION_HEADER_NAME, 1)
                        .with(user(createTestUserDetails(loginMemberId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents", hasSize(expectedResult.getSize())))
                .andExpect(jsonPath("$.contents[0].id").value(reviewId))
                .andExpect(jsonPath("$.contents[0].reviewImage").exists())
                .andDo(print());
        then(reviewQueryService).should().findReviewReed(eq(loginMemberId), any(Pageable.class));
        then(reviewQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("내가 작성한 리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void whenSearchMyReviews_thenReturnReviews() throws Exception {
        // given
        long loginMemberId = 1L;
        SliceImpl<ReviewWithPlaceMarkedStatusDto> expectedResults = new SliceImpl<>(List.of(createReviewDto(2L, createMemberDto(3L), createPlaceWithMarkedStatusDto(4L))));
        given(reviewQueryService.findDtos(eq(loginMemberId), eq(loginMemberId), isNull(), eq(List.of(PLACE)), any(Pageable.class))).willReturn(expectedResults);

        // when & then
        mvc.perform(
                        get("/api/v1/reviews/me")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasContent").value(true))
                .andDo(print());
        then(reviewQueryService).should().findDtos(eq(loginMemberId), eq(loginMemberId), isNull(), eq(List.of(PLACE)), any(Pageable.class));
        then(reviewQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("리뷰를 작성하고자 하는 장소에 대한 키워드들과 메뉴 목록 및 각 메뉴에 대한 키워드들이 주어지고, 자동 생성된 리뷰 내용을 조회하면, 생성된 리뷰 내용을 반환한다.")
    @Test
    void givenPlaceKeywordsAndMenusAndMenuKeywords_whenGettingAutoCreatedReviewContent_thenReturnRespondedMessageContent() throws Exception {
        // given
        List<ReviewKeywordValue> placeKeywords = List.of(FRESH, GENEROUS_PORTIONS, WITH_ALCOHOL, GOOD_FOR_DATE);
        List<String> menus = List.of("시금치카츠카레", "버터치킨카레");
        List<String> menuKeywords = List.of("싱그러운+육즙 가득힌+매콤한", "부드러운+촉촉한");
        String expectedResult = "생성된 리뷰 내용";
        given(openAIService.getAutoCreatedReviewContent(
                placeKeywords,
                menus,
                menuKeywords.stream()
                        .map(keywords -> Arrays.asList(keywords.split("/+")))
                        .toList()
        )).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/reviews/contents/auto-creations")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .param("placeKeywords", placeKeywords.stream().map(ReviewKeywordValue::name).toList().toArray(new String[0]))
                                .param("menus", menus.toArray(new String[0]))
                                .param("menuKeywords", menuKeywords.toArray(new String[0]))
                                .with(user(createTestUserDetails(1L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(expectedResult))
                .andDo(print());
        then(openAIService).should().getAutoCreatedReviewContent(
                placeKeywords,
                menus,
                menuKeywords.stream()
                        .map(keywords -> Arrays.asList(keywords.split("/+")))
                        .toList()
        );
        then(openAIService).shouldHaveNoMoreInteractions();
        then(reviewQueryService).shouldHaveNoInteractions();
    }

    @DisplayName("리뷰 내용 자동 생성 시, 주어진 메뉴와 메뉴 키워드의 개수가 맞지 않다면, 예외가 발생한다.")
    @Test
    void givenMenusAndKeywordsWithMismatchedCounts_whenGettingAutoCreatedContent_thenThrowMismatchedMenuKeywordCountException() throws Exception {
        // given
        List<String> placeKeywords = List.of("신선한 재료", "넉넉한 양");
        List<String> menus = List.of("시금치카츠카레", "버터치킨카레");
        List<String> menuKeywords = List.of("싱그러운+육즙 가득힌+매콤한");

        // when & then
        mvc.perform(
                        get("/api/v1/reviews/contents/auto-creations")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .param("placeKeywords", placeKeywords.toArray(new String[0]))
                                .param("menus", menus.toArray(new String[0]))
                                .param("menuKeywords", menuKeywords.toArray(new String[0]))
                                .with(user(createTestUserDetails(1L)))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
        then(reviewQueryService).shouldHaveNoInteractions();
    }

    private UserDetails createTestUserDetails(long memberId) {
        return UserPrincipal.of(createMemberDto(memberId));
    }

    private MemberDto createMemberDto(Long memberId) {
        return createMemberDto(memberId, Set.of(RoleType.USER));
    }

    private MemberDto createMemberDto(long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                EateryConstants.defaultProfileImageUrl,
                EateryConstants.defaultProfileThumbnailImageUrl,
                "1234567890",
                LoginType.KAKAO,
                roleTypes,
                "test@test.com",
                "test",
                LocalDate.of(1998, 1, 5),
                20,
                Gender.MALE,
                List.of(FoodCategoryValue.KOREAN),
                null
        );
    }

    private PlaceDto createPlaceDto(long placeId) {
        return new PlaceDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                "308342289",
                "연남토마 본점",
                "https://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "https://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of()
        );
    }

    private PlaceWithMarkedStatusDto createPlaceWithMarkedStatusDto(long placeId) {
        return new PlaceWithMarkedStatusDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                "308342289",
                "연남토마 본점",
                "https://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "https://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(),
                false
        );
    }

    public static ReviewWithPlaceMarkedStatusDto createReviewDto(long reviewId, MemberDto writer, PlaceWithMarkedStatusDto place) {
        return new ReviewWithPlaceMarkedStatusDto(
                reviewId,
                writer,
                place,
                List.of(ReviewKeywordValue.NOISY, ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출된 내용",
                List.of(new ReviewImageDto(
                        1L,
                        1L,
                        "test.txt",
                        "storedName",
                        "url",
                        "thumbnailStoredName",
                        "thumbnailUrl")),
                LocalDateTime.now()
        );
    }

    private ReviewCreateRequest createReviewCreateRequest(long placeId) {
        return new ReviewCreateRequest(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출한 내용",
                List.of(createReviewImageCreateRequest())
        );
    }

    private ReviewImageCreateRequest createReviewImageCreateRequest() {
        return new ReviewImageCreateRequest(
                createMockMultipartFile(),
                List.of(
                        createReviewMenuTagCreateRequest("치킨"),
                        createReviewMenuTagCreateRequest("피자")
                )
        );
    }

    private ReviewMenuTagCreateRequest createReviewMenuTagCreateRequest(String content) {
        return new ReviewMenuTagCreateRequest(content, new MenuTagPointCreateRequest("10.0", "50.0"));
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "test",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
    }
}