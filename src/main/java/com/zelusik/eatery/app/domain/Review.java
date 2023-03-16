package com.zelusik.eatery.app.domain;


import com.zelusik.eatery.app.constant.review.ReviewKeyword;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.util.domain.ReviewKeywordsConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
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

    @JoinColumn(name = "writer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @Convert(converter = ReviewKeywordsConverter.class)
    private List<ReviewKeyword> keywords;

    @Column(length = 400)
    private String autoCreatedContent;

    @Column(length = 400)
    private String content;

    @OneToMany(mappedBy = "review")
    List<ReviewFile> reviewFiles = new LinkedList<>();

    private LocalDateTime deletedAt;

    public static Review of(Member writer, Place place, List<ReviewKeyword> keywords, String autoCreatedContent, String content) {
        return new Review(writer, place, keywords, autoCreatedContent, content);
    }

    private Review(Member writer, Place place, List<ReviewKeyword> keywords, String autoCreatedContent, String content) {
        this.writer = writer;
        this.place = place;
        this.keywords = keywords;
        this.autoCreatedContent = autoCreatedContent;
        this.content = content;
    }
}
