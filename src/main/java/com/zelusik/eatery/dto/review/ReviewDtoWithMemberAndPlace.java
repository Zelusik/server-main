package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewDtoWithMemberAndPlace {

    private Long id;
    private MemberDto writerDto;
    private PlaceDtoWithMarkedStatus placeDtoWithMarkedStatus;
    private List<ReviewKeywordValue> keywords;
    private String autoCreatedContent;
    private String content;
    private List<ReviewImageDto> reviewImageDtos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewDtoWithMemberAndPlace of(PlaceDtoWithMarkedStatus placeDtoWithMarkedStatus, List<ReviewKeywordValue> keywords, String autoCreatedContent, String content) {
        return of(null, null, placeDtoWithMarkedStatus, keywords, autoCreatedContent, content, null, null, null, null);
    }

    public static ReviewDtoWithMemberAndPlace of(Long id, MemberDto writerDto, PlaceDtoWithMarkedStatus place, List<ReviewKeywordValue> keywords, String autoCreatedContent, String content, List<ReviewImageDto> reviewImageDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewDtoWithMemberAndPlace(id, writerDto, place, keywords, autoCreatedContent, content, reviewImageDtos, createdAt, updatedAt, deletedAt);
    }

    public static ReviewDtoWithMemberAndPlace from(Review entity, List<Long> markedPlaceIdList) {
        return of(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
                PlaceDtoWithMarkedStatus.from(entity.getPlace(), markedPlaceIdList),
                entity.getKeywords().stream()
                        .map(ReviewKeyword::getKeyword)
                        .toList(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewImages().stream()
                        .map(ReviewImageDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
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
