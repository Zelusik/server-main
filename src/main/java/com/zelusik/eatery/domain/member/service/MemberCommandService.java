package com.zelusik.eatery.domain.member.service;

import com.zelusik.eatery.domain.favorite_food_category.entity.FavoriteFoodCategory;
import com.zelusik.eatery.domain.favorite_food_category.repository.FavoriteFoodCategoryRepository;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.member.dto.request.MemberUpdateRequest;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.exception.MemberNotFoundByIdException;
import com.zelusik.eatery.domain.member.exception.MemberNotFoundException;
import com.zelusik.eatery.domain.member.repository.MemberRepository;
import com.zelusik.eatery.domain.member_deletion_survey.constant.MemberDeletionSurveyType;
import com.zelusik.eatery.domain.member_deletion_survey.dto.MemberDeletionSurveyDto;
import com.zelusik.eatery.domain.member_deletion_survey.entity.MemberDeletionSurvey;
import com.zelusik.eatery.domain.member_deletion_survey.repository.MemberDeletionSurveyRepository;
import com.zelusik.eatery.domain.profile_image.entity.ProfileImage;
import com.zelusik.eatery.domain.profile_image.service.ProfileImageCommandService;
import com.zelusik.eatery.domain.profile_image.service.ProfileImageQueryService;
import com.zelusik.eatery.domain.terms_info.service.TermsInfoCommandService;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberCommandService {

    private final MemberQueryService memberQueryService;
    private final ProfileImageCommandService profileImageCommandService;
    private final ProfileImageQueryService profileImageQueryService;
    private final TermsInfoCommandService termsInfoCommandService;
    private final MemberRepository memberRepository;
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
     * <p>재가입을 진행한다.
     * <p>재가입이란 회원의 <code>deletedAt</code> 속성을 <code>null</code>로 변경하는 것을 의미한다.
     *
     * @param memberId 재가입을 할 회원의 PK
     */
    @CacheEvict(value = "member", key = "#memberId")
    @Transactional
    public void rejoin(Long memberId) {
        Member member = findByIdWithDeleted(memberId);
        member.rejoin();
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
        Member member = memberQueryService.findById(memberId);

        MultipartFile profileImageForUpdate = updateRequest.getProfileImage();
        if (profileImageForUpdate == null) {
            member.update(
                    updateRequest.getNickname(),
                    updateRequest.getBirthDay(),
                    updateRequest.getGender()
            );
        } else {
            Optional<ProfileImage> oldProfileImage = profileImageQueryService.findOptionalByMember(member);
            oldProfileImage.ifPresent(profileImageCommandService::softDelete);

            ProfileImage profileImage = profileImageCommandService.upload(member, profileImageForUpdate);
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
        Member member = memberQueryService.findById(memberId);

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
        Member member = memberQueryService.findById(memberId);
        if (member.getDeletedAt() != null) {
            throw new MemberNotFoundException();
        }

        termsInfoCommandService.deleteByMemberId(memberId);

        member.softDelete();

        MemberDeletionSurvey deletionSurvey = MemberDeletionSurvey.of(member, surveyType);
        memberDeletionSurveyRepository.save(deletionSurvey);
        return MemberDeletionSurveyDto.from(deletionSurvey);
    }

    /**
     * 주어진 PK에 해당하는 회원 entity를 DB에서 조회한다.
     * 삭제된 회원도 포함해서 조회한다.
     *
     * @param memberId 조회할 회원의 PK
     * @return 조회한 회원 entity
     * @throws MemberNotFoundByIdException 일치하는 회원이 없는 경우
     */
    private Member findByIdWithDeleted(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundByIdException(memberId));
    }
}
