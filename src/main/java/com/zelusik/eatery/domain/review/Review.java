package com.zelusik.eatery.domain.review;

import com.zelusik.eatery.domain.BaseTimeEntity;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__review__writer", columnList = "writer_id"),
        @Index(name = "idx__review__place", columnList = "place_id"),
        @Index(name = "idx__review__created_at", columnList = "createdAt"),
        @Index(name = "idx__review__deleted_at", columnList = "deletedAt")
})
@Entity
public class Review extends BaseTimeEntity {

    public static final int MAX_LEN_OF_REVIEW_CONTENT = 400;

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

    @Column(length = MAX_LEN_OF_REVIEW_CONTENT)
    private String autoCreatedContent;

    @Setter(AccessLevel.PRIVATE)
    @Column(length = MAX_LEN_OF_REVIEW_CONTENT)
    private String content;

    @OneToMany(mappedBy = "review")
    private List<ReviewKeyword> keywords = new LinkedList<>();

    @OneToMany(mappedBy = "review")
    private List<ReviewImage> reviewImages = new LinkedList<>();

    @Setter(AccessLevel.PRIVATE)
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

    public void update(String content) {
        this.setContent(content);
    }

    public void softDelete() {
        this.setDeletedAt(LocalDateTime.now());
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
}
