package com.zelusik.eatery.global.open_ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChatCompletionApiRequest {

    private String model;

    private List<ChatCompletionApiMessageDto> messages;

    @JsonProperty("max_tokens")
    private int maxTokens;
}
