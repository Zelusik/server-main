package com.zelusik.eatery.global.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud.aws")
public record AWSProperties(
        S3 s3,
        CloudFront cloudFront
) {

    public record S3(String bucketName) {
    }

    public record CloudFront(String domainName) {
    }
}
