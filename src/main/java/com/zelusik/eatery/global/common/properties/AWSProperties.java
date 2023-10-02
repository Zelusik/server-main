package com.zelusik.eatery.global.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud.aws")
public record AWSProperties(CloudFront cloudFront) {

    public record CloudFront(String domainName) {
    }
}
