package com.zelusik.eatery.domain.member.repository;

import com.zelusik.eatery.domain.member.dto.MemberWithProfileInfoDto;
import com.zelusik.eatery.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberRepositoryQCustom {

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
    MemberWithProfileInfoDto getMemberProfileInfoById(long memberId);

    /**
     * 주어진 키워드에 해당하는 닉네임을 갖는 회원을 검색한다..
     *
     * @param searchKeyword 검색 키워드 (닉네임과 매칭)
     * @param pageable      paging 정보
     * @return 검색된 회원들
     */
    Slice<Member> searchByKeyword(String searchKeyword, Pageable pageable);

    /**
     * 주어진 닉네임을 갖는 회원이 존재하는지 조회한다.
     *
     * @param nickname 회원 닉네임
     * @return 주어진 닉네임을 갖는 회원이 존재한다면 true, 존재하지 않는다면 false
     */
    boolean existsByNickname(String nickname);
}
