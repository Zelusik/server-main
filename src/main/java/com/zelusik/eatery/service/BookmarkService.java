package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.Bookmark;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.bookmark.BookmarkDto;
import com.zelusik.eatery.exception.bookmark.AlreadyMarkedPlaceException;
import com.zelusik.eatery.exception.bookmark.BookmarkNotFoundException;
import com.zelusik.eatery.exception.member.MemberIdNotFoundException;
import com.zelusik.eatery.exception.place.PlaceNotFoundException;
import com.zelusik.eatery.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.repository.member.MemberRepository;
import com.zelusik.eatery.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkService {

    // Service class를 참조하게 될 경우 순환 참조 문제가 발생할 수 있으므로 주의해야 한다.
    private final MemberService memberService;
    private final PlaceRepository placeRepository;
    private final BookmarkRepository bookmarkRepository;

    /**
     * 특정 장소를 북마크에 저장한다.
     *
     * @param memberId 북마크를 하고자 하는 회원(로그인 회원)의 PK
     * @param placeId  북마크를 하고자 하는 장소의 PK
     * @return 생성된 북마크 정보가 담긴 dto
     * @throws AlreadyMarkedPlaceException 이미 저장한 장소를 북마크에 저장하고자 하는 경우
     */
    @Transactional
    public BookmarkDto mark(long memberId, long placeId) {
        validateNotAlreadyMarked(memberId, placeId);

        Member member = memberService.findById(memberId);
        Place place = placeRepository.findById(placeId).orElseThrow(PlaceNotFoundException::new);

        Bookmark bookmark = bookmarkRepository.save(Bookmark.of(member, place));
        return BookmarkDto.from(bookmark);
    }

    /**
     * 회원이 place를 북마크에 저장했는지 여부를 반환한다.
     *
     * @param memberId 북마크 저장 여부를 확인하고자 하는 회원의 PK
     * @param place    북마크 저장 여부를 확인하고자 하는 장소
     * @return 북마크 저장 여부
     */
    public boolean isMarkedPlace(long memberId, @NonNull Place place) {
        return bookmarkRepository.existsByMember_IdAndPlace(memberId, place);
    }

    /**
     * 장소 북마크 취소
     *
     * @param memberId 북마크를 취소하고자 하는 회원(로그인 회원)의 PK
     * @param placeId  북마크를 취소하고자 하는 장소의 PK
     */
    @Transactional
    public void delete(long memberId, long placeId) {
        Bookmark bookmark = bookmarkRepository.findByMember_IdAndPlace_Id(memberId, placeId)
                .orElseThrow(BookmarkNotFoundException::new);
        bookmarkRepository.delete(bookmark);
    }

    /**
     * 이미 북마크에 저장한 장소인지 검증한다.
     *
     * @param memberId 북마크에 저장하고자 하는 회원(로그인 회원)의 PK
     * @param placeId  북마크에 저장하고자 하는 장소의 PK
     */
    private void validateNotAlreadyMarked(Long memberId, Long placeId) {
        if (bookmarkRepository.existsByMember_IdAndPlace_Id(memberId, placeId)) {
            throw new AlreadyMarkedPlaceException();
        }
    }
}
