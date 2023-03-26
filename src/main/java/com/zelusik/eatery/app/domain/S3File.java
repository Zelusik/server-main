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
public abstract class S3File extends BaseTimeEntity {

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String url;

    private LocalDateTime deletedAt;

    protected S3File(String originalName, String storedName, String url, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(createdAt, updatedAt);
        this.originalName = originalName;
        this.storedName = storedName;
        this.url = url;
        this.deletedAt = deletedAt;
    }
}
