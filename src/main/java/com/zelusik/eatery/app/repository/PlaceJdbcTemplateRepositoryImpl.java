package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.app.domain.place.Address;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.place.PlaceCategory;
import com.zelusik.eatery.app.domain.place.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.List;

public class PlaceJdbcTemplateRepositoryImpl implements PlaceJdbcTemplateRepository {

    private final NamedParameterJdbcTemplate template;

    public PlaceJdbcTemplateRepositoryImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Slice<Place> findNearBy(String lat, String lng, int distanceLimit, Pageable pageable) {
        String sql = "select place_id, kakao_pid, name, page_url, category_group_code, first_category, second_category, third_category, phone, sido, sgg, lot_number_address, road_address, homepage_url, lat, lng, closing_hours, created_at, updated_at, deleted_at, " +
                "(6371 * acos(cos(radians(:lat)) * cos(radians(lat)) * cos(radians(lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(lat)))) as distance " +
                "from place " +
                "group by place_id, kakao_pid, name, page_url, category_group_code, first_category, second_category, third_category, phone, sido, sgg, lot_number_address, road_address, homepage_url, lat, lng, closing_hours, created_at, updated_at, deleted_at " +
                "having distance <= :distance_limit " +
                "order by distance " +
                "limit :size " +
                "offset :offset";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("lat", lat)
                .addValue("lng", lng)
                .addValue("distance_limit", distanceLimit)
                .addValue("size", pageable.getPageSize() + 1)   // 다음 페이지 존재 여부 확인을 위함.
                .addValue("offset", pageable.getOffset());

        List<Place> content = template.query(sql, param, placeRowMapper());

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private RowMapper<Place> placeRowMapper() {
        return (rs, rowNum) -> Place.of(
                rs.getLong("place_id"),
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
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toLocalDateTime()
        );
    }
}
