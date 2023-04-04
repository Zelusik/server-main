package com.zelusik.eatery.app.repository.member;

import com.zelusik.eatery.app.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndDeletedAtNull(Long memberId);

    Optional<Member> findBySocialUid(String socialUid);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member m SET m.deletedAt = CURRENT_TIMESTAMP WHERE m = :member")
    void softDelete(@Param("member") Member member);
}
