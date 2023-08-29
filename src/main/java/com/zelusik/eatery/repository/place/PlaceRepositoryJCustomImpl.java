package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.converter.ReviewKeywordValueConverter;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.zelusik.eatery.constant.ConstantUtil.MAX_NUM_OF_FILTERING_KEYWORDS;

public class PlaceRepositoryJCustomImpl implements PlaceRepositoryJCustom {

    private static final String[] POSTFIXES_OF_REVIEW_IMAGE_COLUMN_FOR_SELECT = {
            "",
            ".review_image_id AS ", "_review_image_id, ",
            ".review_id AS ", "_review_id, ",
            ".original_name AS ", "_original_name, ",
            ".stored_name AS ", "_stored_name, ",
            ".url AS ", "_url, ",
            ".thumbnail_stored_name AS ", "_thumbnail_stored_name, ",
            ".thumbnail_url AS ", "_thumbnail_url, ",
            ".created_at AS ", "_created_at, ",
            ".updated_at AS ", "_updated_at, ",
            ".deleted_at AS ", "_deleted_at, "
    };

    private static final String[] POSTFIXES_OF_REVIEW_IMAGE_COLUMN_FOR_GROUP_BY = {
            "",
            ".review_image_id, ",
            ".review_id, ",
            ".original_name, ",
            ".stored_name, ",
            ".url, ",
            ".thumbnail_stored_name, ",
            ".thumbnail_url, ",
            ".created_at, ",
            ".updated_at, ",
            ".deleted_at, "
    };

    private final NamedParameterJdbcTemplate template;

    public PlaceRepositoryJCustomImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Page<PlaceDto> findDtosNearBy(
            Long memberId,
            @Nullable FoodCategoryValue foodCategory,
            @Nullable List<DayOfWeek> daysOfWeek,
            @Nullable ReviewKeywordValue preferredVibe,
            Point center,
            int distanceLimit,
            int numOfPlaceImages,
            Pageable pageable
    ) {
        // SELECT
        StringBuilder sqlForCountingTotalElements =
                new StringBuilder()
                        .append("SELECT COUNT(*) ")
                        .append("FROM (")
                        .append("SELECT p.place_id, (6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:lng)) + SIN(RADIANS(:lat)) * SIN(RADIANS(lat)))) AS distance ")
                        .append("FROM place p ");
        StringBuilder sql = new StringBuilder("SELECT p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, ");
        for (int i = 1; i <= numOfPlaceImages; i++) {
            sql.append(String.join("ri" + i, POSTFIXES_OF_REVIEW_IMAGE_COLUMN_FOR_SELECT));
        }
        sql.append("(6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:lng)) + SIN(RADIANS(:lat)) * SIN(RADIANS(lat)))) AS distance, ")
                .append("CASE WHEN bm.bookmark_id IS NULL THEN FALSE ELSE TRUE END AS is_marked ");

        // FROM, JOIN
        sql.append("FROM place p ");
        for (int i = 1; i <= numOfPlaceImages; i++) {
            sql.append("LEFT JOIN review_image ri")
                    .append(i)
                    .append(" ON ri")
                    .append(i)
                    .append(".review_image_id = (SELECT ri.review_image_id FROM review_image ri JOIN review r ON r.review_id = ri.review_id AND ri.deleted_at IS NULL WHERE r.place_id = p.place_id ORDER BY r.created_at DESC LIMIT 1 OFFSET ")
                    .append(i - 1)
                    .append(") ");
        }
        sql.append("LEFT JOIN bookmark bm ON p.place_id = bm.place_id AND bm.member_id = :member_id ");

        if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
            StringBuilder joinOpeningHours = new StringBuilder("JOIN opening_hours oh ON p.place_id = oh.place_id AND (");
            for (int i = 0; i < daysOfWeek.size(); i++) {
                if (i != 0) {
                    joinOpeningHours.append("OR ");
                }
                joinOpeningHours.append("oh.day_of_week = '")
                        .append(daysOfWeek.get(i))
                        .append("' ");
            }
            joinOpeningHours.append(") ");
            sql.append(joinOpeningHours);
            sqlForCountingTotalElements.append(joinOpeningHours);
        }

