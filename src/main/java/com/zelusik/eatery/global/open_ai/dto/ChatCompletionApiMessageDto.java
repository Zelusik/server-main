package com.zelusik.eatery.global.open_ai.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChatCompletionApiMessageDto {

    private String role;
    private String content;

    public static ChatCompletionApiMessageDto from(Map<String, Object> attributes) {
        return new ChatCompletionApiMessageDto(
                String.valueOf(attributes.get("role")),
                String.valueOf(attributes.get("content"))
        );
    }
}