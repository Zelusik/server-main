package com.zelusik.eatery.app.repository.bookmark;

import java.util.List;

public interface BookmarkJdbcTemplateRepository {

    List<Long> findAllMarkedPlaceId(Long memberId);
}
