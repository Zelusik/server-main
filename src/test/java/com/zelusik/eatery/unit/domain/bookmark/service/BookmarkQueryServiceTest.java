package com.zelusik.eatery.unit.domain.bookmark.service;

import com.zelusik.eatery.domain.bookmark.repository.BookmarkRepository;
import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.member.service.MemberService;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Query) - Bookmark")
@ExtendWith(MockitoExtension.class)
class BookmarkQueryServiceTest {

    @InjectMocks
    private BookmarkQueryService sut;

    @Mock
    private MemberService memberService;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;

    @DisplayName("회원과 장소의 PK가 주어지고, 일치하는 북마크 존재 여부를 확인한다.")
    @Test
    void givenMemberIdAndPlaceId_whenCheckMarkingStatus_thenReturnMarkingStatus() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        Place place = createPlace(placeId, "12345");
        boolean expectedResult = true;
        given(bookmarkRepository.existsByMember_IdAndPlace(memberId, place)).willReturn(expectedResult);

        // when
        boolean actualResult = sut.isMarkedPlace(memberId, place);

        // then
        then(bookmarkRepository).should().existsByMember_IdAndPlace(memberId, place);
        then(memberService).shouldHaveNoInteractions();
        then(placeRepository).shouldHaveNoInteractions();
        then(bookmarkRepository).shouldHaveNoMoreInteractions();
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(memberService).shouldHaveNoMoreInteractions();
        then(placeRepository).shouldHaveNoMoreInteractions();
        then(bookmarkRepository).shouldHaveNoMoreInteractions();
    }

    private Place createPlace(long id, String kakaoPid) {
        return Place.of(
                id,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "place name",
                "page url",
                KakaoCategoryGroupCode.FD6,
                new PlaceCategory("한식", "냉면", null),
                null,
                new Address("sido", "sgg", "lot number address", "road address"),
                null,
                new Point("37.5595073462493", "126.921462488105"),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}