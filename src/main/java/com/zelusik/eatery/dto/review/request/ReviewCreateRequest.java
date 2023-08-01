package com.zelusik.eatery.dto.review.request;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.review.ReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter // for @ModelAttribute
@Getter
public class ReviewCreateRequest {

    @Schema(description = "PK of place", example = "3")
    private Long placeId;

    @Schema(description = "키워드 목록", example = "[\"신선한 재료\", \"최고의 맛\"]")
    @NotEmpty
    private List<@NotBlank String> keywords;

    @Schema(description = "자동으로 생성된 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 씹을 때마다...")
    private String autoCreatedContent;

    @Schema(description = "업로드할 내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = """
            <p>업로드할 이미지 파일들</p>
                        
            <p>이미지 객체는 다음 두 개의 field로 구성됩니다.</p>
            <ul>
                <li><code>image</code>: 이미지 파일</li>
                <li><code>menuTags</code>: 메뉴 태그 목록 (array)</li>
            </ul>
                        
            <p>메뉴 태그 객체(menuTags)는 다음 두 개의 field로 구성됩니다.</p>
            <ul>
                <li><code>content</code>: 메뉴 태그 내용(메뉴 이름)</li>
                <li><code>point</code>: 메뉴 태그 좌표 정보</li>
            </ul>
                        
            <p>메뉴 태그 객체(point)는 다음 두 개의 field로 구성됩니다.</p>
            <ul>
                <li><code>x</code>: 메뉴 태그의 x좌표(double)</li>
                <li><code>y</code>: 메뉴 태그의 y좌표(double)</li>
            </ul>
                        
            <p>요청 데이터 예시를 JSON 형식으로 표현하면 다음과 같습니다.</p>
            <pre>
            {
                "image": "이미지 파일",
                "menuTags": [
                    {
                        "content": "치킨",
                        "point": {
                            "x": 10.25,
                            "y": 45.05
                        }
                    },
                    {
                        "content": "피자",
                        "point": {
                            "x": 10.25,
                            "y": 45.05
                        }
                    }
                ]
            }
            </pre>
            """)
    @NonNull
    private List<ReviewImageCreateRequest> images;

    public static ReviewCreateRequest of(long placeId, List<String> keywords, String autoCreatedContent, String content, List<ReviewImageCreateRequest> images) {
        return new ReviewCreateRequest(placeId, keywords, autoCreatedContent, content, images);
    }

    public ReviewDto toDto(PlaceDto placeDto) {
        return ReviewDto.of(
                placeDto,
                this.getKeywords().stream()
                        .map(ReviewKeywordValue::valueOfDescription)
                        .toList(),
                this.getAutoCreatedContent(),
                this.getContent()
        );
    }
}
