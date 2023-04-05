package com.zelusik.eatery.app.domain.review;

import com.zelusik.eatery.app.domain.BaseTimeEntity;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.place.Place;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @JoinColumn(name = "writer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @JoinColumn(name = "place_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @Column(length = 400)
    private String autoCreatedContent;

    @Setter(AccessLevel.PRIVATE)
    @Column(length = 400)
    private String content;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "review")
    private List<ReviewKeyword> keywords = new LinkedList<>();

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "review")
    private List<ReviewImage> reviewImages = new LinkedList<>();

    private LocalDateTime deletedAt;

    public static Review of(Member writer, Place place, String autoCreatedContent, String content) {
        return of(null, writer, place, autoCreatedContent, content, null, null, null);
    }

    public static Review of(Long id, Member writer, Place place, String autoCreatedContent, String content, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return Review.builder()
                .id(id)
                .writer(writer)
                .place(place)
                .autoCreatedContent(autoCreatedContent)
                .content(content)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Review(Long id, Member writer, Place place, String autoCreatedContent, String content, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.writer = writer;
        this.place = place;
        this.autoCreatedContent = autoCreatedContent;
        this.content = content;
        this.deletedAt = deletedAt;
    }

    public void update(String content) {
        this.setContent(content);
    }
}
