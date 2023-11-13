package com.zelusik.eatery.domain.review_keyword.entity;

import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.global.common.entity.BaseTimeEntity;
import lombok.AccessLevel;
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

    public static ReviewKeyword createNewReviewKeyword(Review review, ReviewKeywordValue keyword) {
        return new ReviewKeyword(null, review, keyword, null, null);
    }

    public ReviewKeyword(Long id, Review review, ReviewKeywordValue keyword, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.review = review;
        this.keyword = keyword;
    }
}
