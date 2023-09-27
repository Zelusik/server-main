package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.domain.Bookmark;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.bookmark.BookmarkDto;
import com.zelusik.eatery.exception.bookmark.AlreadyMarkedPlaceException;
import com.zelusik.eatery.exception.bookmark.BookmarkNotFoundException;
import com.zelusik.eatery.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.repository.place.PlaceRepository;
import com.zelusik.eatery.service.BookmarkService;
import com.zelusik.eatery.service.MemberService;
import com.zelusik.eatery.util.PlaceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Bookmark Service")
@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @InjectMocks
    private BookmarkService sut;

    @Mock
    private MemberService memberService;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;

    @DisplayName("회원과 장소 정보가 주어지고, 이에 대한 북마크를 생성하면, 생성된 북마크를 반환한다.")
    @Test
    void givenMemberAndPlace_whenCreateBookmark_thenReturnBookmarkCreated() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        long bookmarkId = 3L;
        Member member = createMember(memberId);
        Place place = PlaceTestUtils.createPlace(placeId, "12345");
        Bookmark expectedResult = createBookmark(bookmarkId, member, place);
        given(bookmarkRepository.existsByMember_IdAndPlace_Id(memberId, placeId)).willReturn(false);
        given(memberService.findById(memberId)).willReturn(member);
        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
        given(bookmarkRepository.save(any(Bookmark.class))).willReturn(expectedResult);

        // when
        BookmarkDto actualResult = sut.mark(memberId, placeId);

        // then
        then(bookmarkRepository).should().existsByMember_IdAndPlace_Id(memberId, placeId);
        then(memberService).should().findById(memberId);
        then(placeRepository).should().findById(placeId);
        then(bookmarkRepository).should().save(any(Bookmark.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", bookmarkId)
                .hasFieldOrPropertyWithValue("memberId", memberId)
                .hasFieldOrPropertyWithValue("placeId", placeId);
    }

    @DisplayName("이미 북마크에 저장한 장소를 또 저장하려고 하면, 예외가 발생한다.")
    @Test
    void givenMemberAndPlace_whenMarkPlaceAlreadyMarked_thenThrowAlreadyMarkedPlaceException() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        given(bookmarkRepository.existsByMember_IdAndPlace_Id(memberId, placeId)).willReturn(true);

        // when
        Throwable t = catchThrowable(() -> sut.mark(memberId, placeId));

        // then
        then(bookmarkRepository).should().existsByMember_IdAndPlace_Id(memberId, placeId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(AlreadyMarkedPlaceException.class);
    }

    @DisplayName("회원과 장소의 PK가 주어지고, 일치하는 북마크 존재 여부를 확인한다.")
    @Test
    void givenMemberIdAndPlaceId_whenCheckMarkingStatus_thenReturnMarkingStatus() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        Place place = PlaceTestUtils.createPlace(placeId, "12345");
        boolean expectedResult = true;
        given(bookmarkRepository.existsByMember_IdAndPlace(memberId, place)).willReturn(expectedResult);

        // when
        boolean actualResult = sut.isMarkedPlace(memberId, place);

        // then
        then(bookmarkRepository).should().existsByMember_IdAndPlace(memberId, place);
        then(memberService).shouldHaveNoInteractions();
        then(placeRepository).shouldHaveNoInteractions();
        then(bookmarkRepository).shouldHaveNoMoreInteractions();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @DisplayName("회원과 장소의 PK가 주어지면, 일치하는 북마크를 삭제한다.")
    @Test
    void givenMemberIdAndPlaceId_whenDeleteBookmark_thenDeleting() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        long bookmarkId = 3L;
        Member member = createMember(memberId);
        Place place = PlaceTestUtils.createPlace(placeId, "12345");
        Bookmark foundBookmark = createBookmark(bookmarkId, member, place);
        given(bookmarkRepository.findByMember_IdAndPlace_Id(memberId, placeId)).willReturn(Optional.of(foundBookmark));
        willDoNothing().given(bookmarkRepository).delete(foundBookmark);

        // when
        sut.delete(memberId, placeId);

        // then
        then(bookmarkRepository).should().findByMember_IdAndPlace_Id(memberId, placeId);
        then(bookmarkRepository).should().delete(foundBookmark);
        then(memberService).shouldHaveNoInteractions();
        then(placeRepository).shouldHaveNoInteractions();
        then(bookmarkRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("존재하지 않는 북마크를 삭제하려고 하면, 예외가 발생한다.")
    @Test
    void givenMemberIdAndPlaceId_whenDeleteNotExistentBookmark_thenThrowBookmarkNotFoundException() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        given(bookmarkRepository.findByMember_IdAndPlace_Id(memberId, placeId)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.delete(memberId, placeId));

        // then
        then(bookmarkRepository).should().findByMember_IdAndPlace_Id(memberId, placeId);
        then(memberService).shouldHaveNoInteractions();
        then(placeRepository).shouldHaveNoInteractions();
        then(bookmarkRepository).shouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(BookmarkNotFoundException.class);
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(memberService).shouldHaveNoMoreInteractions();
        then(placeRepository).shouldHaveNoMoreInteractions();
        then(bookmarkRepository).shouldHaveNoMoreInteractions();
    }

    private Member createMember(long memberId) {
        return Member.of(
                memberId,
                "profile image url",
                "profile thunmbnail image url",
                "social user id" + memberId,
                LoginType.KAKAO,
                Set.of(RoleType.USER),
                "email",
                "nickname",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Bookmark createBookmark(long bookmarkId, Member member, Place place) {
        return Bookmark.of(
                bookmarkId,
                member,
                place,
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
    }
}