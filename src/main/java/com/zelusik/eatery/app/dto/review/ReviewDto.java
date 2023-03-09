package com.zelusik.eatery.app.dto.review;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.domain.Review;
import com.zelusik.eatery.app.domain.constant.ReviewKeyword;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.dto.place.PlaceDto;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewDto(
        Long id,
        MemberDto uploaderDto,
        PlaceDto placeDto,
        List<ReviewKeyword> keywords,
        String autoCreatedContent,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static ReviewDto of(PlaceDto placeDto, List<ReviewKeyword> keywords, String autoCreatedContent, String content) {
        return of(null, null, placeDto, keywords, autoCreatedContent, content, null, null, null);
    }

    public static ReviewDto of(Long id, MemberDto uploader, PlaceDto place, List<ReviewKeyword> keywords, String autoCreatedContent, String content, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewDto(id, uploader, place, keywords, autoCreatedContent, content, createdAt, updatedAt, deletedAt);
    }

    public static ReviewDto from(Review entity) {
        return of(
                entity.getId(),
                MemberDto.from(entity.getUploader()),
                PlaceDto.from(entity.getPlace()),
                entity.getKeywords(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public Review toEntity(Member uploader, Place place) {
        return Review.of(
                uploader,
                place,
                this.keywords(),
                this.autoCreatedContent(),
                this.content()
        );
    }
}
