package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.place.PlaceDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class ReviewDto {

    private Long id;
    private MemberDto writer;
    private PlaceDto place;
    private List<ReviewKeywordValue> keywords;
    private String autoCreatedContent;
    private String content;
    private List<ReviewImageDto> reviewImageDtos;

    public ReviewDto(PlaceDto place, List<ReviewKeywordValue> keywords, String autoCreatedContent, String content) {
        this(null, null, place, keywords, autoCreatedContent, content, null);
    }

    @NonNull
    public static ReviewDto from(@NonNull Review entity, Boolean isMarkedPlace) {
        return new ReviewDto(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
                PlaceDto.from(entity.getPlace(), isMarkedPlace),
                entity.getKeywords().stream()
                        .map(ReviewKeyword::getKeyword)
                        .toList(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewImages().stream()
                        .map(ReviewImageDto::from)
                        .toList()
        );
    }

    @NonNull
    public static ReviewDto fromWithoutPlace(@NonNull Review entity) {
        return new ReviewDto(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
                null,
                entity.getKeywords().stream()
                        .map(ReviewKeyword::getKeyword)
                        .toList(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewImages().stream()
                        .map(ReviewImageDto::from)
                        .toList()
        );
    }

    @NonNull
    public Review toEntity(Member writer, Place place) {
        return Review.of(
                writer,
                place,
                this.getAutoCreatedContent(),
                this.getContent()
        );
    }
}
