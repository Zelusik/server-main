package com.zelusik.eatery.app.repository.bookmark;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.List;

public class BookmarkJdbcTemplateRepositoryImpl implements BookmarkJdbcTemplateRepository {

    private final NamedParameterJdbcTemplate template;

    public BookmarkJdbcTemplateRepositoryImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }


    @Override
    public List<Long> findAllMarkedPlaceId(Long memberId) {
        String sql = "SELECT p.place_id " +
                "FROM bookmark b " +
                "INNER JOIN place p " +
                "on b.place_id = p.place_id " +
                "where b.member_id = :member_id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("member_id", memberId);

        return template.queryForList(sql, param, Long.class);
    }
}
