package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.constant.DayOfWeek;
import com.zelusik.eatery.app.domain.place.OpeningHours;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.place.OpeningHoursTimeDto;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.request.PlaceRequest;
import com.zelusik.eatery.app.repository.OpeningHoursRepository;
import com.zelusik.eatery.app.repository.PlaceRepository;
import com.zelusik.eatery.global.exception.place.PlaceNotFoundException;
import com.zelusik.eatery.global.exception.scraping.OpeningHoursUnexpectedFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final OpeningHoursRepository openingHoursRepository;

    /**
     * 장소 정보를 받아 장소를 저장한다.
     *
     * @param placeRequest 장소 정보가 담긴 dto.
     * @param homepageUrl  장소의 홈페이지 주소.
     * @param openingHours 장소의 영업시간.
     * @param closingHours 장소의 휴무일 정보.
     * @return 저장된 장소 entity.
     */
    @Transactional
    public Place create(
            PlaceRequest placeRequest,
            String homepageUrl,
            String openingHours,
            String closingHours
    ) {
        Place place = placeRequest
                .toDto(homepageUrl, closingHours)
                .toEntity();
        createOpeningHours(place, openingHours);
        return placeRepository.save(place);
    }

    public PlaceDto findDtoById(Long placeId) {
        return PlaceDto.from(placeRepository.findById(placeId)
                .orElseThrow(PlaceNotFoundException::new));
    }

    /**
     * kakaoPid에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param kakaoPid 조회하고자 하는 장소의 kakaoPid
     * @return 조회한 장소의 optional entity
     */
    public Optional<Place> findOptEntityByKakaoPid(String kakaoPid) {
        return placeRepository.findByKakaoPid(kakaoPid);
    }

    /**
     * 중심 좌표 기준, 가까운 순으로 장소 목록을 조회한다.
     *
     * @param lat      중심좌표의 위도
     * @param lng      중심좌표의 경도
     * @param pageable paging 정보
     * @return 조회한 장소 목록
     */
    public Slice<PlaceDto> findDtosNearBy(String lat, String lng, Pageable pageable) {
        Slice<Place> places = placeRepository.findNearBy(lat, lng, 3, pageable);
        if (!places.hasContent()) {
            places = placeRepository.findNearBy(lat, lng, 10, pageable);
        }
        return places.map(PlaceDto::from);
    }

    /**
     * 장소의 영업시간을 저장할 OpeningHours entity들을 생성/저장한다.
     *
     * @param place        OpeningHours와 연관된 장소 entity.
     * @param openingHours 영업시간 정보.
     */
    private void createOpeningHours(Place place, String openingHours) {
        if (openingHours == null) return;

        for (String oh : openingHours.split("\n")) {
            oh = oh.trim();

            if (oh.startsWith("매일")) {
                // 매일 11:30 ~ 22:00
                OpeningHoursTimeDto openingHoursTime = extractOpeningHoursTime(oh, 3);

                Arrays.stream(DayOfWeek.values())
                        .forEach(dayOfWeek -> place.getOpeningHoursList().add(OpeningHours.of(
                                place,
                                dayOfWeek,
                                openingHoursTime.openAt(),
                                openingHoursTime.closeAt()
                        )));
                openingHoursRepository.saveAll(place.getOpeningHoursList());
            } else if (oh.charAt(1) == '~') {
                // 월~토 18:00 ~ 02:00
                DayOfWeek startDay = DayOfWeek.valueOfDescription(oh.charAt(0));
                DayOfWeek endDay = DayOfWeek.valueOfDescription(oh.charAt(2));
                List<DayOfWeek> dayOfWeekList = DayOfWeek.getValuesInRange(startDay, endDay);

                OpeningHoursTimeDto openingHoursTime = extractOpeningHoursTime(oh, 4);

                dayOfWeekList.forEach(dayOfWeek -> place.getOpeningHoursList().add(OpeningHours.of(
                        place,
                        dayOfWeek,
                        openingHoursTime.openAt(),
                        openingHoursTime.closeAt()
                )));
                openingHoursRepository.saveAll(place.getOpeningHoursList());
            } else if (oh.charAt(1) == ',') {
                // 월,화,목,금,토,일 10:00 ~ 19:30
                int firstSpaceIdx = oh.indexOf(" ");

                List<DayOfWeek> dayOfWeekList = new ArrayList<>();
                for (int i = 0; i < firstSpaceIdx; i += 2) {
                    DayOfWeek dayOfWeek = DayOfWeek.valueOfDescription(oh.charAt(i));
                    dayOfWeekList.add(dayOfWeek);
                }

                OpeningHoursTimeDto openingHoursTime = extractOpeningHoursTime(oh, firstSpaceIdx + 1);

                dayOfWeekList.forEach(dayOfWeek -> place.getOpeningHoursList().add(OpeningHours.of(
                        place,
                        dayOfWeek,
                        openingHoursTime.openAt(),
                        openingHoursTime.closeAt()
                )));
                openingHoursRepository.saveAll(place.getOpeningHoursList());
            } else if (oh.charAt(1) == ' ') {
                // 목 11:00 ~ 18:00
                DayOfWeek dayOfWeek = DayOfWeek.valueOfDescription(oh.charAt(0));

                OpeningHoursTimeDto openingHoursTime = extractOpeningHoursTime(oh, 2);

                OpeningHours openingHoursEntity = OpeningHours.of(
                        place,
                        dayOfWeek,
                        openingHoursTime.openAt(),
                        openingHoursTime.closeAt()
                );
                openingHoursRepository.save(openingHoursEntity);
                place.getOpeningHoursList().add(openingHoursEntity);
            } else {
                throw new OpeningHoursUnexpectedFormatException();
            }
        }
    }

    /**
     * 영업시간 정보(String)를 받아 영업 시작 시간과 영업 종료 시간을 추출한다.
     *
     * @param openingHours   영업시간 정보.
     * @param openAtStartIdx 영업 시작 시간이 시작하는 index.
     * @return openingHours에서 추출한 영업 시작 시간, 종료 시간 정보가 담긴 객체.Q
     */
    private OpeningHoursTimeDto extractOpeningHoursTime(String openingHours, int openAtStartIdx) {
        int closeAtStartIdx = openAtStartIdx + 5 + 3;

        String subStrOpenAt = openingHours.substring(openAtStartIdx, openAtStartIdx + 5);
        String subStrCloseAt = openingHours.substring(closeAtStartIdx, closeAtStartIdx + 5);
        LocalTime openAt = subStrOpenAt.equals("24:00") ? LocalTime.of(23, 59) : LocalTime.parse(subStrOpenAt);
        LocalTime closeAt = subStrCloseAt.equals("24:00") ? LocalTime.of(23, 59) : LocalTime.parse(subStrCloseAt);

        return new OpeningHoursTimeDto(openAt, closeAt);
    }
}
