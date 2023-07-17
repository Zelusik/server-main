package com.zelusik.eatery.integration.repository.place;

import com.zelusik.eatery.config.JpaConfig;
import com.zelusik.eatery.config.QuerydslConfig;
import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.repository.place.OpeningHoursRepository;
import com.zelusik.eatery.repository.place.PlaceRepository;
import com.zelusik.eatery.util.PlaceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 대한민국 북-남 거리 약 1,100km
 */

@DisplayName("[Integration] Place Repository")
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JpaConfig.class})
@DataJpaTest
class PlaceRepositoryTest {

    private final PlaceRepository placeRepository;
    private final OpeningHoursRepository openingHoursRepository;

    public PlaceRepositoryTest(
            @Autowired PlaceRepository placeRepository,
            @Autowired OpeningHoursRepository openingHoursRepository
    ) {
        this.placeRepository = placeRepository;
        this.openingHoursRepository = openingHoursRepository;
    }

    @DisplayName("장소들이 존재하고, 중심 좌표 주변의 장소를 조회하면, 거리순으로 정렬된 장소 목록이 반환된다.")
    @Test
    void givenPlaces_whenFindNearBy_thenReturnPlaceSliceSortedByDistance() {
        // given
        Long memberId = 1L;
        String centerLat = "37";
        String centerLng = "127";
        for (int i = 0; i < 50; i++) {
            placeRepository.save(PlaceTestUtils.createPlace(
                    (long) (i + 1),
                    String.valueOf(i),
                    null,
                    centerLat,
                    String.valueOf(Integer.parseInt(centerLng) + Math.random()),
                    null
            ));
        }

        // when
        Slice<PlaceDto> places = placeRepository.findDtosNearBy(memberId, List.of(), null, centerLat, centerLng, 1100, PageRequest.of(0, 30));

        // then
        assertThat(places.getSize()).isEqualTo(30);
        assertThat(places.hasNext()).isTrue();
        for (int i = 0; i < places.getSize() - 1; i++) {
            PlaceDto curPlace = places.getContent().get(i);
            PlaceDto nextPlace = places.getContent().get(i + 1);
            assertThat(calculateDiff(centerLng, curPlace.getPoint().getLng()))
                    .isLessThanOrEqualTo(calculateDiff(centerLng, nextPlace.getPoint().getLng()));
        }
    }

    @DisplayName("주변 장소를 조회하면, 50km와 1100km 내에 있는 가게들을 조회한다.")
    @Test
    void givenPlaces_whenFindNearBy_thenReturnPlacesWith50kmAnd1100km() {
        // given
        Long memberId = 1L;
        Point pos = new Point("37.5776087830657", "126.976896737645");  // 경복궁
        Place place1 = placeRepository.save(PlaceTestUtils.createPlace(1L, "1", "성심당", "36.32765802936324", "127.42727719121109", null));    // 대전
        place1.getOpeningHoursList().add(openingHoursRepository.save(PlaceTestUtils.createOpeningHours(1L, place1, DayOfWeek.MON)));
        placeRepository.save(PlaceTestUtils.createPlace(2L, "2", "해운대암소갈비집", "35.163310169485634", "129.1666092786243", null));  // 부산
        Place place3 = placeRepository.save(PlaceTestUtils.createPlace(3L, "3", "연남토마 본점", "37.5595073462493", "126.921462488105", null));   // 서울
        place3.getOpeningHoursList().add(openingHoursRepository.save(PlaceTestUtils.createOpeningHours(2L, place3, DayOfWeek.MON)));
        placeRepository.save(PlaceTestUtils.createPlace(4L, "4", "연돈", "33.258895288625645", "126.40715814631936", null));   // 제주
        Place place5 = placeRepository.save(PlaceTestUtils.createPlace(5L, "5", "본수원갈비", "37.27796181430103", "127.04060364807957", null));  // 수원
        place5.getOpeningHoursList().add(openingHoursRepository.save(PlaceTestUtils.createOpeningHours(3L, place5, DayOfWeek.WED)));

        // when
        Pageable pageable = Pageable.ofSize(30);
        Slice<PlaceDto> placesLimit50 = placeRepository.findDtosNearBy(memberId, null, null, pos.getLat(), pos.getLng(), 50, pageable);
        Slice<PlaceDto> placesLimit1100 = placeRepository.findDtosNearBy(memberId, null, null, pos.getLat(), pos.getLng(), 1100, pageable);
        Slice<PlaceDto> placesLimit1100DaysMon = placeRepository.findDtosNearBy(memberId, List.of(DayOfWeek.MON), null, pos.getLat(), pos.getLng(), 1100, pageable);
        Slice<PlaceDto> placesLimit1100DaysWed = placeRepository.findDtosNearBy(memberId, List.of(DayOfWeek.WED), null, pos.getLat(), pos.getLng(), 1100, pageable);
        Slice<PlaceDto> placesLimit1100DaysMonAndWed = placeRepository.findDtosNearBy(memberId, List.of(DayOfWeek.MON, DayOfWeek.WED), null, pos.getLat(), pos.getLng(), 1100, pageable);

        // then
        assertThat(placesLimit50.getNumberOfElements()).isEqualTo(2);
        assertThat(placesLimit1100.getNumberOfElements()).isEqualTo(5);
        assertThat(placesLimit1100DaysMon.getNumberOfElements()).isEqualTo(2);
        assertThat(placesLimit1100DaysWed.getNumberOfElements()).isEqualTo(1);
        assertThat(placesLimit1100DaysMonAndWed.getNumberOfElements()).isEqualTo(3);
    }

    private double calculateDiff(String centerLng, String placeLng) {
        return Math.abs(Double.parseDouble(centerLng) - Double.parseDouble(placeLng));
    }
}