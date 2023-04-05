package com.zelusik.eatery.app.repository.member;

import com.zelusik.eatery.app.config.QuerydslConfig;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.member.ProfileImage;
import com.zelusik.eatery.util.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Repository] ProfileImage")
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class ProfileImageRepositoryTest {

    private final ProfileImageRepository profileImageRepository;
    private final MemberRepository memberRepository;

    public ProfileImageRepositoryTest(
            @Autowired ProfileImageRepository profileImageRepository,
            @Autowired MemberRepository memberRepository
    ) {
        this.profileImageRepository = profileImageRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("프로필 이미지를 갖는 회원이 존재하고, 프로필 이미지의 soft delete를 진행하면, 프로필 이미지의 deleted at을 갱신한다.")
    @Test
    void givenMemberWithProfileImage_whenSoftDeleteProfileImage_thenUpdateDeletedAtOfProfileImage() {
        // given
        Member member = memberRepository.save(MemberTestUtils.createNotSavedMember());
        ProfileImage profileImage = profileImageRepository.save(MemberTestUtils.createNotSavedProfileImage(member));

        // when
        profileImageRepository.softDelete(profileImage);

        // then
        ProfileImage updatedProfileImage = profileImageRepository.findById(profileImage.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(updatedProfileImage.getDeletedAt()).isNotNull();
    }
}