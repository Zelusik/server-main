package com.zelusik.eatery.repository.place;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.zelusik.eatery.domain.QBookmark.bookmark;
import static com.zelusik.eatery.domain.place.QPlace.place;

@RequiredArgsConstructor
public class PlaceRepositoryQCustomImpl implements PlaceRepositoryQCustom {

    public static final int MAX_NUM_OF_FILTERING_KEYWORDS = 8;

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Place> searchByKeyword(String keyword, Pageable pageable) {
        List<Place> content = queryFactory
                .selectFrom(place)
                .where(place.name.containsIgnoreCase(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)  // 다음 페이지 존재 여부 확인을 위함
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<PlaceFilteringKeywordDto> getFilteringKeywords(long loginMemberId) {
        Long numOfMarkedPlaces = queryFactory
                .select(bookmark.count())
                .from(bookmark)
                .where(bookmark.member.id.eq(loginMemberId))
                .fetchOne();
        int minCount = numOfMarkedPlaces == null || numOfMarkedPlaces < 20 ? 3 : 5;

        List<PlaceFilteringKeywordDto> result = new ArrayList<>();
        result.addAll(getFilteringKeywordsForFirstCategory(loginMemberId, minCount));
        result.addAll(getFilteringKeywordsForSecondCategory(loginMemberId, minCount));
        result.addAll(getFilteringKeywordsForAddress(loginMemberId, minCount));
        result.addAll(getFilteringKeywordsForTop3Keywords(loginMemberId, minCount));
        result.sort(Comparator.comparing(PlaceFilteringKeywordDto::getCount).reversed());   // 개수 많은 순 정렬

        return result.size() < MAX_NUM_OF_FILTERING_KEYWORDS ? result : result.subList(0, MAX_NUM_OF_FILTERING_KEYWORDS);
    }

    /**
     * 저장된 장소들의 first category에 대한 filtering keywords를 조회한다.
     *
     * @param loginMemberId PK of login member
     * @param minCount      filtering keyword가 되기 위한 최소 중복 개수. 3 또는 5
     * @return 조회된 filtering keywords
     */
    @NonNull
    private List<PlaceFilteringKeywordDto> getFilteringKeywordsForFirstCategory(long loginMemberId, int minCount) {
        List<Tuple> tuples = queryFactory
                .select(place.category.firstCategory,
                        place.category.firstCategory.count().intValue())
                .from(bookmark)
                .join(bookmark.place, place)
                .where(bookmark.member.id.eq(loginMemberId)
                        .and(place.category.firstCategory.isNotEmpty()))
                .groupBy(place.category.firstCategory)
                .having(place.category.firstCategory.count().goe(minCount))
                .fetch();

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        return tuples.stream()
                .map(tuple -> new PlaceFilteringKeywordDto(
                        Objects.requireNonNull(tuple.get(place.category.firstCategory)),
                        Objects.requireNonNull(tuple.get(place.category.firstCategory.count().intValue())),
                        FilteringType.FIRST_CATEGORY
                )).toList();
    }

    /**
     * 저장된 장소들의 second category에 대한 filtering keywords를 조회한다.
     *
     * @param loginMemberId PK of login member
     * @param minCount      filtering keyword가 되기 위한 최소 중복 개수. 3 또는 5
     * @return 조회된 filtering keywords
     */
    @NonNull
    private List<PlaceFilteringKeywordDto> getFilteringKeywordsForSecondCategory(long loginMemberId, int minCount) {
        List<Tuple> tuples = queryFactory
                .select(place.category.secondCategory,
                        place.category.secondCategory.count().intValue())
                .from(bookmark)
                .join(bookmark.place, place)
                .where(bookmark.member.id.eq(loginMemberId)
                        .and(place.category.secondCategory.isNotEmpty()))
                .groupBy(place.category.secondCategory)
                .having(place.category.secondCategory.count().goe(minCount))
                .fetch();

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        return tuples.stream()
                .map(tuple -> new PlaceFilteringKeywordDto(
                        Objects.requireNonNull(tuple.get(place.category.secondCategory)),
                        Objects.requireNonNull(tuple.get(place.category.secondCategory.count().intValue())),
                        FilteringType.SECOND_CATEGORY
                )).toList();
    }

    /**
     * 저장된 장소들의 주소(읍면동 단위) 대한 filtering keywords를 조회한다.
     *
     * @param loginMemberId PK of login member
     * @param minCount      filtering keyword가 되기 위한 최소 중복 개수. 3 또는 5
     * @return 조회된 filtering keywords
     */
    @NonNull
    private List<PlaceFilteringKeywordDto> getFilteringKeywordsForAddress(long loginMemberId, int minCount) {
        List<String> lotNumberAddresses = queryFactory
                .select(place.address.lotNumberAddress)
                .from(bookmark)
                .join(bookmark.place, place)
                .where(bookmark.member.id.eq(loginMemberId)
                        .and(place.address.lotNumberAddress.isNotNull()))
                .fetch();

        if (lotNumberAddresses == null || lotNumberAddresses.isEmpty()) {
            return List.of();
        }

        List<String> emdAddresses = lotNumberAddresses.stream()
                .map(addr -> {
                    if (!StringUtils.hasText(addr)) {
                        return null;
                    }
                    return addr.substring(0, addr.indexOf(" "));
                }).toList();

        Map<String, Integer> countMap = new HashMap<>();
        emdAddresses.forEach(emdAddr -> countMap.put(emdAddr, countMap.getOrDefault(emdAddr, 0) + 1));

        List<PlaceFilteringKeywordDto> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            String keyword = entry.getKey();
            int count = entry.getValue();
            if (count < minCount) {
                continue;
            }
            result.add(new PlaceFilteringKeywordDto(keyword, count, FilteringType.ADDRESS));
        }
        return result;
    }

    /**
     * 저장된 장소들의 top 3 keywords 대한 filtering keywords를 조회한다.
     *
     * @param loginMemberId PK of login member
     * @param minCount      filtering keyword가 되기 위한 최소 중복 개수. 3 또는 5
     * @return 조회된 filtering keywords
     */
    @NonNull
    private List<PlaceFilteringKeywordDto> getFilteringKeywordsForTop3Keywords(long loginMemberId, int minCount) {
        List<List<ReviewKeywordValue>> top3KeywordsList = queryFactory
                .select(place.top3Keywords)
                .from(bookmark)
                .join(bookmark.place, place)
                .where(bookmark.member.id.eq(loginMemberId))
                .fetch();

        Map<ReviewKeywordValue, Integer> countMap = new HashMap<>();
        top3KeywordsList.forEach(top3Keywords -> {
            if (top3Keywords.isEmpty()) {
                return;
            }
            top3Keywords.forEach(keyword -> countMap.put(keyword, countMap.getOrDefault(keyword, 0) + 1));
        });

        List<PlaceFilteringKeywordDto> result = new ArrayList<>();
        for (Map.Entry<ReviewKeywordValue, Integer> entry : countMap.entrySet()) {
            ReviewKeywordValue keyword = entry.getKey();
            double count = (double) entry.getValue() / 2;   // top 3 keyword의 경우 다른 항목들에 비해 자주 겹치는 데이터이므로 실제 개수의 절반을 개수로 한다. (노션 기획 문서 참고)
            if (count < minCount) {
                continue;
            }
            result.add(new PlaceFilteringKeywordDto(keyword.getContent(), (int) count, FilteringType.TOP_3_KEYWORDS));
        }
        return result;
    }
}
