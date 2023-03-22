package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdAndDeletedAtNull(Long memberId);

    Optional<Member> findBySocialUid(String socialUid);
}
