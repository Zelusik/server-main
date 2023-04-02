package com.zelusik.eatery.app.dto.member.request;

import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.dto.ImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

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

    @Schema(description = "<p>변경하고자 하는 프로필 이미지" +
            "<p>이미지 요청 데이터는 다음 두 개의 field로 구성됩니다." +
            "<ul>" +
            "<li><code>image</code>: 이미지</li>" +
            "<li><code>thumbnailImage</code>: 리사이징된 썸네일 이미지</li>" +
            "</ul>" +
            "<p>요청 데이터 예시는 다음과 같습니다." +
            "<p><code>profileImage.image = 이미지1</code>" +
            "<p><code>profileImage.thumbnailImage = 이미지2</code>")
    private ImageDto profileImage;

    public static MemberUpdateRequest of(String nickname, LocalDate birthDay, Gender gender, ImageDto profileImage) {
        return new MemberUpdateRequest(nickname, birthDay, gender, profileImage);
    }
}
