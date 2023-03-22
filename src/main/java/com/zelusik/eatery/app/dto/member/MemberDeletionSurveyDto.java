package com.zelusik.eatery.app.dto.member;

import com.zelusik.eatery.app.constant.review.MemberDeletionSurveyType;
import com.zelusik.eatery.app.domain.member.MemberDeletionSurvey;

public record MemberDeletionSurveyDto(
        Long id,
        Long memberId,
        MemberDeletionSurveyType surveyType
) {
    public static MemberDeletionSurveyDto of(Long id, Long memberId, MemberDeletionSurveyType surveyType) {
        return new MemberDeletionSurveyDto(id, memberId, surveyType);
    }

    public static MemberDeletionSurveyDto from(MemberDeletionSurvey entity) {
        return of(
                entity.getId(),
                entity.getId(),
                entity.getSurveyType()
        );
    }
}
