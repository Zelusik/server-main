package com.zelusik.eatery.app.dto.member.request;

import com.zelusik.eatery.app.constant.member.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class MemberUpdateRequest {

    @Schema(description = "닉네임", example = "우기")
    @Length(max = 15)
    private String nickname;

    @Schema(description = "생년월일", example = "1998-01-05")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    @Schema(description = "성별", example = "MALE")
    @NotNull
    private Gender gender;

    @Schema(description = "변경하고자 하는 프로필 이미지")
    private MultipartFile profileImage;

    public static MemberUpdateRequest of(String nickname, LocalDate birthDay, Gender gender, MultipartFile profileImage) {
        return new MemberUpdateRequest(nickname, birthDay, gender, profileImage);
    }
}
