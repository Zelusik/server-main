package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.terms_info.TermsInfo;
import com.zelusik.eatery.dto.member.request.AgreeToTermsRequest;
import com.zelusik.eatery.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.exception.member.MemberIdNotFoundException;
import com.zelusik.eatery.repository.member.MemberRepository;
import com.zelusik.eatery.repository.terms_info.TermsInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TermsInfoService {

    private final MemberRepository memberRepository;
    private final TermsInfoRepository termsInfoRepository;

    /**
     * 전체 약관에 대한 동의 정보를 받아 약관 동의를 진행한다.
     *
     * @param memberId 로그인 회원 id(PK)
     * @param request  약관 동의 정보
     * @return 적용된 약관 동의 결과 정보
     */
    @CacheEvict(value = "member", key = "#memberId")
    @Transactional
    public TermsInfoDto saveTermsInfo(long memberId, AgreeToTermsRequest request) {
        Member member = findMemberById(memberId);
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

    private Member findMemberById(long memberId) {
        return memberRepository.findByIdAndDeletedAtNull(memberId).orElseThrow(() -> new MemberIdNotFoundException(memberId));
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
