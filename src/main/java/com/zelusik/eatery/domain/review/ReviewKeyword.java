package com.zelusik.eatery.domain.review;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__review_keyword__keyword", columnList = "keyword")
})
@Entity
public class ReviewKeyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_keyword_id")
    private Long id;

    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Enumerated(EnumType.STRING)
    private ReviewKeywordValue keyword;

    public static ReviewKeyword of(Review review, ReviewKeywordValue keyword) {
        return of(null, review, keyword, null, null);
    }

    public static ReviewKeyword of(Long id, Review review, ReviewKeywordValue keyword, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return ReviewKeyword.builder()
                .id(id)
                .review(review)
                .keyword(keyword)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ReviewKeyword(Long id, Review review, ReviewKeywordValue keyword, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.review = review;
        this.keyword = keyword;
    }
}
