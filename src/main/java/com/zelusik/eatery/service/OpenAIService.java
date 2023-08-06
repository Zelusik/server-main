package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.open_ai.ChatCompletionApiMessageDto;
import com.zelusik.eatery.dto.open_ai.ChatCompletionApiRequest;
import com.zelusik.eatery.dto.open_ai.ChatCompletionApiResponse;
import com.zelusik.eatery.exception.open_ai.OpenAIServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OpenAIService {

    private final RestTemplate restTemplate;

    @Value("${open_ai.api.key}")
    private String apiKey;

    public String getAutoCreatedReviewContent(
            @NonNull List<String> placeKeywords,
            @Nullable Map<String, List<String>> menusKeywordMap
    ) {
        URI requestUri = UriComponentsBuilder
                .fromUriString("https://api.openai.com/v1/chat/completions")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);

        StringBuilder messageBuilder = new StringBuilder("다음 내용을 읽고 음식점에 대한 후기를 존댓말로 작성해줘." +
                " 음식점은(는) " + String.join(", ", placeKeywords) + "(이)라는 특징이 있었어. ");
        if (!isMenuKeywordsEmpty(menusKeywordMap)) {
            menusKeywordMap.forEach((menu, keywords) ->
                    messageBuilder
                            .append("메뉴 ")
                            .append(menu)
                            .append("은(는) ")
                            .append(String.join(", ", keywords))
                            .append("(이)라는 특징이 있었어. "));
        }
        ChatCompletionApiRequest requestBody = new ChatCompletionApiRequest(
                "gpt-3.5-turbo",
                List.of(new ChatCompletionApiMessageDto("user", messageBuilder.toString())),
                Review.MAX_LEN_OF_REVIEW_CONTENT
        );

        HttpEntity<ChatCompletionApiRequest> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<ChatCompletionApiResponse> response;
        try {
            response = restTemplate.postForEntity(requestUri, requestEntity, ChatCompletionApiResponse.class);
        } catch (RestClientException ex) {
            throw new OpenAIServerException(ex);
        }
        validateResponseIsNotEmpty(response);
        return response.getBody().getMessageContent();
    }

    private boolean isMenuKeywordsEmpty(@Nullable Map<String, List<String>> menuKeywordMap) {
        return menuKeywordMap == null || menuKeywordMap.isEmpty();
    }

    private void validateResponseIsNotEmpty(ResponseEntity<?> response) {
        if (!response.hasBody()) {
            throw new OpenAIServerException();
        }
    }
}