        // WHERE
        boolean flagForWhere = false;
        StringBuilder whereClause = new StringBuilder();
        if (foodCategory != null) {
            whereClause.append("WHERE p.first_category IN (");
            List<String> matchingFirstCategories = foodCategory.getMatchingFirstCategories();
            for (int i = 0; i < matchingFirstCategories.size(); i++) {
                String category = matchingFirstCategories.get(i);
                if (i != 0) {
                    whereClause.append(", ");
                }
                whereClause.append("'").append(category).append("'");
            }
            whereClause.append(") ");
            flagForWhere = true;
        }
        if (preferredVibe != null) {
            if (flagForWhere) {
                whereClause.append("AND ");
            } else {
                whereClause.append("WHERE ");
            }
            whereClause.append("p.top3keywords LIKE '%").append(preferredVibe.name()).append("%' ");
            flagForWhere = true;
        }
        if (flagForWhere) {
            sql.append(whereClause);
            sqlForCountingTotalElements.append(whereClause);
        }

        // GROUP BY
        sql.append("GROUP BY p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, ");
        for (int i = 1; i <= numOfPlaceImages; i++) {
            sql.append(String.join("ri" + i, POSTFIXES_OF_REVIEW_IMAGE_COLUMN_FOR_GROUP_BY));
        }
        sql.append("bm.bookmark_id, bm.member_id, bm.place_id, bm.created_at, bm.updated_at ");

        // HAVING
        sql.append("HAVING distance <= :distance_limit ");

