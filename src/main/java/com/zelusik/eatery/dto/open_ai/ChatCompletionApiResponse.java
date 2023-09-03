package com.zelusik.eatery.dto.open_ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChatCompletionApiResponse {

    private String id;

    private String object;

    private Long created;

    private String model;

    private List<Choice> choices;

    private Usage usage;

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Choice {

        private Integer index;

        private ChatCompletionApiMessageDto message;

        @JsonProperty("finish_reason")
        private String finishReason;

        public static Choice from(Map<String, Object> attributes) {
            return new Choice(
                    attributes.get("index") != null ? Integer.parseInt(String.valueOf(attributes.get("index"))) : null,
                    ChatCompletionApiMessageDto.from((Map<String, Object>) attributes.get("message")),
                    String.valueOf(attributes.get("finish_reanson"))
            );
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Usage {

        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        @JsonProperty("total_tokens")
        private Integer totalTokens;

        public static Usage from(Map<String, Object> attributes) {
            return new Usage(
                    attributes.get("prompt_tokens") != null ? Integer.parseInt(attributes.get("prompt_tokens").toString()) : null,
                    attributes.get("completion_tokens") != null ? Integer.parseInt(attributes.get("completion_tokens").toString()) : null,
                    attributes.get("total_tokens") != null ? Integer.parseInt(attributes.get("total_tokens").toString()) : null
            );
        }
    }

    public static ChatCompletionApiResponse from(Map<String, Object> attributes) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) attributes.get("choices");
        return new ChatCompletionApiResponse(
                String.valueOf(attributes.get("id")),
                String.valueOf(attributes.get("object")),
                attributes.get("created") != null ? Long.parseLong(attributes.get("created").toString()) : null,
                String.valueOf(attributes.get("model")),
                List.of(Choice.from(choices.get(0))),
                Usage.from((Map<String, Object>) attributes.get("usage"))
        );
    }

    public String getMessageContent() {
        return getChoices().get(0).getMessage().getContent();
    }
}
