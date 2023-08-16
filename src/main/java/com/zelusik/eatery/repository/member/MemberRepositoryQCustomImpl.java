package com.zelusik.eatery.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.zelusik.eatery.domain.member.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryQCustomImpl implements MemberRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Member> searchByKeyword(String searchKeyword, Pageable pageable) {
        List<Member> content = queryFactory.selectFrom(member)
                .where(member.nickname.containsIgnoreCase(searchKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
