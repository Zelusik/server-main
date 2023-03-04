package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsBySocialUid(String socialUid);

    Optional<Member> findBySocialUid(String socialUid);
}
