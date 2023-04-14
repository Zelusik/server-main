package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.member.MemberDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewDtoWithMember {

    private Long id;
    private MemberDto writerDto;
    private List<ReviewKeywordValue> keywords;
    private String autoCreatedContent;
    private String content;
    private List<ReviewImageDto> reviewImageDtos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewDtoWithMember of(List<ReviewKeywordValue> keywords, String autoCreatedContent, String content) {
        return of(null, null, keywords, autoCreatedContent, content, null, null, null, null);
    }

    public static ReviewDtoWithMember of(Long id, MemberDto writerDto, List<ReviewKeywordValue> keywords, String autoCreatedContent, String content, List<ReviewImageDto> reviewImageDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewDtoWithMember(id, writerDto, keywords, autoCreatedContent, content, reviewImageDtos, createdAt, updatedAt, deletedAt);
    }

    public static ReviewDtoWithMember from(Review entity) {
        return of(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
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
}
