package com.zelusik.eatery.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.open_ai.ChatCompletionApiMessageDto;
import com.zelusik.eatery.dto.open_ai.ChatCompletionApiRequest;
import com.zelusik.eatery.service.OpenAIService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("[Unit] Open AI Service")
@Import(ObjectMapper.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@RestClientTest(OpenAIService.class)
class OpenAIServiceTest {

    private final OpenAIService sut;
    private final MockRestServiceServer restServer;
    private final ObjectMapper mapper;

    @Value("${open_ai.api.key}")
    private String apiKey;

    @Autowired
    public OpenAIServiceTest(OpenAIService openAIService, MockRestServiceServer restServer, ObjectMapper mapper) {
        this.sut = openAIService;
        this.restServer = restServer;
        this.mapper = mapper;
    }

    @DisplayName("리뷰를 작성하고자 하는 장소에 대한 키워드들과 메뉴 목록 및 각 메뉴에 대한 키워드들이 주어지고, 자동 생성된 리뷰 내용을 조회하면, 생성된 리뷰 내용을 반환한다.")
    @Test
    void givenPlaceKeywordsAndMenusAndMenuKeywords_whenGettingAutoCreatedReviewContent_thenReturnRespondedMessageContent() throws Exception {
        // given
        List<String> placeKeywords = List.of("신선한 재료", "넉넉한 양", "술과 함께", "데이트에 최고");
        Map<String, List<String>> menuKeywordMap = new LinkedHashMap<>();
        menuKeywordMap.put("시금치카츠카레", List.of("싱그러운", "육즙 가득한", "매콤한"));
        menuKeywordMap.put("버터치킨카레", List.of("부드러운", "촉촉한"));
        URI requestUri = UriComponentsBuilder
                .fromUriString("https://api.openai.com/v1/chat/completions")
                .encode(StandardCharsets.UTF_8)
                .build().toUri();
        String requestMessage = "다음 내용을 읽고 음식점에 대한 후기를 존댓말로 작성해줘. 음식점은(는) 신선한 재료, 넉넉한 양, 술과 함께, 데이트에 최고(이)라는 특징이 있었어. 메뉴 시금치카츠카레은(는) 싱그러운, 육즙 가득한, 매콤한(이)라는 특징이 있었어. 메뉴 버터치킨카레은(는) 부드러운, 촉촉한(이)라는 특징이 있었어. ";
        ChatCompletionApiRequest requestBody = new ChatCompletionApiRequest(
                "gpt-3.5-turbo",
                List.of(new ChatCompletionApiMessageDto("user", requestMessage)),
                Review.MAX_LEN_OF_REVIEW_CONTENT
        );
        String autoCreatedContent = "이 음식점은 신선한 재료와 넉넉한 양, 술과 함께 제공되어 데이트에 최고입니다. 또한 웃어른과 함께하기에도 좋은 곳입니다. 메뉴 시금치 카츠카레는 싱그러운 맛과 육즙 가득한 맛, 살짝 매콤한 맛이 있습니다. 또한 메뉴 버터 치킨 카레는 단짠 맛이 특징입니다.";
        String expectedResponse = "{" +
                "\"id\": \"chatcmpl-7jfArHKedWzJqsCe7TWaylblfh2zm\"," +
                "\"object\": \"chat.completion\"," +
                "\"created\": 1691117389," +
                "\"model\": \"gpt-3.5-turbo-0613\"," +
                "\"choices\": [" +
                "   {" +
                "       \"index\": 0," +
                "       \"message\": {" +
                "           \"role\": \"assistant\"," +
                "           \"content\": \"" + autoCreatedContent + "\"" +
                "       }," +
                "       \"finish_reason\": \"stop\"" +
                "   }" +
                "]," +
                "   \"usage\": {" +
                "       \"prompt_tokens\": 197," +
                "       \"completion_tokens\": 153," +
                "       \"total_tokens\": 350" +
                "   }" +
                "}";

        restServer.expect(requestTo(requestUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(requestBody)))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // when
        String result = sut.getAutoCreatedReviewContent(placeKeywords, menuKeywordMap);

        // then
        restServer.verify();
        assertThat(result).isEqualTo(autoCreatedContent);
    }
}
