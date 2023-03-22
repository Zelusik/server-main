package com.zelusik.eatery.app.dto.review.request;

import com.zelusik.eatery.app.constant.review.MemberDeletionSurveyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class MemberDeletionSurveyRequest {

    @Schema(description = "회원 탈퇴 설문 응답", example = "HARD_TO_WRITE")
    @NotNull
    private MemberDeletionSurveyType surveyType;
}
