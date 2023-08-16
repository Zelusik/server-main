package com.zelusik.eatery.integration.repository.member;

import com.zelusik.eatery.config.JpaConfig;
import com.zelusik.eatery.config.QuerydslConfig;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import static com.zelusik.eatery.util.MemberTestUtils.createNotSavedMember;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Integration] Member Repository")
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JpaConfig.class})
@DataJpaTest
class MemberRepositoryTest {

    private final MemberRepository sut;

    @Autowired
    public MemberRepositoryTest(MemberRepository sut) {
        this.sut = sut;
    }

    @DisplayName("주어진 검색 키워드로 회원을 검색한다.")
    @Test
    void givenSearchKeyword_whenSearchMembersByKeyword_thenReturnSearchedMembers() {
        // given
        Member member1 = sut.save(createNotSavedMember("1", "7 옥타브 고양이"));
        Member member2 = sut.save(createNotSavedMember("2", "하얀 강아지"));
        Member member3 = sut.save(createNotSavedMember("3", "까만 고양이"));
        Member member4 = sut.save(createNotSavedMember("4", "개냥이"));
        Member member5 = sut.save(createNotSavedMember("5", "반려묘"));
        String searchKeyword = "고양이";

        // when
        Slice<Member> result = sut.searchByKeyword(searchKeyword, Pageable.ofSize(30));

        // then
        assertThat(result.getNumberOfElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(member1.getId());
        assertThat(result.getContent().get(1).getId()).isEqualTo(member3.getId());
    }
}