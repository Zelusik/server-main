package com.zelusik.eatery.domain.member.repository;

import com.zelusik.eatery.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryQCustom {

    boolean existsByNickname(String nickname);

    Optional<Member> findByIdAndDeletedAtNull(Long memberId);

    Optional<Member> findBySocialUid(String socialUid);
}
