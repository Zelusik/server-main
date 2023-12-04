package com.zelusik.eatery.domain.review.dto;

import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusDto;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.review.constant.ReviewEmbedOption;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.domain.review_keyword.entity.ReviewKeyword;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.WRITER;

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
    private LocalDateTime createdAt;

    public static ReviewDto createNewReviewDto(PlaceDto place, List<ReviewKeywordValue> keywords, String autoCreatedContent, String content) {
        return new ReviewDto(null, null, place, keywords, autoCreatedContent, content, null, null);
    }

    public static ReviewDto from(Review entity, List<ReviewEmbedOption> embed) {
        return new ReviewDto(
                entity.getId(),
                embed != null && embed.contains(WRITER) ? MemberDto.from(entity.getWriter()) : null,
                embed != null && embed.contains(PLACE) ? PlaceDto.from(entity.getPlace()) : null,
                entity.getKeywords().stream().map(ReviewKeyword::getKeyword).toList(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewImages().stream().map(ReviewImageDto::from).toList(),
                entity.getCreatedAt()
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
