package com.zelusik.eatery.app.dto.member.response;

import com.zelusik.eatery.app.dto.member.MemberDeletionSurveyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberDeletionSurveyResponse {

    @Schema(description = "회원 탈퇴 설문의 PK", example = "3")
    private Long id;

    @Schema(description = "회원의 PK", example = "1")
    private Long memberId;

    @Schema(description = "설문 내용", example = "스토리/탭탭리뷰를 작성하기가 어렵고 불편함")
    private String survey;

    public static MemberDeletionSurveyResponse of(Long id, Long memberId, String surveyType) {
        return new MemberDeletionSurveyResponse(id, memberId, surveyType);
    }

    public static MemberDeletionSurveyResponse from(MemberDeletionSurveyDto dto) {
        return of(dto.id(), dto.memberId(), dto.surveyType().getDescription());
    }
}
