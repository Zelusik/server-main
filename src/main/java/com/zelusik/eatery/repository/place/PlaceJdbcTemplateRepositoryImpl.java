package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.place.PlaceSearchKeyword;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDtoWithImages;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import com.zelusik.eatery.repository.bookmark.BookmarkRepository;
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
        StringBuilder sql = new StringBuilder("select p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, ")
                .append("ri1.review_image_id as ri1_review_image_id, ri1.review_id as ri1_review_id, ri1.original_name as ri1_original_name, ri1.stored_name as ri1_stored_name, ri1.url as ri1_url, ri1.thumbnail_stored_name as ri1_thumbnail_stored_name, ri1.thumbnail_url as ri1_thumbnail_url, ri1.created_at as ri1_created_at, ri1.updated_at as ri1_updated_at, ri1.deleted_at as ri1_deleted_at, ")
                .append("ri2.review_image_id as ri2_review_image_id, ri2.review_id as ri2_review_id, ri2.original_name as ri2_original_name, ri2.stored_name as ri2_stored_name, ri2.url as ri2_url, ri2.thumbnail_stored_name as ri2_thumbnail_stored_name, ri2.thumbnail_url as ri2_thumbnail_url, ri2.created_at as ri2_created_at, ri2.updated_at as ri2_updated_at, ri2.deleted_at as ri2_deleted_at, ")
                .append("ri3.review_image_id as ri3_review_image_id, ri3.review_id as ri3_review_id, ri3.original_name as ri3_original_name, ri3.stored_name as ri3_stored_name, ri3.url as ri3_url, ri3.thumbnail_stored_name as ri3_thumbnail_stored_name, ri3.thumbnail_url as ri3_thumbnail_url, ri3.created_at as ri3_created_at, ri3.updated_at as ri3_updated_at, ri3.deleted_at as ri3_deleted_at, ")
                .append("(6371 * acos(cos(radians(:lat)) * cos(radians(lat)) * cos(radians(lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(lat)))) as distance ")
                .append("from place p ")
                .append("left join review_image ri1 on ri1.review_image_id = (select ri.review_image_id from review_image ri join review r on r.review_id = ri.review_id and ri.deleted_at is null where r.place_id = p.place_id order by r.created_at desc limit 1 offset 0) ")
                .append("left join review_image ri2 on ri2.review_image_id = (select ri.review_image_id from review_image ri join review r on r.review_id = ri.review_id and ri.deleted_at is null where r.place_id = p.place_id order by r.created_at desc limit 1 offset 1) ")
                .append("left join review_image ri3 on ri3.review_image_id = (select ri.review_image_id from review_image ri join review r on r.review_id = ri.review_id and ri.deleted_at is null where r.place_id = p.place_id order by r.created_at desc limit 1 offset 2) ");

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

        sql.append("group by p.place_id, p.top3keywords, p.kakao_pid, p.name, p.page_url, p.category_group_code, p.first_category, p.second_category, p.third_category, p.phone, p.sido, p.sgg, p.lot_number_address, p.road_address, p.homepage_url, p.lat, p.lng, p.closing_hours, p.created_at, p.updated_at, ")
                .append("ri1.review_image_id, ri1.review_id, ri1.original_name, ri1.stored_name, ri1.url, ri1.thumbnail_stored_name, ri1.thumbnail_url, ri1.created_at, ri1.updated_at, ri1.deleted_at, ")
                .append("ri2.review_image_id, ri2.review_id, ri2.original_name, ri2.stored_name, ri2.url, ri2.thumbnail_stored_name, ri2.thumbnail_url, ri2.created_at, ri2.updated_at, ri2.deleted_at, ")
                .append("ri3.review_image_id, ri3.review_id, ri3.original_name, ri3.stored_name, ri3.url, ri3.thumbnail_stored_name, ri3.thumbnail_url, ri3.created_at, ri3.updated_at, ri3.deleted_at ")
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
                       ri1.review_image_id        as ri1_review_image_id,
                       ri1.review_id             as ri1_review_id,
                       ri1.original_name         as ri1_original_name,
                       ri1.stored_name           as ri1_stored_name,
                       ri1.url                   as ri1_url,
                       ri1.thumbnail_stored_name as ri1_thumbnail_stored_name,
                       ri1.thumbnail_url         as ri1_thumbnail_url,
                       ri1.created_at            as ri1_created_at,
                       ri1.updated_at            as ri1_updated_at,
                       ri1.deleted_at            as ri1_deleted_at,
                       ri2.review_image_id       as ri2_review_image_id,
                       ri2.review_id             as ri2_review_id,
                       ri2.original_name         as ri2_original_name,
                       ri2.stored_name           as ri2_stored_name,
                       ri2.url                   as ri2_url,
                       ri2.thumbnail_stored_name as ri2_thumbnail_stored_name,
                       ri2.thumbnail_url         as ri2_thumbnail_url,
                       ri2.created_at            as ri2_created_at,
                       ri2.updated_at            as ri2_updated_at,
                       ri2.deleted_at            as ri2_deleted_at,
                       ri3.review_image_id       as ri3_review_image_id,
                       ri3.review_id             as ri3_review_id,
                       ri3.original_name         as ri3_original_name,
                       ri3.stored_name           as ri3_stored_name,
                       ri3.url                   as ri3_url,
                       ri3.thumbnail_stored_name as ri3_thumbnail_stored_name,
                       ri3.thumbnail_url         as ri3_thumbnail_url,
                       ri3.created_at            as ri3_created_at,
                       ri3.updated_at            as ri3_updated_at,
                       ri3.deleted_at            as ri3_deleted_at
                from bookmark bm
                         join place p on bm.place_id = p.place_id
                         left join review_image ri1 on ri1.review_image_id = (select ri.review_image_id
                                                                            from review_image ri
                                                                                     join review r on r.review_id = ri.review_id
                                                                            where r.place_id = p.place_id
                                                                                and ri.deleted_at is null
                                                                            order by r.created_at desc
                                                                            limit 1 offset 0)
                         left join review_image ri2 on ri2.review_image_id = (select ri.review_image_id
                                                                            from review_image ri
                                                                                     join review r on r.review_id = ri.review_id
                                                                            where r.place_id = p.place_id
                                                                                and ri.deleted_at is null
                                                                            order by r.created_at desc
                                                                            limit 1 offset 1)
                         left join review_image ri3 on ri3.review_image_id = (select ri.review_image_id
                                                                            from review_image ri
                                                                                     join review r on r.review_id = ri.review_id
                                                                            where r.place_id = p.place_id
                                                                                and ri.deleted_at is null
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
        List<Long> markedPlaceIdList = bookmarkRepository.findAllMarkedPlaceId(memberId);

        return (rs, rowNum) -> {
            ReviewKeywordValueConverter reviewKeywordValueConverter = new ReviewKeywordValueConverter();

            long placeId = rs.getLong("place_id");
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
                    getTop3ReviewImageDtosOrderByLatest(rs),
                    isMarked,
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime()
            );
        };
    }

    private List<ReviewImageDto> getTop3ReviewImageDtosOrderByLatest(ResultSet rs) throws SQLException {
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
}