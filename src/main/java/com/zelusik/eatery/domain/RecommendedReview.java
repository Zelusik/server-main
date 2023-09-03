package com.zelusik.eatery.domain;

import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.review.Review;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = @UniqueConstraint(
        name = "unique__recommended_review__member_id__review_id__ranking",
        columnNames = {"member_id", "review_id", "ranking"}
))
@Entity
public class RecommendedReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommended_review_id")
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "review_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column(nullable = false)
    private Short ranking;

    public static RecommendedReview of(Member member, Review review, Short ranking) {
        return of(null, member, review, ranking, null, null);
    }

    public static RecommendedReview of(Long id, Member member, Review review, Short ranking, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new RecommendedReview(id, member, review, ranking, createdAt, updatedAt);
    }

    public RecommendedReview(Long id, Member member, Review review, Short ranking, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.member = member;
        this.review = review;
        this.ranking = ranking;
    }
}
