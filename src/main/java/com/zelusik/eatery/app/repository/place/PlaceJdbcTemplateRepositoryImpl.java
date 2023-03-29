package com.zelusik.eatery.app.repository.place;

import com.zelusik.eatery.app.constant.place.DayOfWeek;
import com.zelusik.eatery.app.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.app.constant.place.PlaceSearchKeyword;
import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.domain.place.PlaceCategory;
import com.zelusik.eatery.app.domain.place.Point;
import com.zelusik.eatery.app.dto.place.PlaceDtoWithImages;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import com.zelusik.eatery.app.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.app.util.domain.ReviewKeywordValueConverter;
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
import java.util.List;

public class PlaceJdbcTemplateRepositoryImpl implements PlaceJdbcTemplateRepository {

    private final NamedParameterJdbcTemplate template;
    private final BookmarkRepository bookmarkRepository;

    public PlaceJdbcTemplateRepositoryImpl(
            DataSource dataSource,
            BookmarkRepository bookmarkRepository
    ) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.bookmarkRepository = bookmarkRepository;
    }

    @Override
    public Slice<PlaceDtoWithImages> findNearBy(Long memberId, List<DayOfWeek> daysOfWeek, PlaceSearchKeyword keyword, String lat, String lng, int distanceLimit, Pageable pageable) {
        StringBuilder sql = new StringBuilder("select p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, p.deleted_at, ")
                .append("rf1.review_file_id as rf1_review_file_id, rf1.review_id as rf1_review_id, rf1.original_name as rf1_original_name, rf1.stored_name as rf1_stored_name, rf1.url as rf1_url, rf1.thumbnail_stored_name as rf1_thumbnail_stored_name, rf1.thumbnail_url as rf1_thumbnail_url, rf1.created_at as rf1_created_at, rf1.updated_at as rf1_updated_at, rf1.deleted_at as rf1_deleted_at, ")
                .append("rf2.review_file_id as rf2_review_file_id, rf2.review_id as rf2_review_id, rf2.original_name as rf2_original_name, rf2.stored_name as rf2_stored_name, rf2.url as rf2_url, rf2.thumbnail_stored_name as rf2_thumbnail_stored_name, rf2.thumbnail_url as rf2_thumbnail_url, rf2.created_at as rf2_created_at, rf2.updated_at as rf2_updated_at, rf2.deleted_at as rf2_deleted_at, ")
                .append("rf3.review_file_id as rf3_review_file_id, rf3.review_id as rf3_review_id, rf3.original_name as rf3_original_name, rf3.stored_name as rf3_stored_name, rf3.url as rf3_url, rf3.thumbnail_stored_name as rf3_thumbnail_stored_name, rf3.thumbnail_url as rf3_thumbnail_url, rf3.created_at as rf3_created_at, rf3.updated_at as rf3_updated_at, rf3.deleted_at as rf3_deleted_at, ")
                .append("(6371 * acos(cos(radians(:lat)) * cos(radians(lat)) * cos(radians(lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(lat)))) as distance ")
                .append("from place p ")
                .append("left join review_file rf1 on rf1.review_file_id = (select rf.review_file_id from review_file rf join review r on r.review_id = rf.review_id where r.place_id = p.place_id order by r.created_at desc limit 1 offset 0) ")
                .append("left join review_file rf2 on rf2.review_file_id = (select rf.review_file_id from review_file rf join review r on r.review_id = rf.review_id where r.place_id = p.place_id order by r.created_at desc limit 1 offset 1) ")
                .append("left join review_file rf3 on rf3.review_file_id = (select rf.review_file_id from review_file rf join review r on r.review_id = rf.review_id where r.place_id = p.place_id order by r.created_at desc limit 1 offset 2) ");

        if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
            sql.append("inner join opening_hours oh ")
                    .append("on p.place_id = oh.place_id ")
                    .append("and (");
            for (int i = 0; i < daysOfWeek.size(); i++) {
                if (i != 0) {
                    sql.append("or ");
                }
                sql.append("oh.day_of_week = '")
                        .append(daysOfWeek.get(i))
                        .append("' ");
            }
            sql.append(") ");
        }

        sql.append("group by p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, p.deleted_at, ")
                .append("rf1.review_file_id, rf1.review_id, rf1.original_name, rf1.stored_name, rf1.url, rf1.thumbnail_stored_name, rf1.thumbnail_url, rf1.created_at, rf1.updated_at, rf1.deleted_at, ")
                .append("rf2.review_file_id, rf2.review_id, rf2.original_name, rf2.stored_name, rf2.url, rf2.thumbnail_stored_name, rf2.thumbnail_url, rf2.created_at, rf2.updated_at, rf2.deleted_at, ")
                .append("rf3.review_file_id, rf3.review_id, rf3.original_name, rf3.stored_name, rf3.url, rf3.thumbnail_stored_name, rf3.thumbnail_url, rf3.created_at, rf3.updated_at, rf3.deleted_at ")
                .append("having distance <= :distance_limit ")
                .append("order by distance ")
                .append("limit :size_of_page ")
                .append("offset :offset");

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("lat", lat)
                .addValue("lng", lng)
                .addValue("distance_limit", distanceLimit)
                .addValue("size_of_page", pageable.getPageSize() + 1)   // 다음 페이지 존재 여부 확인을 위함.
                .addValue("offset", pageable.getOffset());

        List<PlaceDtoWithImages> content = template.query(sql.toString(), param, placeDtoWithImagesRowMapper(memberId));

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<PlaceDtoWithImages> findMarkedPlaces(Long memberId, Pageable pageable) {
        String sql = """
                select p.place_id,
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
                       p.deleted_at,
                       rf1.review_file_id        as rf1_review_file_id,
                       rf1.review_id             as rf1_review_id,
                       rf1.original_name         as rf1_original_name,
                       rf1.stored_name           as rf1_stored_name,
                       rf1.url                   as rf1_url,
                       rf1.thumbnail_stored_name as rf1_thumbnail_stored_name,
                       rf1.thumbnail_url         as rf1_thumbnail_url,
                       rf1.created_at            as rf1_created_at,
                       rf1.updated_at            as rf1_updated_at,
                       rf1.deleted_at            as rf1_deleted_at,
                       rf2.review_file_id        as rf2_review_file_id,
                       rf2.review_id             as rf2_review_id,
                       rf2.original_name         as rf2_original_name,
                       rf2.stored_name           as rf2_stored_name,
                       rf2.url                   as rf2_url,
                       rf2.thumbnail_stored_name as rf2_thumbnail_stored_name,
                       rf2.thumbnail_url         as rf2_thumbnail_url,
                       rf2.created_at            as rf2_created_at,
                       rf2.updated_at            as rf2_updated_at,
                       rf2.deleted_at            as rf2_deleted_at,
                       rf3.review_file_id        as rf3_review_file_id,
                       rf3.review_id             as rf3_review_id,
                       rf3.original_name         as rf3_original_name,
                       rf3.stored_name           as rf3_stored_name,
                       rf3.url                   as rf3_url,
                       rf3.thumbnail_stored_name as rf3_thumbnail_stored_name,
                       rf3.thumbnail_url         as rf3_thumbnail_url,
                       rf3.created_at            as rf3_created_at,
                       rf3.updated_at            as rf3_updated_at,
                       rf3.deleted_at            as rf3_deleted_at
                from bookmark bm
                         join place p on bm.place_id = p.place_id
                         left join review_file rf1 on rf1.review_file_id = (select rf.review_file_id
                                                                            from review_file rf
                                                                                     join review r on r.review_id = rf.review_id
                                                                            where r.place_id = p.place_id
                                                                            order by r.created_at desc
                                                                            limit 1 offset 0)
                         left join review_file rf2 on rf2.review_file_id = (select rf.review_file_id
                                                                            from review_file rf
                                                                                     join review r on r.review_id = rf.review_id
                                                                            where r.place_id = p.place_id
                                                                            order by r.created_at desc
                                                                            limit 1 offset 1)
                         left join review_file rf3 on rf3.review_file_id = (select rf.review_file_id
                                                                            from review_file rf
                                                                                     join review r on r.review_id = rf.review_id
                                                                            where r.place_id = p.place_id
                                                                            order by r.created_at desc
                                                                            limit 1 offset 2)
                where bm.member_id = :member_id
                order by bm.created_at desc
                limit :size_of_page offset :offset;
                """;

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("size_of_page", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        List<PlaceDtoWithImages> content = template.query(sql, param, placeDtoWithImagesRowMapper(memberId));

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private RowMapper<PlaceDtoWithImages> placeDtoWithImagesRowMapper(Long memberId) {
        return (rs, rowNum) -> {
            ReviewKeywordValueConverter reviewKeywordValueConverter = new ReviewKeywordValueConverter();

            long placeId = rs.getLong("place_id");

            List<Long> markedPlaceIdList = bookmarkRepository.findAllMarkedPlaceId(memberId);
            boolean isMarked = markedPlaceIdList != null && markedPlaceIdList.contains(placeId);

            return PlaceDtoWithImages.of(
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
                    getTop3ReviewFileDtosOrderByLatest(rs),
                    isMarked,
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toLocalDateTime()
            );
        };
    }

    private List<ReviewFileDto> getTop3ReviewFileDtosOrderByLatest(ResultSet rs) throws SQLException {
        List<ReviewFileDto> reviewFileDtos = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            ReviewFileDto rf = getReviewFileDtoByAlias(rs, "rf" + i);
            if (rf != null) {
                reviewFileDtos.add(rf);
            }
        }

        return reviewFileDtos;
    }

    private ReviewFileDto getReviewFileDtoByAlias(ResultSet rs, String alias) throws SQLException {
        long reviewFileId = rs.getLong(alias + "_review_file_id");

        if (reviewFileId == 0) {
            return null;
        }

        return ReviewFileDto.of(
                reviewFileId,
                rs.getLong(alias + "_review_id"),
                rs.getString(alias + "_original_name"),
                rs.getString(alias + "_stored_name"),
                rs.getString(alias + "_url"),
                rs.getString(alias + "_thumbnail_stored_name"),
                rs.getString(alias + "_thumbnail_url"),
                rs.getTimestamp(alias + "_created_at").toLocalDateTime(),
                rs.getTimestamp(alias + "_updated_at").toLocalDateTime(),
                rs.getTimestamp(alias + "_deleted_at") == null ? null : rs.getTimestamp("rf1_deleted_at").toLocalDateTime()
        );
    }
}
