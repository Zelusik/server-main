package com.zelusik.eatery.app.repository.member;

import com.zelusik.eatery.app.domain.member.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProfileImage pi SET pi.deletedAt = CURRENT_TIMESTAMP WHERE pi = :profileImage")
    void softDelete(@Param("profileImage") ProfileImage profileImage);
}
