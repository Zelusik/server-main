package com.zelusik.eatery.app.repository.place;

import com.zelusik.eatery.app.constant.place.DayOfWeek;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.place.Point;
import com.zelusik.eatery.app.repository.OpeningHoursRepository;
import com.zelusik.eatery.app.repository.PlaceRepository;
import com.zelusik.eatery.util.PlaceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 대한민국 북-남 거리 약 1,100km
 */

@DisplayName("[Repository] Place")
@ActiveProfiles("test")
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
        String centerLat = "37";
        String centerLng = "127";
        for (int i = 0; i < 50; i++) {
            placeRepository.save(PlaceTestUtils.createPlace(
                    (long) (i + 1),
                    null,
                    centerLat,
                    String.valueOf(Integer.parseInt(centerLng) + Math.random()),
                    null
            ));
        }

        // when
        Slice<Place> places = placeRepository.findNearBy(List.of(), null, centerLat, centerLng, 1100, PageRequest.of(0, 30));

        // then
        assertThat(places.getSize()).isEqualTo(30);
        assertThat(places.hasNext()).isTrue();
        for (int i = 0; i < places.getSize() - 1; i++) {
            Place curPlace = places.getContent().get(i);
            Place nextPlace = places.getContent().get(i + 1);
            assertThat(calculateDiff(centerLng, curPlace.getPoint().getLng()))
                    .isLessThanOrEqualTo(calculateDiff(centerLng, nextPlace.getPoint().getLng()));
        }
    }

    @DisplayName("주변 장소를 조회하면, 50km와 1100km 내에 있는 가게들을 조회한다.")
    @Test
    void givenPlaces_whenFindNearBy_thenReturnPlacesWith50kmAnd1100km() {
        // given
        Point pos = new Point("37.5776087830657", "126.976896737645");  // 경복궁
        Place place1 = placeRepository.save(PlaceTestUtils.createPlace(1L, "성심당", "36.32765802936324", "127.42727719121109", null));    // 대전
        place1.getOpeningHoursList().add(openingHoursRepository.save(PlaceTestUtils.createOpeningHours(1L, place1, DayOfWeek.MON)));
        placeRepository.save(PlaceTestUtils.createPlace(2L, "해운대암소갈비집", "35.163310169485634", "129.1666092786243", null));  // 부산
        Place place3 = placeRepository.save(PlaceTestUtils.createPlace(3L, "연남토마 본점", "37.5595073462493", "126.921462488105", null));   // 서울
        place3.getOpeningHoursList().add(openingHoursRepository.save(PlaceTestUtils.createOpeningHours(2L, place3, DayOfWeek.MON)));
        placeRepository.save(PlaceTestUtils.createPlace(4L, "연돈", "33.258895288625645", "126.40715814631936", null));   // 제주
        Place place5 = placeRepository.save(PlaceTestUtils.createPlace(5L, "본수원갈비", "37.27796181430103", "127.04060364807957", null));  // 수원
        place5.getOpeningHoursList().add(openingHoursRepository.save(PlaceTestUtils.createOpeningHours(3L, place5, DayOfWeek.WED)));

        // when
        Pageable pageable = Pageable.ofSize(30);
        Slice<Place> placesLimit50 = placeRepository.findNearBy(null, null, pos.getLat(), pos.getLng(), 50, pageable);
        Slice<Place> placesLimit1100 = placeRepository.findNearBy(null, null, pos.getLat(), pos.getLng(), 1100, pageable);
        Slice<Place> placesLimit1100DaysMon = placeRepository.findNearBy(List.of(DayOfWeek.MON), null, pos.getLat(), pos.getLng(), 1100, pageable);
        Slice<Place> placesLimit1100DaysWed = placeRepository.findNearBy(List.of(DayOfWeek.WED), null, pos.getLat(), pos.getLng(), 1100, pageable);
        Slice<Place> placesLimit1100DaysMonAndWed = placeRepository.findNearBy(List.of(DayOfWeek.MON, DayOfWeek.WED), null, pos.getLat(), pos.getLng(), 1100, pageable);

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