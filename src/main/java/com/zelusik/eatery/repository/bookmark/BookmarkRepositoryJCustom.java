package com.zelusik.eatery.repository.bookmark;

import java.util.List;

public interface BookmarkRepositoryJCustom {

    List<Long> findAllMarkedPlaceId(Long memberId);
}
