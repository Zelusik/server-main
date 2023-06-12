package com.zelusik.eatery.service;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.review.MemberDeletionSurveyType;
import com.zelusik.eatery.domain.member.*;
import com.zelusik.eatery.dto.ImageDto;
import com.zelusik.eatery.dto.member.MemberDeletionSurveyDto;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.member.request.MemberUpdateRequest;
import com.zelusik.eatery.dto.member.request.TermsAgreeRequest;
import com.zelusik.eatery.dto.terms_info.TermsInfoDto;
import com.zelusik.eatery.exception.member.MemberIdNotFoundException;
import com.zelusik.eatery.exception.member.MemberNotFoundException;
import com.zelusik.eatery.repository.member.FavoriteFoodCategoryRepository;
import com.zelusik.eatery.repository.member.MemberDeletionSurveyRepository;
import com.zelusik.eatery.repository.member.MemberRepository;
import com.zelusik.eatery.repository.member.TermsInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MemberDeletionSurveyRepository memberDeletionSurveyRepository;
    private final FavoriteFoodCategoryRepository favoriteFoodCategoryRepository;

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

        Member member = findById(memberId);
        member.addTermsInfo(termsInfo);

        return TermsInfoDto.from(termsInfo);
    }

    /**
     * 주어진 PK에 해당하는 회원 entity를 DB에서 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 entity
     * @throws MemberIdNotFoundException 일치하는 회원이 없는 경우
     */
    public Member findById(Long memberId) {
        return memberRepository.findByIdAndDeletedAtNull(memberId)
                .orElseThrow(() -> new MemberIdNotFoundException(memberId));
    }

    /**
     * 주어진 PK에 해당하는 회원을 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 dto
     */
    @Cacheable(value = "member", key = "#memberId")
    public MemberDto findDtoById(Long memberId) {
        return MemberDto.from(findById(memberId));
    }

    /**
     * 주어진 socialUid에 해당하는 회원을 조회한 후 <code>Optional</code> 객체를 그대로 반환한다.
     * 삭제된 회원도 포함해서 조회한다.
     *
     * @param socialUid 조회할 회원의 socialUid
     * @return 조회한 회원 dto. <code>Optional</code> 그대로 반환한다.
     */
    public Optional<MemberDto> findOptionalDtoBySocialUidWithDeleted(String socialUid) {
        return memberRepository.findBySocialUid(socialUid).map(MemberDto::from);
    }

    /**
     * <p>재가입을 진행한다.
     * <p>재가입이란 회원의 <code>deletedAt</code> 속성을 <code>null</code>로 변경하는 것을 의미한다.
     *
     * @param memberId 재가입을 할 회원의 PK
     */
    @CachePut(value = "member", key = "#memberId")
    @Transactional
    public void rejoin(Long memberId) {
        Member member = findByIdWithDeleted(memberId);
        member.rejoin();
    }

    /**
     * 주어진 PK에 해당하는 회원 entity를 DB에서 조회한다.
     * 삭제된 회원도 포함해서 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 entity
     * @throws MemberIdNotFoundException 일치하는 회원이 없는 경우
     */
    private Member findByIdWithDeleted(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberIdNotFoundException(memberId));
    }

    /**
     * 회원 정보를 수정한다.
     *
     * @param memberId      정보를 수정할 회원의 PK
     * @param updateRequest 수정할 정보
     * @return 수정된 회원 dto
     */
    @CachePut(value = "member", key = "#memberId")
    @Transactional
    public MemberDto update(Long memberId, MemberUpdateRequest updateRequest) {
        Member member = findById(memberId);

        ImageDto imageDtoForUpdate = updateRequest.getProfileImage();
        if (imageDtoForUpdate == null) {
            member.update(
                    updateRequest.getNickname(),
                    updateRequest.getBirthDay(),
                    updateRequest.getGender()
            );
        } else {
            Optional<ProfileImage> oldProfileImage = profileImageService.findByMember(member);
            oldProfileImage.ifPresent(profileImageService::softDelete);

            ProfileImage profileImage = profileImageService.upload(member, imageDtoForUpdate);
            member.update(
                    profileImage.getUrl(),
                    profileImage.getThumbnailUrl(),
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
    @CachePut(value = "member", key = "#memberId")
    @Transactional
    public MemberDto updateFavoriteFoodCategories(Long memberId, List<FoodCategoryValue> favoriteFoodCategories) {
        Member member = findById(memberId);

        favoriteFoodCategoryRepository.deleteAll(member.getFavoriteFoodCategories());
        member.getFavoriteFoodCategories().clear();

        List<FavoriteFoodCategory> newFavoriteCategories = favoriteFoodCategories.stream()
                .map(foodCategory -> FavoriteFoodCategory.of(member, foodCategory))
                .toList();
        member.getFavoriteFoodCategories().addAll(newFavoriteCategories);
        favoriteFoodCategoryRepository.saveAll(newFavoriteCategories);

        return MemberDto.from(member);
    }

    /**
     * <p>회원을 삭제한다(soft delete).
     * <p>약관 동의 정보도 함께 삭제한다.
     * <p>회원 삭제 후, 탈퇴 사유가 담긴 설문 entity를 생성하고 반환한다.
     *
     * @param memberId   삭제할 회원의 PK
     * @param surveyType 탈퇴 사유
     * @return 탈퇴 사유 정보가 담긴 {@link MemberDeletionSurveyDto} 객체
     */
    @CacheEvict(value = "member", key = "#memberId")
    @Transactional
    public MemberDeletionSurveyDto delete(Long memberId, MemberDeletionSurveyType surveyType) {
        Member member = findById(memberId);

        if (member.getDeletedAt() != null) {
            throw new MemberNotFoundException();
        }

        TermsInfo memberTermsInfo = member.getTermsInfo();
        if (memberTermsInfo != null) {
            member.removeTermsInfo();
            termsInfoRepository.delete(memberTermsInfo);
        }

        softDelete(member);

        MemberDeletionSurvey deletionSurvey = MemberDeletionSurvey.of(member, surveyType);
        memberDeletionSurveyRepository.save(deletionSurvey);
        return MemberDeletionSurveyDto.from(deletionSurvey);
    }

    /**
     * <p>Member soft delete.
     * <p>Member의 deletedAt 값을 현재 시간으로 update한다.
     *
     * @param member
     */
    private void softDelete(Member member) {
        member.softDelete();
        memberRepository.flush();
    }
}
