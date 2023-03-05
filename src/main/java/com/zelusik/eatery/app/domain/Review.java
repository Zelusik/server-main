package com.zelusik.eatery.app.domain;


import com.zelusik.eatery.app.domain.constant.ReviewKeyword;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.util.ReviewKeywordsConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE review SET deleted_at = CURRENT_TIMESTAMP WHERE review_id = ?")
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @JoinColumn(name = "uploader_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member uploader;

    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @Convert(converter = ReviewKeywordsConverter.class)
    private List<ReviewKeyword> keywords;

    @Column(length = 400, nullable = false)
    private String autoCreatedContent;

    @Column(length = 400, nullable = false)
    private String content;

    private LocalDateTime deletedAt;

    public static Review of(Member uploader, Place place, List<ReviewKeyword> keywords, String autoCreatedContent, String content) {
        return new Review(uploader, place, keywords, autoCreatedContent, content);
    }

    private Review(Member uploader, Place place, List<ReviewKeyword> keywords, String autoCreatedContent, String content) {
        this.uploader = uploader;
        this.place = place;
        this.keywords = keywords;
        this.autoCreatedContent = autoCreatedContent;
        this.content = content;
    }
}
