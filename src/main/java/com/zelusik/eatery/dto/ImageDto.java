package com.zelusik.eatery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class ImageDto {

    @Schema(description = "이미지")
    private MultipartFile image;

    @Schema(description = "리사이징된 썸네일 이미지")
    private MultipartFile thumbnailImage;

    public static ImageDto of(MultipartFile image, MultipartFile thumbnailImage) {
        return new ImageDto(image, thumbnailImage);
    }
}
