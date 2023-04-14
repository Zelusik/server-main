package com.zelusik.eatery.repository.member;

import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.member.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    Optional<ProfileImage> findByMember(Member member);
}
