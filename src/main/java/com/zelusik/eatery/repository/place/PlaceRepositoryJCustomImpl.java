package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.place.PlaceSearchKeyword;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatusAndImages;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import com.zelusik.eatery.util.domain.ReviewKeywordValueConverter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.zelusik.eatery.constant.ConstantUtil.MAX_NUM_OF_FILTERING_KEYWORDS;

public class PlaceRepositoryJCustomImpl implements PlaceRepositoryJCustom {

    private final NamedParameterJdbcTemplate template;

    public PlaceRepositoryJCustomImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Slice<PlaceDtoWithMarkedStatusAndImages> findDtosNearBy(
            Long memberId,
            List<DayOfWeek> daysOfWeek,
            PlaceSearchKeyword keyword,
            String lat,
            String lng,
            int distanceLimit,
            Pageable pageable
    ) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, ")
                .append("ri1.review_image_id AS ri1_review_image_id, ri1.review_id AS ri1_review_id, ri1.original_name AS ri1_original_name, ri1.stored_name AS ri1_stored_name, ri1.url AS ri1_url, ri1.thumbnail_stored_name AS ri1_thumbnail_stored_name, ri1.thumbnail_url AS ri1_thumbnail_url, ri1.created_at AS ri1_created_at, ri1.updated_at AS ri1_updated_at, ri1.deleted_at AS ri1_deleted_at, ")
                .append("ri2.review_image_id AS ri2_review_image_id, ri2.review_id AS ri2_review_id, ri2.original_name AS ri2_original_name, ri2.stored_name AS ri2_stored_name, ri2.url AS ri2_url, ri2.thumbnail_stored_name AS ri2_thumbnail_stored_name, ri2.thumbnail_url AS ri2_thumbnail_url, ri2.created_at AS ri2_created_at, ri2.updated_at AS ri2_updated_at, ri2.deleted_at AS ri2_deleted_at, ")
                .append("ri3.review_image_id AS ri3_review_image_id, ri3.review_id AS ri3_review_id, ri3.original_name AS ri3_original_name, ri3.stored_name AS ri3_stored_name, ri3.url AS ri3_url, ri3.thumbnail_stored_name AS ri3_thumbnail_stored_name, ri3.thumbnail_url AS ri3_thumbnail_url, ri3.created_at AS ri3_created_at, ri3.updated_at AS ri3_updated_at, ri3.deleted_at AS ri3_deleted_at, ")
                .append("(6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:lng)) + SIN(RADIANS(:lat)) * SIN(RADIANS(lat)))) AS distance, ")
                .append("CASE WHEN bm.bookmark_id IS NULL THEN FALSE ELSE TRUE END AS is_marked ")
                .append("FROM place p ")
                .append("LEFT JOIN review_image ri1 ON ri1.review_image_id = (SELECT ri.review_image_id FROM review_image ri JOIN review r ON r.review_id = ri.review_id AND ri.deleted_at IS NULL WHERE r.place_id = p.place_id ORDER BY r.created_at DESC LIMIT 1 OFFSET 0) ")
                .append("LEFT JOIN review_image ri2 ON ri2.review_image_id = (SELECT ri.review_image_id FROM review_image ri JOIN review r ON r.review_id = ri.review_id AND ri.deleted_at IS NULL WHERE r.place_id = p.place_id ORDER BY r.created_at DESC LIMIT 1 OFFSET 1) ")
                .append("LEFT JOIN review_image ri3 ON ri3.review_image_id = (SELECT ri.review_image_id FROM review_image ri JOIN review r ON r.review_id = ri.review_id AND ri.deleted_at IS NULL WHERE r.place_id = p.place_id ORDER BY r.created_at DESC LIMIT 1 OFFSET 2) ")
                .append("LEFT JOIN bookmark bm ON p.place_id = bm.place_id AND bm.member_id = :member_id ");

