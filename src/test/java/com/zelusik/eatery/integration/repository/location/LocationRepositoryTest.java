package com.zelusik.eatery.integration.repository.location;

import com.zelusik.eatery.global.config.QuerydslConfig;
import com.zelusik.eatery.domain.location.entity.Location;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.location.dto.LocationDto;
import com.zelusik.eatery.domain.location.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Integration] Location Repository")
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void createLocations() {
        List<Location> locations = List.of(
                Location.of("서울특별시", "강동구", "암사제1동", new Point("37.551508", "127.132663")),
                Location.of("서울특별시", "종로구", "숭인동", new Point("37.5778049", "127.0156274")),
                Location.of("서울특별시", "서초구", "방배2동", new Point("37.4797439", "126.9855106")),
                Location.of("서울특별시", "종로구", "연지동", new Point("37.5737", "127.0002")),
                Location.of("서울특별시", "중구", "남대문로4가", new Point("37.5605942", "126.975609")),
                Location.of("서울특별시", "동대문구", "전농제2동", new Point("37.5781027", "127.0600375")),
                Location.of("서울특별시", "영등포구", "신길동", new Point("37.5112667", "126.9214285")),
                Location.of("서울특별시", "동작구", "상도1동", new Point("37.4981", "126.953089")),
                Location.of("서울특별시", "종로구", "신교동", new Point("37.5845", "126.9678")),
                Location.of("서울특별시", "종로구", "신영동", new Point("37.60294", "126.9621")),
                Location.of("서울특별시", "양천구", "신정6동", new Point("37.5170414", "126.8644471")),
                Location.of("서울특별시", "용산구", "원효로2가", new Point("37.536775", "126.963225")),
                Location.of("서울특별시", "영등포구", "양평제2동", new Point("37.5364739", "126.8939535")),
                Location.of("서울특별시", "종로구", "사직동", new Point("37.576196", "126.9688397")),
                Location.of("서울특별시", "서대문구", "대신동", new Point("37.565502", "126.9459748")),
                Location.of("서울특별시", "용산구", "이태원제1동", new Point("37.5325225", "126.9950384")),
                Location.of("서울특별시", "마포구", null, new Point("37.5663245", "126.901491")),
                Location.of("서울특별시", "동대문구", "장안제1동", new Point("37.567842", "127.066375")),
                Location.of("서울특별시", "송파구", "오륜동", new Point("37.515425", "127.1343")),
                Location.of("서울특별시", "도봉구", "도봉제1동", new Point("37.6786913", "127.0434369"))
        );
        locationRepository.saveAll(locations);
    }

    @DisplayName("검색 키워드가 주어지고, 키워드로 검색하면, 검색 결과가 반환된다.")
    @Test
    void givenKeyword_whenSearchingByKeyword_thenReturnSearchResults() {
        // given
        String keyword = "종로";

        // when
        Page<LocationDto> result = locationRepository.searchDtosByKeyword(keyword, Pageable.ofSize(15));

        System.out.println("result.getTotalPages() = " + result.getTotalPages());
        System.out.println("result.getTotalElements() = " + result.getTotalElements());
        System.out.println("result.getNumber() = " + result.getNumber());

        // then
        assertThat(result.getNumberOfElements()).isEqualTo(5);
        assertThat(result.hasNext()).isFalse();
        result.forEach(res -> assertThat(
                res.getSido().contains(keyword)
                        || res.getSgg().contains(keyword)
                        || res.getEmdg().contains(keyword)
        ).isTrue());
    }

    @DisplayName("검색 키워드가 주어지고, 키워드로 두 번째 페이지(page 1)를 검색하면, 검색 결과가 반환된다.")
    @Test
    void givenKeyword_whenSearchingSecondPageByKeyword_thenReturnSearchResults() {
        // given
        String keyword = "서울";

        // when
        Page<LocationDto> result = locationRepository.searchDtosByKeyword(keyword, PageRequest.of(1, 15));

        System.out.println("result.getTotalPages() = " + result.getTotalPages());
        System.out.println("result.getTotalElements() = " + result.getTotalElements());
        System.out.println("result.getNumber() = " + result.getNumber());

        // then
        assertThat(result.getNumberOfElements()).isEqualTo(5);
        assertThat(result.hasNext()).isFalse();
        result.forEach(res -> assertThat(
                res.getSido().contains(keyword)
                        || res.getSgg().contains(keyword)
                        || res.getEmdg().contains(keyword)
        ).isTrue());
    }
}