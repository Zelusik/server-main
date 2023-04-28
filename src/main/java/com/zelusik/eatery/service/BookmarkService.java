package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.Bookmark;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.bookmark.BookmarkDto;
import com.zelusik.eatery.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.exception.bookmark.AlreadyMarkedPlaceException;
import com.zelusik.eatery.exception.bookmark.BookmarkNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkService {

    private final MemberService memberService;
    private final PlaceService placeService;
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
    public BookmarkDto mark(Long memberId, Long placeId) {
        validateNotAlreadyMarked(memberId, placeId);

        Member member = memberService.findById(memberId);
        Place place = placeService.findById(placeId);

        Bookmark bookmark = Bookmark.of(member, place);
        bookmarkRepository.save(bookmark);
        return BookmarkDto.from(bookmark);
    }

    /**
     * 장소 북마크 취소
     *
     * @param memberId 북마크를 취소하고자 하는 회원(로그인 회원)의 PK
     * @param placeId  북마크를 취소하고자 하는 장소의 PK
     */
    @Transactional
    public void delete(Long memberId, Long placeId) {
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
