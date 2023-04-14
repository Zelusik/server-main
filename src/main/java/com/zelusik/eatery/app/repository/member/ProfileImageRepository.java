package com.zelusik.eatery.app.repository.member;

import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.member.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    Optional<ProfileImage> findByMember(Member member);
}