        // ORDER BY, LIMIT, OFFSET
        sql.append("ORDER BY distance ")
                .append("LIMIT :size_of_page OFFSET :offset;");

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("lat", center.getLat())
                .addValue("lng", center.getLng())
                .addValue("member_id", memberId)
                .addValue("distance_limit", distanceLimit)
                .addValue("size_of_page", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        List<PlaceDto> content = template.query(sql.toString(), params, placeDtoWithImagesRowMapper(numOfPlaceImages));

        // Count total elements
        sqlForCountingTotalElements.append("GROUP BY p.place_id HAVING distance <= :distance_limit) AS total_elements");
        long numOfTotalElements = Optional.ofNullable(
                template.queryForObject(sqlForCountingTotalElements.toString(), params, Long.class)
        ).orElse(0L);

        return new PageImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.asc("distance"))), numOfTotalElements);
    }

    @Override
    public Page<PlaceDto> findMarkedPlaces(Long memberId, FilteringType filteringType, String filteringKeyword, int numOfPlaceImages, Pageable pageable) {
        // SELECT
        StringBuilder sqlBuilderForCountingTotalElements = new StringBuilder()
                .append("SELECT COUNT(*) ")
                .append("FROM (")
                .append("SELECT p.place_id ")
                .append("FROM bookmark bm ")
                .append("JOIN place p ON bm.place_id = p.place_id ");
        StringBuilder sqlBuilder = new StringBuilder("SELECT p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, ");
        for (int i = 1; i <= numOfPlaceImages; i++) {
            sqlBuilder.append(String.join("ri" + i, POSTFIXES_OF_REVIEW_IMAGE_COLUMN_FOR_SELECT));
        }
        sqlBuilder.append("TRUE AS is_marked ");

        // FROM, JOIN
        sqlBuilder.append("FROM bookmark bm ")
                .append("JOIN place p ON bm.place_id = p.place_id ");
        for (int i = 1; i <= numOfPlaceImages; i++) {
            sqlBuilder.append("LEFT JOIN review_image ri")
                    .append(i)
                    .append(" ON ri")
                    .append(i)
                    .append(".review_image_id = (SELECT ri.review_image_id FROM review_image ri JOIN review r ON r.review_id = ri.review_id WHERE r.place_id = p.place_id AND ri.deleted_at IS NULL ORDER BY r.created_at DESC LIMIT 1 OFFSET ")
                    .append(i - 1)
                    .append(") ");
        }

        // WHERE
        String whereClause = "WHERE bm.member_id = :member_id ";
        whereClause += switch (filteringType) {
            case FIRST_CATEGORY -> "AND p.first_category = '" + filteringKeyword + "' ";
            case SECOND_CATEGORY -> "AND p.second_category = '" + filteringKeyword + "' ";
            case TOP_3_KEYWORDS -> "AND p.top3keywords LIKE '%" + filteringKeyword + "%' ";
            case ADDRESS -> "AND p.lot_number_address LIKE '%" + filteringKeyword + "%' ";
            default -> "";
        };
        sqlBuilder.append(whereClause);
        sqlBuilderForCountingTotalElements.append(whereClause);

        // ORDER BY, LIMIT, OFFSET
        sqlBuilder.append("ORDER BY bm.created_at DESC ");
        sqlBuilder.append("LIMIT :size_of_page OFFSET :offset;");

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("size_of_page", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        List<PlaceDto> content = template.query(sqlBuilder.toString(), params, placeDtoWithImagesRowMapper(numOfPlaceImages));

        sqlBuilderForCountingTotalElements.append(") AS num_of_total_elements");
        Long numOfTotalElements = Optional.ofNullable(
                template.queryForObject(sqlBuilderForCountingTotalElements.toString(), params, Long.class)
        ).orElse(0L);

        return new PageImpl<>(
                content,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Order.desc("bookmark.created_at"))
                ),
                numOfTotalElements
        );
    }

    private RowMapper<PlaceDto> placeDtoWithImagesRowMapper(int numOfPlaceImages) {
        return (rs, rowNum) -> {
            ReviewKeywordValueConverter reviewKeywordValueConverter = new ReviewKeywordValueConverter();
            long placeId = rs.getLong("place_id");
            return PlaceDto.of(
                    placeId,
                    reviewKeywordValueConverter.convertToEntityAttribute(rs.getString("top3keywords")),
                    rs.getString("kakao_pid"),
                    rs.getString("name"),
                    rs.getString("page_url"),
                    KakaoCategoryGroupCode.valueOf(rs.getString("category_group_code")),
                    new PlaceCategory(
                            rs.getString("first_category"),
                            rs.getString("second_category"),
                            rs.getString("third_category")
                    ),
                    rs.getString("phone"),
                    new Address(
                            rs.getString("sido"),
                            rs.getString("sgg"),
                            rs.getString("lot_number_address"),
                            rs.getString("road_address")
                    ),
                    rs.getString("homepage_url"),
                    new Point(
                            rs.getString("lat"),
                            rs.getString("lng")
                    ),
                    rs.getString("closing_hours"),
                    null,
                    getRecentFourReviewImageDtosOrderByLatest(rs, numOfPlaceImages),
                    rs.getBoolean("is_marked"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime()
            );
        };
    }

    private List<ReviewImageDto> getRecentFourReviewImageDtosOrderByLatest(ResultSet rs, int numOfPlaceImages) throws SQLException {
        List<ReviewImageDto> reviewImageDtos = new ArrayList<>();

        for (int i = 1; i <= numOfPlaceImages; i++) {
            ReviewImageDto ri = getReviewImageDtoByAlias(rs, "ri" + i);
            if (ri != null) {
                reviewImageDtos.add(ri);
            }
        }

        return reviewImageDtos;
    }

    private ReviewImageDto getReviewImageDtoByAlias(ResultSet rs, String alias) throws SQLException {
        long reviewImageId = rs.getLong(alias + "_review_image_id");

        if (reviewImageId == 0) {
            return null;
        }

        return ReviewImageDto.of(
                reviewImageId,
                rs.getLong(alias + "_review_id"),
                rs.getString(alias + "_original_name"),
                rs.getString(alias + "_stored_name"),
                rs.getString(alias + "_url"),
                rs.getString(alias + "_thumbnail_stored_name"),
                rs.getString(alias + "_thumbnail_url"),
                rs.getTimestamp(alias + "_created_at").toLocalDateTime(),
                rs.getTimestamp(alias + "_updated_at").toLocalDateTime(),
                rs.getTimestamp(alias + "_deleted_at") == null ? null : rs.getTimestamp(alias + "_deleted_at").toLocalDateTime()
        );
    }

    @Override
    public List<PlaceFilteringKeywordDto> getFilteringKeywords(Long memberId) {
        int numOfMarkedPlaces = getNumOfMarkedPlaces(memberId);
        int minCount = numOfMarkedPlaces < 20 ? 3 : 5;

        List<PlaceFilteringKeywordDto> filteringKeywords = new ArrayList<>();
        filteringKeywords.addAll(getFirstCategoryFilteringKeyword(memberId, minCount));
        filteringKeywords.addAll(getSecondCategoryFilteringKeywords(memberId, minCount));
        filteringKeywords.addAll(getAddressFilteringKeywords(memberId, minCount));
        filteringKeywords.addAll(getTop3KeywordFilteringKeywords(memberId, minCount));
        filteringKeywords.sort(Comparator.comparing(PlaceFilteringKeywordDto::getCount));

        return filteringKeywords.size() < MAX_NUM_OF_FILTERING_KEYWORDS
                ? filteringKeywords
                : filteringKeywords.subList(0, MAX_NUM_OF_FILTERING_KEYWORDS);
    }

    /**
     * 저장된 장소의 개수(북마크 개수)를 조회한다.
     *
     * @param memberId PK of member
     * @return 북마크에 저장된 장소의 개수
     */
    private int getNumOfMarkedPlaces(Long memberId) {
        Integer count = template.queryForObject(
                """
                        SELECT COUNT(bm.place_id)
                        FROM bookmark bm
                        WHERE bm.member_id = :member_id;
                        """,
                Map.of("member_id", memberId),
                Integer.class
        );
        return count != null ? count : 0;
    }

    /**
     * 저장된 장소들의 카테고리(first category)에 대해 filtering keywords를 조회한다.
     *
     * @param memberId PK of member
     * @param minCount filtering keyword가 되기 위한 최소 개수 조건. 3 또는 5
     * @return 조회된 filtering keywords
     */
    private List<PlaceFilteringKeywordDto> getFirstCategoryFilteringKeyword(Long memberId, int minCount) {
        String query = """
                SELECT p.first_category        AS keyword,
                       COUNT(p.first_category) AS cnt
                FROM bookmark bm
                         JOIN place p ON bm.place_id = p.place_id
                WHERE bm.member_id = :member_id
                GROUP BY p.first_category
                HAVING cnt >= :min_count;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("min_count", minCount);

        return template.query(query, params, placeFilteringKeywordRowMapper(FilteringType.FIRST_CATEGORY));
    }

    /**
     * 저장된 장소들의 카테고리(second category)에 대해 filtering keywords를 조회한다.
     *
     * @param memberId PK of member
     * @param minCount filtering keyword가 되기 위한 최소 개수 조건. 3 또는 5
     * @return 조회된 filtering keywords
     */
    private List<PlaceFilteringKeywordDto> getSecondCategoryFilteringKeywords(Long memberId, int minCount) {
        String query = """
                SELECT p.second_category        AS keyword,
                       COUNT(p.second_category) AS cnt
                FROM bookmark bm
                         JOIN place p ON bm.place_id = p.place_id
                WHERE bm.member_id = :member_id
                  AND p.second_category IS NOT NULL
                GROUP BY p.second_category
                HAVING cnt >= :min_count;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("min_count", minCount);

        return template.query(query, params, placeFilteringKeywordRowMapper(FilteringType.SECOND_CATEGORY));
    }

    /**
     * 저장된 장소들의 주소에 대해 filtering keywords를 조회한다.
     *
     * @param memberId PK of member
     * @param minCount filtering keyword가 되기 위한 최소 개수 조건. 3 또는 5
     * @return 조회된 filtering keywords
     */
    private List<PlaceFilteringKeywordDto> getAddressFilteringKeywords(Long memberId, int minCount) {
        String query = """
                SELECT SUBSTRING_INDEX(p.lot_number_address, ' ', 1) AS keyword,
                       COUNT(*)                                      AS cnt
                FROM bookmark bm
                         JOIN place p ON bm.place_id = p.place_id
                WHERE bm.member_id = :member_id
                  AND p.lot_number_address IS NOT NULL
                GROUP BY keyword
                HAVING cnt >= :min_count;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("min_count", minCount);

        return template.query(query, params, placeFilteringKeywordRowMapper(FilteringType.ADDRESS));
    }

    /**
     * 저장된 장소들의 top 3 keywords에 대해 filtering keywords를 조회한다.
     *
     * @param memberId PK of member
     * @param minCount filtering keyword가 되기 위한 최소 개수 조건. 3 또는 5
     * @return 조회된 filtering keywords
     */
    private List<PlaceFilteringKeywordDto> getTop3KeywordFilteringKeywords(Long memberId, int minCount) {
        String query = """
                SELECT keyword, COUNT(*) / 2 AS cnt
                FROM (SELECT SUBSTRING_INDEX(p.top3keywords, ' ', 1) AS keyword
                      FROM bookmark bm
                               JOIN place p ON bm.place_id = p.place_id
                      WHERE bm.member_id = :member_id
                        AND CHAR_LENGTH(p.top3keywords) - CHAR_LENGTH(REPLACE(p.top3keywords, ' ', '')) >= 0
                                
                      UNION ALL
                                
                      SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(p.top3keywords, ' ', 2), ' ', -1) AS keyword
                      FROM bookmark bm
                               JOIN place p ON bm.place_id = p.place_id
                      WHERE bm.member_id = :member_id
                        AND CHAR_LENGTH(p.top3keywords) - CHAR_LENGTH(REPLACE(p.top3keywords, ' ', '')) >= 1
                                
                      UNION ALL
                                
                      SELECT SUBSTRING_INDEX(p.top3keywords, ' ', -1) AS keyword
                      FROM bookmark bm
                               JOIN place p ON bm.place_id = p.place_id
                      WHERE bm.member_id = :member_id
                        AND CHAR_LENGTH(p.top3keywords) - CHAR_LENGTH(REPLACE(p.top3keywords, ' ', '')) >= 2) AS t
                WHERE keyword IS NOT NULL
                GROUP BY keyword
                HAVING COUNT(keyword) >= :min_count * 2;
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("min_count", minCount);

        return template.query(query, params, placeTop3KeywordFilteringKeywordRowMapper());
    }

    private RowMapper<PlaceFilteringKeywordDto> placeFilteringKeywordRowMapper(FilteringType filteringType) {
        return (rs, rowNum) -> PlaceFilteringKeywordDto.of(
                rs.getString("keyword"),
                rs.getInt("cnt"),
                filteringType
        );
    }

    private RowMapper<PlaceFilteringKeywordDto> placeTop3KeywordFilteringKeywordRowMapper() {
        return (rs, rowNum) -> PlaceFilteringKeywordDto.of(
                ReviewKeywordValue.valueOf(rs.getString("keyword")).getDescription(),
                (int) rs.getDouble("cnt"),
                FilteringType.TOP_3_KEYWORDS
        );
    }
}
