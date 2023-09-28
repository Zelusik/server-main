package com.zelusik.eatery.unit.domain.bookmark.api;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.bookmark.api.BookmarkController;
import com.zelusik.eatery.domain.bookmark.dto.BookmarkDto;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.global.security.UserPrincipal;
import com.zelusik.eatery.domain.bookmark.service.BookmarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Bookmark Controller Test")
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = BookmarkController.class)
class BookmarkControllerTest {

    @MockBean
    private BookmarkService bookmarkService;

    private final MockMvc mvc;

    @Autowired
    public BookmarkControllerTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("북마크에 저장하고자 하는 장소의 id가 주어지고, 장소를 북마크에 저장하면, 저장된 북마크 정보가 반환된다.")
    @Test
    void givenPlaceId_whenMarkPlace_thenReturnSavedBookmark() throws Exception {
        // given
        long loginMemberId = 1L;
        long placeId = 2L;
        long bookmarkId = 3L;
        BookmarkDto expectedResult = createBookmarkDto(bookmarkId, loginMemberId, placeId);
        given(bookmarkService.mark(loginMemberId, placeId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        post("/api/v1/bookmarks")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("placeId", String.valueOf(placeId))
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookmarkId))
                .andExpect(jsonPath("$.memberId").value(loginMemberId))
                .andExpect(jsonPath("$.placeId").value(placeId))
                .andDo(print());
    }

    @DisplayName("장소의 id가 주어지고, 해당 장소에 대한 북마크를 삭제한다.")
    @Test
    void givenPlaceId_whenDeletePlaceBookmark_thenDeleteBookmark() throws Exception {
        // given
        long loginMemberId = 1L;
        long placeId = 2L;
        willDoNothing().given(bookmarkService).delete(loginMemberId, placeId);

        // when & then
        mvc.perform(
                        delete("/api/v1/bookmarks")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("placeId", String.valueOf(placeId))
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    private UserDetails createTestUserDetails(long memberId) {
        return UserPrincipal.of(createMemberDto(memberId, Set.of(RoleType.USER)));
    }

    private MemberDto createMemberDto(Long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                EateryConstants.defaultProfileImageUrl,
                EateryConstants.defaultProfileThumbnailImageUrl,
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

    private BookmarkDto createBookmarkDto(long bookmarkId, long memberId, long placeId) {
        return new BookmarkDto(bookmarkId, memberId, placeId);
    }
}