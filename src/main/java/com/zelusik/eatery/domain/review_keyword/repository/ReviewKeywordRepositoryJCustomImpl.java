package com.zelusik.eatery.domain.review_keyword.repository;

import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.List;

public class ReviewKeywordRepositoryJCustomImpl implements ReviewKeywordRepositoryJCustom {

    private final NamedParameterJdbcTemplate template;

    public ReviewKeywordRepositoryJCustomImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<ReviewKeywordValue> searchTop3Keywords(Long placeId) {
        String sql = "SELECT rk.keyword " +
                "FROM review r " +
                "JOIN place p ON r.place_id = p.place_id AND p.place_id = :place_id " +
                "JOIN review_keyword rk ON r.review_id = rk.review_id " +
                "WHERE r.deleted_at IS NULL " +
                "GROUP BY rk.keyword " +
                "ORDER BY COUNT(rk.keyword) DESC " +
                "LIMIT 3";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("place_id", placeId);

        return template.queryForList(sql, param, ReviewKeywordValue.class);
    }
}
