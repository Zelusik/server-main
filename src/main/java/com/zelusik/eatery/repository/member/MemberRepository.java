package com.zelusik.eatery.repository.member;

import com.zelusik.eatery.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryQCustom {

    Optional<Member> findByIdAndDeletedAtNull(Long memberId);

    Optional<Member> findBySocialUid(String socialUid);
}
