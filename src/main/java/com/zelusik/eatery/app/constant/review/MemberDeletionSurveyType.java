package com.zelusik.eatery.app.constant.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "<p>회원 탈퇴 설문 응답. 목록은 다음과 같다." +
        "<ul>" +
        "<li>HARD_TO_WRITE - 스토리/탭탭리뷰를 작성하기가 어렵고 불편함</li>" +
        "<li>FEED_UNUSEFUL - 피드의 추천이 유용하지 않음</li>" +
        "<li>REVIEW_NOT_ENOUGH - 리뷰가 많지 않아 도움되지 않음</li>" +
        "<li>NOT_MEET_EXPECTATION - 다운로드 시 기대한 내용과 앱이 다름</li>" +
        "<li>NOT_TRUST - 서비스 운영의 신뢰도가 낮음</li>" +
        "<li>ETC - 기타</li>" +
        "</ul>")
@AllArgsConstructor
@Getter
public enum MemberDeletionSurveyType {

    HARD_TO_WRITE("스토리/탭탭리뷰를 작성하기가 어렵고 불편함"),
    FEED_UNUSEFUL("피드의 추천이 유용하지 않음"),
    REVIEW_NOT_ENOUGH("리뷰가 많지 않아 도움되지 않음"),
    NOT_MEET_EXPECTATION("다운로드 시 기대한 내용과 앱이 다름"),
    NOT_TRUST("서비스 운영의 신뢰도가 낮음"),
    ETC("기타");

    private final String description;
}
