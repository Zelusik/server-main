package com.zelusik.eatery.app.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class S3File extends BaseTimeEntity {

    @Column(nullable = false)
    protected String originalName;

    @Column(nullable = false)
    protected String storedName;

    @Column(nullable = false)
    protected String url;

    protected LocalDateTime deletedAt;
}
