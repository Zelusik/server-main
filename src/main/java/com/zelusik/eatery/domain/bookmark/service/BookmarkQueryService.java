package com.zelusik.eatery.domain.bookmark.service;

import com.zelusik.eatery.domain.bookmark.repository.BookmarkRepository;
import com.zelusik.eatery.domain.place.entity.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkQueryService {

    private final BookmarkRepository bookmarkRepository;

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
}
