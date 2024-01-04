package com.zelusik.eatery.domain.member.dto.request;

import com.zelusik.eatery.domain.member.constant.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberUpdateRequest {

    @Schema(description = "닉네임. 최대 15자", example = "우기")
    @Length(max = 15)
    @NotBlank
    private String nickname;

    @Schema(description = "생년월일 (yyyy-MM-dd 형식)", example = "1998-01-05")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    @Schema(description = "성별", example = "MALE")
    @NotNull
    private Gender gender;

    @Schema(description = "변경하고자 하는 프로필 이미지")
    private MultipartFile profileImage;
}
