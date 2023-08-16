package com.zelusik.eatery.repository.member;

import com.zelusik.eatery.domain.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberRepositoryQCustom {

    Slice<Member> searchByKeyword(String searchKeyword, Pageable pageable);
}
