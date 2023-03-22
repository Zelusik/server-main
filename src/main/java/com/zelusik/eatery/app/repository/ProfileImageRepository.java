package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.domain.member.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
}
