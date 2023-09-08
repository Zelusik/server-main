package com.zelusik.eatery.service;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
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

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OpenAIService {

    private final RestTemplate restTemplate;

    @Value("${open_ai.api.key}")
    private String apiKey;

    public String getAutoCreatedReviewContent(
            @NonNull List<ReviewKeywordValue> placeKeywords,
            @Nullable List<String> menus,
            @Nullable List<List<String>> menuKeywords
    ) {
        URI requestUri = UriComponentsBuilder
                .fromUriString("https://api.openai.com/v1/chat/completions")
                .build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);

        List<String> placeKeywordDescriptions = placeKeywords.stream().map(ReviewKeywordValue::getContent).toList();
        StringBuilder messageBuilder = new StringBuilder();
        if (isNotEmpty(menus) && isNotEmpty(menuKeywords)) {
            for (int i = 0; i < menus.size(); i++) {
                String menu = menus.get(i);
                List<String> keywords = menuKeywords.get(i);
                messageBuilder
                        .append(String.join(", ", keywords))
                        .append(" '").append(menu).append("'");
                if (i != menus.size() - 1) {
                    messageBuilder.append("와 ");
                }
            }
            messageBuilder.append(" 메뉴가 있고 ");
        }
        messageBuilder
                .append(String.join(", ", placeKeywordDescriptions))
                .append("라는 특징이 있는 식당에 대한 후기를 작성해줘. ")
                .append("Summarize in ")
                .append(Review.MAX_LEN_OF_REVIEW_CONTENT - 100)
                .append(" character and Korean.");

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

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    private void validateResponseIsNotEmpty(ResponseEntity<?> response) {
        if (!response.hasBody()) {
            throw new OpenAIServerException();
        }
    }
}
