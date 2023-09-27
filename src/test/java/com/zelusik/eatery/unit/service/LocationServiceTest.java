package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.location.dto.LocationDto;
import com.zelusik.eatery.domain.location.repository.LocationRepository;
import com.zelusik.eatery.domain.location.service.LocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Location Service")
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @InjectMocks
    private LocationService sut;

    @Mock
    private LocationRepository locationRepository;

    @DisplayName("검색 키워드가 주어지고, 검색하면, 검색된 location dto 객체들이 반환된다.")
    @Test
    void givenKeyword_whenSearchingByKeyword_thenReturnLocationDtos() {
        // given
        String keyword = "서울";
        Pageable pageable = Pageable.ofSize(15);
        List<LocationDto> expectedContents = List.of(
                LocationDto.of("서울특별시", "강동구", "암사제1동", new Point("37.551508", "127.132663")),
                LocationDto.of("서울특별시", "종로구", "숭인동", new Point("37.5778049", "127.0156274")),
                LocationDto.of("서울특별시", "서초구", "방배2동", new Point("37.4797439", "126.9855106")),
                LocationDto.of("서울특별시", "종로구", "연지동", new Point("37.5737", "127.0002")),
                LocationDto.of("서울특별시", "중구", "남대문로4가", new Point("37.5605942", "126.975609"))
        );
        given(locationRepository.searchDtosByKeyword(keyword, pageable)).willReturn(new PageImpl<>(expectedContents));

        // when
        Page<LocationDto> actualResult = sut.searchDtosByKeyword(keyword, pageable);

        // then
        then(locationRepository).should().searchDtosByKeyword(keyword, pageable);
        then(locationRepository).shouldHaveNoMoreInteractions();
        assertThat(actualResult.getNumberOfElements()).isEqualTo(5);
    }
}