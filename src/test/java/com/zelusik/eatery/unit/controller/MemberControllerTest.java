package com.zelusik.eatery.unit.controller;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.constant.ConstantUtil;
import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.constant.review.MemberDeletionSurveyType;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.controller.MemberController;
import com.zelusik.eatery.dto.member.MemberDeletionSurveyDto;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.member.MemberProfileInfoDto;
import com.zelusik.eatery.dto.member.request.FavoriteFoodCategoriesUpdateRequest;
import com.zelusik.eatery.dto.member.request.MemberUpdateRequest;
import com.zelusik.eatery.dto.review.request.MemberDeletionSurveyRequest;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.constant.ConstantUtil.API_MINOR_VERSION_HEADER_NAME;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Member Controller")
@MockBean(JpaMetamodelMappingContext.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    public MemberControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
        this.mapper = new ObjectMapper();
    }

    @DisplayName("내 정보를 조회하면, 조회된 내 회원 정보가 응답된다.")
    @Test
    void given_whenGettingMyInfo_thenReturnMyInfo() throws Exception {
        // given
        long memberId = 1L;
        MemberDto expectedResult = createMemberDto(memberId);
        given(memberService.findDtoById(memberId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/members/me")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.profileImage.imageUrl").value(expectedResult.getProfileImageUrl()))
                .andExpect(jsonPath("$.profileImage.thumbnailImageUrl").value(expectedResult.getProfileThumbnailImageUrl()))
                .andExpect(jsonPath("$.nickname").value(expectedResult.getNickname()))
                .andExpect(jsonPath("$.gender").value(expectedResult.getGender().getDescription()))
                .andExpect(jsonPath("$.birthDay").value(expectedResult.getBirthDay().toString()));
    }

    @DisplayName("내 프로필 정보를 조회하면, 조회된 내 프로필 정보가 응답된다.")
    @Test
    void given_whenGettingMyProfileInfo_thenReturnMemberProfileInfo() throws Exception {
        // given
        long memberId = 1L;
        int numOfReviews = 62;
        String mostVisitedLocation = "연남동";
        ReviewKeywordValue mostTaggedReviewKeyword = ReviewKeywordValue.FRESH;
        FoodCategoryValue mostEatenFoodCategory = FoodCategoryValue.KOREAN;
        MemberProfileInfoDto expectedResult = createMemberProfileInfoDto(memberId, numOfReviews, mostVisitedLocation, mostTaggedReviewKeyword, mostEatenFoodCategory);
        given(memberService.getMemberProfileInfoById(memberId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/members/me/profile")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.profileImage.imageUrl").value(expectedResult.getProfileImageUrl()))
                .andExpect(jsonPath("$.profileImage.thumbnailImageUrl").value(expectedResult.getProfileThumbnailImageUrl()))
                .andExpect(jsonPath("$.nickname").value(expectedResult.getNickname()))
                .andExpect(jsonPath("$.gender").value(expectedResult.getGender().getDescription()))
                .andExpect(jsonPath("$.birthDay").value(expectedResult.getBirthDay().toString()))
                .andExpect(jsonPath("$.numOfReviews").value(numOfReviews))
                .andExpect(jsonPath("$.influence").value(0))
                .andExpect(jsonPath("$.numOfFollowers").value(0))
                .andExpect(jsonPath("$.numOfFollowings").value(0))
                .andExpect(jsonPath("$.tasteStatistics.mostVisitedLocation").value(mostVisitedLocation))
                .andExpect(jsonPath("$.tasteStatistics.mostTaggedReviewKeyword").value(mostTaggedReviewKeyword.getDescription()))
                .andExpect(jsonPath("$.tasteStatistics.mostEatenFoodCategory").value(mostEatenFoodCategory.getCategoryName()));
    }

    @DisplayName("id로 회원 프로필 정보를 조회하면, 조회된 회원 프로필 정보가 응답된다.")
    @Test
    void given_whenGettingMemberProfileInfoWithMemberId_thenReturnMemberProfileInfo() throws Exception {
        // given
        long loginMemberId = 1L;
        long memberId = 2L;
        int numOfReviews = 62;
        String mostVisitedLocation = "연남동";
        ReviewKeywordValue mostTaggedReviewKeyword = ReviewKeywordValue.FRESH;
        FoodCategoryValue mostEatenFoodCategory = FoodCategoryValue.KOREAN;
        MemberProfileInfoDto expectedResult = createMemberProfileInfoDto(memberId, numOfReviews, mostVisitedLocation, mostTaggedReviewKeyword, mostEatenFoodCategory);
        given(memberService.getMemberProfileInfoById(memberId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/members/" + memberId + "/profile")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.isEqualLoginMember").value(loginMemberId == memberId))
                .andExpect(jsonPath("$.profileImage.imageUrl").value(expectedResult.getProfileImageUrl()))
                .andExpect(jsonPath("$.profileImage.thumbnailImageUrl").value(expectedResult.getProfileThumbnailImageUrl()))
                .andExpect(jsonPath("$.nickname").value(expectedResult.getNickname()))
                .andExpect(jsonPath("$.numOfReviews").value(numOfReviews))
                .andExpect(jsonPath("$.influence").value(0))
                .andExpect(jsonPath("$.numOfFollowers").value(0))
                .andExpect(jsonPath("$.numOfFollowings").value(0))
                .andExpect(jsonPath("$.tasteStatistics.mostVisitedLocation").value(mostVisitedLocation))
                .andExpect(jsonPath("$.tasteStatistics.mostTaggedReviewKeyword").value(mostTaggedReviewKeyword.getContent()))
                .andExpect(jsonPath("$.tasteStatistics.mostEatenFoodCategory").value(mostEatenFoodCategory.getCategoryName()));
    }

    @DisplayName("주어진 검색 키워드로 회원을 검색하면, 검색된 회원 목록이 반환된다.")
    @Test
    void givenSearchKeyword_whenSearchMembersByKeyword_thenReturnSearchedMembers() throws Exception {
        // given
        String searchKeyword = "test";
        List<MemberDto> expectedResult = List.of(createMemberDto(2L));
        given(memberService.searchDtosByKeyword(eq(searchKeyword), any(Pageable.class))).willReturn(new SliceImpl<>(expectedResult));

        // when & then
        mvc.perform(
                        get("/api/v1/members/search")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("keyword", searchKeyword)
                                .with(user(createTestUserDetails(1L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numOfElements").value(expectedResult.size()))
                .andExpect(jsonPath("$.contents", hasSize(expectedResult.size())));
    }

    @DisplayName("프로필 이미지를 제외한 수정할 회원 정보가 주어지고, 내 정보를 수정한다.")
    @Test
    void givenMemberUpdateInfoWithoutProfileImage_whenUpdatingMyInfo_thenUpdate() throws Exception {
        // given
        long memberId = 1L;
        MemberUpdateRequest memberUpdateInfo = new MemberUpdateRequest("update", LocalDate.of(2020, 1, 1), Gender.ETC, null);
        given(memberService.update(eq(memberId), any(MemberUpdateRequest.class)))
                .willReturn(createMemberDto(memberId));

        // when & then
        mvc.perform(
                        multipart(HttpMethod.PUT, "/api/v1/members")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .param("nickname", memberUpdateInfo.getNickname())
                                .param("birthDay", memberUpdateInfo.getBirthDay().toString())
                                .param("gender", memberUpdateInfo.getGender().toString())
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId));
    }

    @DisplayName("선호 음식 카테고리 목록이 주어지고, 이를 업데이트하면, 수정된 멤버 정보가 반환된다.")
    @Test
    void givenFavoriteFoodCategories_whenUpdatingFavoriteFoodCategories_thenReturnUpdatedMember() throws Exception {
        // given
        long memberId = 1L;
        FavoriteFoodCategoriesUpdateRequest request = new FavoriteFoodCategoriesUpdateRequest(List.of(FoodCategoryValue.KOREAN, FoodCategoryValue.WESTERN));
        given(memberService.updateFavoriteFoodCategories(any(), any())).willReturn(createMemberDto(memberId));

        // when & then
        mvc.perform(
                        put("/api/v1/members/favorite-food")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nickname").exists())
                .andExpect(jsonPath("$.favoriteFoodCategories").exists());
    }

    @DisplayName("회원 탈퇴를 하면, 회원과 약관 동의 정보를 삭제하고 탈퇴 설문 정보를 반환한다.")
    @Test
    void given_whenDeleteMember_thenDeleteMemberAndReturnSurvey() throws Exception {
        // given
        long memberId = 1L;
        MemberDeletionSurveyType surveyType = MemberDeletionSurveyType.NOT_TRUST;
        given(memberService.delete(memberId, surveyType))
                .willReturn(createMemberDeletionSurveyDto(memberId, surveyType));

        // when & then
        mvc.perform(
                        delete("/api/v1/members")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(MemberDeletionSurveyRequest.of(surveyType)))
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.survey").value(surveyType.getDescription()));
    }

    private UserDetails createTestUserDetails(long memberId) {
        return UserPrincipal.of(createMemberDto(memberId));
    }

    private MemberDto createMemberDto(Long memberId) {
        return createMemberDto(memberId, Set.of(RoleType.USER));
    }

    private MemberDto createMemberDto(Long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
                "1234567890",
                LoginType.KAKAO,
                roleTypes,
                "test@test.com",
                "test",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                List.of(FoodCategoryValue.KOREAN),
                null
        );
    }

    private MemberDeletionSurveyDto createMemberDeletionSurveyDto(Long memberId, MemberDeletionSurveyType surveyType) {
        return MemberDeletionSurveyDto.of(
                10L,
                memberId,
                surveyType
        );
    }

    @NonNull
    private MemberProfileInfoDto createMemberProfileInfoDto(long memberId, int numOfReviews, String mostVisitedLocation, ReviewKeywordValue mostTaggedReviewKeyword, FoodCategoryValue mostEatenFoodCategory) {
        return new MemberProfileInfoDto(
                memberId,
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
                "test",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                numOfReviews,
                0,
                0,
                0,
                mostVisitedLocation,
                mostTaggedReviewKeyword,
                mostEatenFoodCategory
        );
    }
}