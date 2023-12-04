package com.zelusik.eatery.domain.terms_info.repository;

import com.zelusik.eatery.domain.terms_info.entity.TermsInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermsInfoRepository extends JpaRepository<TermsInfo, Long> {

    Optional<TermsInfo> findByMember_Id(long memberId);

    void deleteByMember_Id(long memberId);
}
