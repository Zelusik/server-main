package com.zelusik.eatery.domain.member;

import com.zelusik.eatery.constant.review.MemberDeletionSurveyType;
import com.zelusik.eatery.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MemberDeletionSurvey extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_deletion_survey_id")
    private Long id;

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private MemberDeletionSurveyType surveyType;

    public static MemberDeletionSurvey of(Member member, MemberDeletionSurveyType surveyType) {
        return MemberDeletionSurvey.builder()
                .member(member)
                .surveyType(surveyType)
                .build();
    }

    public static MemberDeletionSurvey of(Long id, Member member, MemberDeletionSurveyType surveyType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return MemberDeletionSurvey.builder()
                .id(id)
                .member(member)
                .surveyType(surveyType)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private MemberDeletionSurvey(Long id, Member member, MemberDeletionSurveyType surveyType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.member = member;
        this.surveyType = surveyType;
    }
}
