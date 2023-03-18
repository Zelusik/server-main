package com.zelusik.eatery.app.dto.review;

import com.zelusik.eatery.app.constant.review.ReviewKeyword;
import com.zelusik.eatery.app.domain.Review;
import com.zelusik.eatery.app.dto.member.MemberDto;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewDtoWithMember(
        Long id,
        MemberDto writerDto,
        List<ReviewKeyword> keywords,
        String autoCreatedContent,
        String content,
        List<ReviewFileDto> reviewFileDtos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static ReviewDtoWithMember of(List<ReviewKeyword> keywords, String autoCreatedContent, String content) {
        return of(null, null, keywords, autoCreatedContent, content, null, null, null, null);
    }

    public static ReviewDtoWithMember of(Long id, MemberDto writerDto, List<ReviewKeyword> keywords, String autoCreatedContent, String content, List<ReviewFileDto> reviewFileDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewDtoWithMember(id, writerDto, keywords, autoCreatedContent, content, reviewFileDtos, createdAt, updatedAt, deletedAt);
    }

    public static ReviewDtoWithMember from(Review entity) {
        return of(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
                entity.getKeywords(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewFiles().stream()
                        .map(ReviewFileDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}