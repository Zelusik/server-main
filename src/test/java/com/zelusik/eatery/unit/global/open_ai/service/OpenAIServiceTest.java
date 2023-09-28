package com.zelusik.eatery.unit.global.open_ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.global.open_ai.dto.ChatCompletionApiMessageDto;
import com.zelusik.eatery.global.open_ai.dto.ChatCompletionApiRequest;
import com.zelusik.eatery.global.open_ai.service.OpenAIService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.zelusik.eatery.domain.review.constant.ReviewKeywordValue.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("[Unit] Service - Open AI ")
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
        List<ReviewKeywordValue> placeKeywords = List.of(FRESH, GENEROUS_PORTIONS, WITH_ALCOHOL, GOOD_FOR_DATE);
        List<String> menus = List.of("시금치카츠카레", "버터치킨카레");
        List<List<String>> menuKeywords = List.of(
                List.of("싱그러운", "육즙 가득한", "매콤한"),
                List.of("부드러운", "촉촉한")
        );
        URI requestUri = UriComponentsBuilder
                .fromUriString("https://api.openai.com/v1/chat/completions")
                .encode(StandardCharsets.UTF_8)
                .build().toUri();
        String requestMessage = "싱그러운, 육즙 가득한, 매콤한 '시금치카츠카레'와 부드러운, 촉촉한 '버터치킨카레' 메뉴가 있고 신선한 재료, 넉넉한 양, 술과 함께, 데이트에 최고라는 특징이 있는 식당에 대한 후기를 작성해줘. Summarize in 300 character and Korean.";
        ChatCompletionApiRequest requestBody = new ChatCompletionApiRequest(
                "gpt-3.5-turbo",
                List.of(new ChatCompletionApiMessageDto("user", requestMessage)),
                Review.MAX_LEN_OF_REVIEW_CONTENT
        );
        String autoCreatedContent = "가까운 일요일 저녁, 연인과 함께 식사를 위해 방문한 식당은 정말로 기억에 남는 경험이었습니다. 신선한 재료와 넉넉한 양이라는 특징이 고객들에게 정말로 잘 어울려서, 음식을 먹으며 실로 만족스러웠습니다. 특히 싱그러운, 육즙 가득한 시금치카츠카레와 부드러운, 촉촉한 버터치킨카레는 정말로 맛있었어요. 매콤함이 가미된 카레 소스는 입맛을 돋구어줘서 정말로 멋진 조합이었습니다. 두 음식 모두 훌륭한 퀄리티를 자랑했고, 서빙되는 동안 냉기를 잃지 않으며 뜨끈한 온도로 유지되어서 맛을 한층 더 끌어올렸습니다. 또한, 좋은 분위기와 함께 술을 즐기며 데이트하기에 최적의 장소였습니다. 아무래도 이 식당은 계속해서 방문하게 될 것 같아요.";
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
        String result = sut.getAutoCreatedReviewContent(placeKeywords, menus, menuKeywords);

        // then
        restServer.verify();
        assertThat(result).isEqualTo(autoCreatedContent);
    }
}
