package com.zelusik.eatery.app.repository.member;

import com.zelusik.eatery.app.config.QuerydslConfig;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.util.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Repository] Member")
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class MemberRepositoryTest {

    private final MemberRepository memberRepository;

    public MemberRepositoryTest(@Autowired MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @DisplayName("회원이 존재하고, 회원의 soft delete를 진행하면, deleted at 정보가 갱신된다.")
    @Test
    void givenMember_whenSoftDeleteMember_thenUpdateDeleteAt() {
        // given
        Member member = memberRepository.save(MemberTestUtils.createNotSavedMember());

        // when
        memberRepository.softDelete(member);

        // then
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(updatedMember.getDeletedAt()).isNotNull();
    }
}