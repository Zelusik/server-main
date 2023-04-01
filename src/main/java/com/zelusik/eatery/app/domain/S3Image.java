package com.zelusik.eatery.app.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@MappedSuperclass
public abstract class S3Image extends S3File {

    @Column(nullable = false)
    protected String thumbnailStoredName;

    @Column(nullable = false)
    protected String thumbnailUrl;

    protected S3Image(String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(originalName, storedName, url, createdAt, updatedAt);
        this.thumbnailStoredName = thumbnailStoredName;
        this.thumbnailUrl = thumbnailUrl;
    }
}
