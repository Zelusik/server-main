package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.TermsInfo;
import com.zelusik.eatery.app.domain.member.ProfileImage;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.dto.member.request.MemberUpdateRequest;
import com.zelusik.eatery.app.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.app.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.app.repository.MemberRepository;
import com.zelusik.eatery.app.repository.TermsInfoRepository;
import com.zelusik.eatery.global.exception.member.MemberIdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final ProfileImageService profileImageService;
    private final MemberRepository memberRepository;
    private final TermsInfoRepository termsInfoRepository;

    /**
     * 회원 정보를 전달받아 회원가입을 진행한다.
     *
     * @param memberDto 등록할 회원정보
     * @return 등록된 회원 dto
     */
    @Transactional
    public MemberDto save(MemberDto memberDto) {
        return MemberDto.from(memberRepository.save(memberDto.toEntity()));
    }

    /**
     * 전체 약관에 대한 동의 정보를 받아 약관 동의를 진행한다.
     *
     * @param memberId 로그인 회원 id(PK)
     * @param request  약관 동의 정보
     * @return 적용된 약관 동의 결과 정보
     */
    @Transactional
    public TermsInfoDto agreeToTerms(Long memberId, TermsAgreeRequest request) {
        LocalDateTime now = LocalDateTime.now();
        TermsInfo termsInfo = TermsInfo.of(
                request.getIsNotMinor(),
                request.getService(), now,
                request.getUserInfo(), now,
                request.getLocationInfo(), now,
                request.getMarketingReception(), now
        );
        termsInfoRepository.save(termsInfo);

        Member member = findEntityById(memberId);
        member.setTermsInfo(termsInfo);

        return TermsInfoDto.from(termsInfo);
    }

    /**
     * 주어진 PK에 해당하는 회원 entity를 DB에서 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 entity
     * @throws MemberIdNotFoundException 일치하는 회원이 없는 경우
     */
    public Member findEntityById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberIdNotFoundException(memberId));
    }

    /**
     * 주어진 PK에 해당하는 회원을 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 dto
     */
    public MemberDto findDtoById(Long memberId) {
        return MemberDto.from(findEntityById(memberId));
    }

    /**
     * 주어진 socialUid에 해당하는 회원을 조회한 후 <code>Optional</code> 객체를 그대로 반환한다.
     *
     * @param socialUid 조회할 회원의 socialUid
     * @return 조회한 회원 dto. <code>Optional</code> 그대로 반환한다.
     */
    public Optional<MemberDto> findOptionalDtoBySocialUid(String socialUid) {
        return memberRepository.findBySocialUid(socialUid).map(MemberDto::from);
    }

    @Transactional
    public MemberDto updateMember(Long memberId, MemberUpdateRequest updateRequest) {
        Member member = findEntityById(memberId);

        MultipartFile profileImageFile = updateRequest.getProfileImage();
        if (profileImageFile == null) {
            member.update(
                    updateRequest.getNickname(),
                    updateRequest.getBirthDay(),
                    updateRequest.getGender()
            );
        } else {
            ProfileImage profileImage = profileImageService.upload(member, profileImageFile);
            member.update(
                    profileImage.getUrl(),
                    profileImage.getUrl(),
                    updateRequest.getNickname(),
                    updateRequest.getBirthDay(),
                    updateRequest.getGender()
            );
        }

        return MemberDto.from(member);
    }

    /**
     * 좋아하는 음식 취향을 업데이트한다.
     *
     * @param memberId               회원 id(PK)
     * @param favoriteFoodCategories 변경하고자 하는 음식 취향 목록
     */
    @Transactional
    public MemberDto updateFavoriteFoodCategories(Long memberId, List<FoodCategory> favoriteFoodCategories) {
        Member member = findEntityById(memberId);
        member.setFavoriteFoodCategories(favoriteFoodCategories);
        return MemberDto.from(member);
    }
}
