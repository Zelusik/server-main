package com.zelusik.eatery.domain.member.service;

import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.member.dto.MemberWithProfileInfoDto;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.exception.MemberNotFoundByIdException;
import com.zelusik.eatery.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberQueryService {

    private final MemberRepository memberRepository;

    /**
     * 주어진 PK에 해당하는 회원 entity를 DB에서 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 entity
     * @throws MemberNotFoundByIdException 일치하는 회원이 없는 경우
     */
    public Member getById(Long memberId) {
        return memberRepository.findByIdAndDeletedAtNull(memberId)
                .orElseThrow(() -> new MemberNotFoundByIdException(memberId));
    }

    /**
     * 주어진 PK에 해당하는 회원 entity를 DB에서 조회한다.
     * 삭제된 회원도 포함해서 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 entity
     * @throws MemberNotFoundByIdException 일치하는 회원이 없는 경우
     */
    public Member getByIdWithDeleted(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundByIdException(memberId));
    }

    /**
     * 주어진 PK에 해당하는 회원을 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 dto
     */
    @Cacheable(value = "member", key = "#memberId")
    public MemberDto getDtoById(Long memberId) {
        return MemberDto.from(getById(memberId));
    }

    /**
     * 주어진 socialUid에 해당하는 회원을 조회한 후 <code>Optional</code> 객체를 그대로 반환한다.
     * 삭제된 회원도 포함해서 조회한다.
     *
     * @param socialUid 조회할 회원의 socialUid
     * @return 조회한 회원 dto. <code>Optional</code> 그대로 반환한다.
     */
    public Optional<MemberDto> findDtoBySocialUidWithDeleted(String socialUid) {
        return memberRepository.findBySocialUid(socialUid).map(MemberDto::from);
    }

    /**
     * 키워드로 회원을 검색한다.
     *
     * @param searchKeyword 검색 키워드
     * @param pageable      paging 정보
     * @return 조회된 회원 목록
     */
    public Slice<MemberDto> searchDtosByKeyword(String searchKeyword, Pageable pageable) {
        return memberRepository.searchByKeyword(searchKeyword, pageable).map(MemberDto::from);
    }

    /**
     * <p>회원 프로필 정보를 조회한다.
     * <p>회원 프로필 정보란 다음 항목들을 의미합니다.
     * <ul>
     *     <li>회원 정보</li>
     *     <li>작성한 리뷰 수</li>
     *     <li>영향력</li>
     *     <li>팔로워 수</li>
     *     <li>팔로잉 수</li>
     *     <li>가장 많이 방문한 장소(읍면동)</li>
     *     <li>가장 많이 태그된 리뷰 키워드</li>
     *     <li>가장 많이 먹은 음식 카테고리</li>
     * </ul>
     *
     * @param memberId 프로필 정보를 조회할 회원의 PK
     * @return 조회된 프로필 정보
     */
    public MemberWithProfileInfoDto getMemberProfileInfoById(long memberId) {
        return memberRepository.getMemberProfileInfoById(memberId);
    }

    /**
     * 이미 존재하는 닉네임인지 확인한다.
     *
     * @param nickname 존재 여부를 확인할 닉네임
     * @return 닉네임 존재 여부
     */
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
}
