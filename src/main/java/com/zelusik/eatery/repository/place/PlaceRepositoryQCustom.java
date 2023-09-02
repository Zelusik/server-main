package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PlaceRepositoryQCustom {

    Slice<Place> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 북마크에 저장한 장소들에 대해 filtering keywords를 조회한다.
     *
     * @param loginMemberId PK of login member
     * @return 조회된 filtering keywords
     */
    List<PlaceFilteringKeywordDto> getFilteringKeywords(long loginMemberId);
}
