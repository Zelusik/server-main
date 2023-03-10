package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.repository.MemberRepository;
import com.zelusik.eatery.global.exception.member.MemberIdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 정보를 전달받아 회원가입을 진행한다.
     *
     * @param memberDto 등록할 회원정보
     * @return 등록된 회원 dto
     */
    @Transactional
    public MemberDto save(MemberDto memberDto) {
        return MemberDto.from(memberRepository.save(memberDto.toEntity()));
    }

    /**
     * 주어진 PK에 해당하는 회원 entity를 DB에서 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 entity
     * @throws MemberIdNotFoundException 일치하는 회원이 없는 경우
     */
    public Member findEntityById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberIdNotFoundException(memberId));
    }

    /**
     * 주어진 PK에 해당하는 회원을 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 dto
     */
    public MemberDto findDtoById(Long memberId) {
        return MemberDto.from(findEntityById(memberId));
    }

    /**
     * 주어진 socialUid에 해당하는 회원을 조회한 후 <code>Optional</code> 객체를 그대로 반환한다.
     *
     * @param socialUid 조회할 회원의 socialUid
     * @return 조회한 회원 dto. <code>Optional</code> 그대로 반환한다.
     */
    public Optional<MemberDto> findOptionalDtoBySocialUid(String socialUid) {
        return memberRepository.findBySocialUid(socialUid).map(MemberDto::from);
    }
}
