package com.zelusik.eatery.repository.bookmark;

import java.util.List;

public interface BookmarkJdbcTemplateRepository {

    List<Long> findAllMarkedPlaceId(Long memberId);
}