        if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
            sql.append("JOIN opening_hours oh ")
                    .append("ON p.place_id = oh.place_id ")
                    .append("AND (");
            for (int i = 0; i < daysOfWeek.size(); i++) {
                if (i != 0) {
                    sql.append("OR ");
                }
                sql.append("oh.day_of_week = '")
                        .append(daysOfWeek.get(i))
                        .append("' ");
            }
            sql.append(") ");
        }

        sql.append("GROUP BY p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, ")
                .append("ri1.review_image_id, ri1.review_id, ri1.original_name, ri1.stored_name, ri1.url, ri1.thumbnail_stored_name, ri1.thumbnail_url, ri1.created_at, ri1.updated_at, ri1.deleted_at, ")
                .append("ri2.review_image_id, ri2.review_id, ri2.original_name, ri2.stored_name, ri2.url, ri2.thumbnail_stored_name, ri2.thumbnail_url, ri2.created_at, ri2.updated_at, ri2.deleted_at, ")
                .append("ri3.review_image_id, ri3.review_id, ri3.original_name, ri3.stored_name, ri3.url, ri3.thumbnail_stored_name, ri3.thumbnail_url, ri3.created_at, ri3.updated_at, ri3.deleted_at, ")
                .append("bm.bookmark_id, bm.member_id, bm.place_id, bm.created_at, bm.updated_at ")
                .append("HAVING distance <= :distance_limit ")
                .append("ORDER BY distance ")
                .append("LIMIT :size_of_page OFFSET :offset;");

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("lat", lat)
                .addValue("lng", lng)
                .addValue("member_id", memberId)
                .addValue("distance_limit", distanceLimit)
                .addValue("size_of_page", pageable.getPageSize() + 1)   // 다음 페이지 존재 여부 확인을 위함.
                .addValue("offset", pageable.getOffset());

        List<PlaceDtoWithMarkedStatusAndImages> content = template.query(sql.toString(), params, placeDtoWithImagesRowMapper());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<PlaceDtoWithMarkedStatusAndImages> findMarkedPlaces(Long memberId, FilteringType filteringType, String filteringKeyword, Pageable pageable) {
        String sql = """
                SELECT p.place_id,
                       p.top3keywords,
                       p.kakao_pid,
                       p.name,
                       p.page_url,
                       p.category_group_code,
                       p.first_category,
                       p.second_category,
                       p.third_category,
                       p.phone,
                       p.sido,
                       p.sgg,
                       p.lot_number_address,
                       p.road_address,
                       p.homepage_url,
                       p.lat,
                       p.lng,
                       p.closing_hours,
                       p.created_at,
                       p.updated_at,
                       ri1.review_image_id       AS ri1_review_image_id,
                       ri1.review_id             AS ri1_review_id,
                       ri1.original_name         AS ri1_original_name,
                       ri1.stored_name           AS ri1_stored_name,
                       ri1.url                   AS ri1_url,
                       ri1.thumbnail_stored_name AS ri1_thumbnail_stored_name,
                       ri1.thumbnail_url         AS ri1_thumbnail_url,
                       ri1.created_at            AS ri1_created_at,
                       ri1.updated_at            AS ri1_updated_at,
                       ri1.deleted_at            AS ri1_deleted_at,
                       ri2.review_image_id       AS ri2_review_image_id,
                       ri2.review_id             AS ri2_review_id,
                       ri2.original_name         AS ri2_original_name,
                       ri2.stored_name           AS ri2_stored_name,
                       ri2.url                   AS ri2_url,
                       ri2.thumbnail_stored_name AS ri2_thumbnail_stored_name,
                       ri2.thumbnail_url         AS ri2_thumbnail_url,
                       ri2.created_at            AS ri2_created_at,
                       ri2.updated_at            AS ri2_updated_at,
                       ri2.deleted_at            AS ri2_deleted_at,
                       ri3.review_image_id       AS ri3_review_image_id,
                       ri3.review_id             AS ri3_review_id,
                       ri3.original_name         AS ri3_original_name,
                       ri3.stored_name           AS ri3_stored_name,
                       ri3.url                   AS ri3_url,
                       ri3.thumbnail_stored_name AS ri3_thumbnail_stored_name,
                       ri3.thumbnail_url         AS ri3_thumbnail_url,
                       ri3.created_at            AS ri3_created_at,
                       ri3.updated_at            AS ri3_updated_at,
                       ri3.deleted_at            AS ri3_deleted_at,
                       TRUE                      AS is_marked
                FROM bookmark bm
                         JOIN place p ON bm.place_id = p.place_id
                         LEFT JOIN review_image ri1 ON ri1.review_image_id = (SELECT ri.review_image_id
                                                                              FROM review_image ri
                                                                                       JOIN review r ON r.review_id = ri.review_id
                                                                              WHERE r.place_id = p.place_id
                                                                                AND ri.deleted_at IS NULL
                                                                              ORDER BY r.created_at DESC
                                                                              LIMIT 1 OFFSET 0)
                         LEFT JOIN review_image ri2 ON ri2.review_image_id = (SELECT ri.review_image_id
                                                                              FROM review_image ri
                                                                                       JOIN review r ON r.review_id = ri.review_id
                                                                              WHERE r.place_id = p.place_id
                                                                                AND ri.deleted_at IS NULL
                                                                              ORDER BY r.created_at DESC
                                                                              LIMIT 1 OFFSET 1)
                         LEFT JOIN review_image ri3 ON ri3.review_image_id = (SELECT ri.review_image_id
                                                                              FROM review_image ri
                                                                                       JOIN review r ON r.review_id = ri.review_id
                                                                              WHERE r.place_id = p.place_id
                                                                                AND ri.deleted_at IS NULL
                                                                              ORDER BY r.created_at DESC
                                                                              LIMIT 1 OFFSET 2)
                WHERE bm.member_id = :member_id
                """;

        sql += switch (filteringType) {
            case FIRST_CATEGORY -> "AND p.first_category = '" + filteringKeyword + "' ";
            case SECOND_CATEGORY -> "AND p.second_category = '" + filteringKeyword + "' ";
            case TOP_3_KEYWORDS -> "AND p.top3keywords LIKE '%" + filteringKeyword + "%' ";
            case ADDRESS -> "AND p.lot_number_address LIKE '%" + filteringKeyword + "%' ";
            case NONE -> "";
        };

        sql += "ORDER BY bm.created_at DESC ";
        sql += "LIMIT :size_of_page OFFSET :offset;";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("size_of_page", pageable.getPageSize() + 1)
                .addValue("offset", pageable.getOffset());

        List<PlaceDtoWithMarkedStatusAndImages> content = template.query(sql, params, placeDtoWithImagesRowMapper());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
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

    private List<ReviewImageDto> getRecent3ReviewImageDtosOrderByLatest(ResultSet rs) throws SQLException {
        List<ReviewImageDto> reviewImageDtos = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
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

    private RowMapper<PlaceDtoWithMarkedStatusAndImages> placeDtoWithImagesRowMapper() {
        return (rs, rowNum) -> {
            ReviewKeywordValueConverter reviewKeywordValueConverter = new ReviewKeywordValueConverter();
            long placeId = rs.getLong("place_id");
            return PlaceDtoWithMarkedStatusAndImages.of(
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
                    getRecent3ReviewImageDtosOrderByLatest(rs),
                    rs.getBoolean("is_marked"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime()
            );
        };
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
