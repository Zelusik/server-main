package com.zelusik.eatery.repository.terms_info;

import com.zelusik.eatery.domain.terms_info.TermsInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermsInfoRepository extends JpaRepository<TermsInfo, Long> {

    void deleteByMember_Id(long memberId);
}
