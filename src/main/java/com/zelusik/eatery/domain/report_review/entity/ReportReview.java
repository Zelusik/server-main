package com.zelusik.eatery.domain.report_review.entity;

import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.report_review.dto.ReportReviewReasonOption;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.global.common.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table
@Entity
public class ReportReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_review_id")
    private Long id;

    @JoinColumn(name = "reporter_id")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporter;

    @JoinColumn(name = "review_id")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private ReportReviewReasonOption reasonOption;

    @Column
    @NotNull
    private String reasonDetail;

    public ReportReview(Long id, Member reporter, Review review, ReportReviewReasonOption reasonOption, String reasonDetail, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.reporter = reporter;
        this.review = review;
        this.reasonOption = reasonOption;
        this.reasonDetail = reasonDetail;
    }

    public static ReportReview create(Member reporter, Review review, ReportReviewReasonOption reasonOption, String reasonDetail) {
        return create(null, reporter, review, reasonOption, reasonDetail, null, null);
    }

    public static ReportReview create(Long id, Member reporter, Review review, ReportReviewReasonOption reasonOption, String reasonDetail, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new ReportReview(id, reporter, review, reasonOption, reasonDetail, createdAt, updatedAt);
    }
}
