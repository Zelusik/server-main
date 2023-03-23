package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.Bookmark;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.bookmark.BookmarkDto;
import com.zelusik.eatery.app.repository.BookmarkRepository;
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
     */
    @Transactional
    public BookmarkDto mark(Long memberId, Long placeId) {
        Member member = memberService.findEntityById(memberId);
        Place place = placeService.findEntityById(placeId);

        Bookmark bookmark = Bookmark.of(member, place);
        bookmarkRepository.save(bookmark);
        return BookmarkDto.from(bookmark);
    }
}