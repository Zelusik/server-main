package com.zelusik.eatery.domain.terms_info.service;

import com.zelusik.eatery.domain.terms_info.dto.TermsInfoDto;
import com.zelusik.eatery.domain.terms_info.repository.TermsInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TermsInfoQueryService {

    private final TermsInfoRepository termsInfoRepository;

    /**
     * <code>memberId</code>에 해당하는 회원의 약관 동의 정보(termsInfo)를 조회한다.
     *
     * @param memberId 약관 동의 정보를 조회하고자 하는 회원의 id(PK)
     * @return 조회된 약관 동의 정보(termsInfo entity)가 담긴 optional dto
     */
    public Optional<TermsInfoDto> findOptionalDtoByMemberId(long memberId) {
        return termsInfoRepository.findByMember_Id(memberId).map(TermsInfoDto::from);
    }
}
