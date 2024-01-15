package com.zelusik.eatery.domain.report_review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportReviewReasonOption {
    UNRELATED("해당 음식점과 관련 없는 내용임"),
    ADVERTISING("광고/홍보성 게시글임"),
    SENSATIONAL("선정적이거나 폭력, 혐오적임"),
    UNAUTHORIZED("무단 도용, 사칭, 저작권 침해가 의심됨"),
    PRIVACY("개인 정보 노출이 우려됨"),
    ETC("기타");

    private final String fullSentence;
}
