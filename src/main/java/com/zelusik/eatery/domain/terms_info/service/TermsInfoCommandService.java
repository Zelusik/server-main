package com.zelusik.eatery.domain.terms_info.service;

import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.terms_info.dto.TermsInfoDto;
import com.zelusik.eatery.domain.terms_info.dto.request.AgreeToTermsRequest;
import com.zelusik.eatery.domain.terms_info.entity.TermsInfo;
import com.zelusik.eatery.domain.terms_info.repository.TermsInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class TermsInfoCommandService {

    private final MemberQueryService memberQueryService;
    private final TermsInfoRepository termsInfoRepository;

    /**
     * 전체 약관에 대한 동의 정보를 받아 약관 동의를 진행한다.
     *
     * @param memberId 로그인 회원 id(PK)
     * @param request  약관 동의 정보
     * @return 적용된 약관 동의 결과 정보
     */
    @CacheEvict(value = "member", key = "#memberId")
    public TermsInfoDto saveTermsInfo(long memberId, AgreeToTermsRequest request) {
        Member member = memberQueryService.getById(memberId);
        LocalDateTime now = LocalDateTime.now();
        TermsInfo termsInfo = TermsInfo.of(
                member,
                request.getIsNotMinor(),
                request.getService(), now,
                request.getUserInfo(), now,
                request.getLocationInfo(), now,
                request.getMarketingReception(), now
        );
        termsInfoRepository.save(termsInfo);
        return TermsInfoDto.from(termsInfo);
    }

    /**
     * <code>memberId</code>에 해당하는 회원의 약관 동의 정보를 삭제한다.
     *
     * @param memberId 약관 동의 정보를 삭제하고자 하는 회원의 id(PK)
     */
    public void deleteByMemberId(long memberId) {
        termsInfoRepository.deleteByMember_Id(memberId);
    }
}
