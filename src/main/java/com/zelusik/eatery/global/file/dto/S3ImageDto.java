package com.zelusik.eatery.global.file.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class S3ImageDto {

    private String originalName;
    private String storedName;
    private String url;
    private String thumbnailStoredName;
    private String thumbnailUrl;
}
