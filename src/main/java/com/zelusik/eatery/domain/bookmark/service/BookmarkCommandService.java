package com.zelusik.eatery.domain.bookmark.service;

import com.zelusik.eatery.domain.bookmark.dto.BookmarkDto;
import com.zelusik.eatery.domain.bookmark.entity.Bookmark;
import com.zelusik.eatery.domain.bookmark.exception.AlreadyMarkedPlaceException;
import com.zelusik.eatery.domain.bookmark.exception.BookmarkNotFoundException;
import com.zelusik.eatery.domain.bookmark.repository.BookmarkRepository;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.exception.PlaceNotFoundException;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkCommandService {

    private final MemberQueryService memberQueryService;
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

        Member member = memberQueryService.getById(memberId);
        Place place = placeRepository.findById(placeId).orElseThrow(PlaceNotFoundException::new);

        Bookmark bookmark = bookmarkRepository.save(Bookmark.of(member, place));
        return BookmarkDto.from(bookmark);
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
