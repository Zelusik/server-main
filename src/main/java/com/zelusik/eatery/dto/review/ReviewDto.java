package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.constant.review.ReviewEmbedOption;
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
import lombok.NoArgsConstructor;

import java.util.List;

import static com.zelusik.eatery.constant.review.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.constant.review.ReviewEmbedOption.WRITER;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static ReviewDto from(Review entity, Boolean isMarkedPlace) {
        return from(entity, List.of(WRITER, PLACE), isMarkedPlace);
    }

    // 장소를 포함하지 않는 경우
    public static ReviewDto from(Review entity, List<ReviewEmbedOption> embed) {
        return from(entity, embed, null);
    }

    // 장소를 포함하는 경우
    public static ReviewDto from(Review entity, List<ReviewEmbedOption> embed, Boolean isMarkedPlace) {
        return new ReviewDto(
                entity.getId(),
                embed != null && embed.contains(WRITER) ? MemberDto.from(entity.getWriter()) : null,
                embed != null && embed.contains(PLACE) ? PlaceDto.from(entity.getPlace(), isMarkedPlace) : null,
                entity.getKeywords().stream().map(ReviewKeyword::getKeyword).toList(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewImages().stream().map(ReviewImageDto::from).toList()
        );
    }

    public Review toEntity(Member writer, Place place) {
        return Review.of(
                writer,
                place,
                this.getAutoCreatedContent(),
                this.getContent()
        );
    }
}
