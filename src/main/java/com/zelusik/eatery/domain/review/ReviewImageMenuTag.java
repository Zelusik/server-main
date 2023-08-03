package com.zelusik.eatery.domain.review;

import com.zelusik.eatery.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ReviewImageMenuTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_menu_tag_id")
    private Long id;

    @JoinColumn(name = "review_image_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ReviewImage reviewImage;

    @Column(nullable = false)
    private String content;

    @Embedded
    private MenuTagPoint point;

    public static ReviewImageMenuTag of(@NonNull ReviewImage reviewImage, @NonNull String content, @NonNull MenuTagPoint point) {
        return of(null, reviewImage, content, point, null, null);
    }

    public static ReviewImageMenuTag of(
            @Nullable Long id,
            @NonNull ReviewImage reviewImage,
            @NonNull String content,
            @NonNull MenuTagPoint point,
            @Nullable LocalDateTime createdAt,
            @Nullable LocalDateTime updatedAt
    ) {
        return new ReviewImageMenuTag(id, reviewImage, content, point, createdAt, updatedAt);
    }

    private ReviewImageMenuTag(@Nullable Long id, @NonNull ReviewImage reviewImage, @NonNull String content, @NonNull MenuTagPoint point, @Nullable LocalDateTime createdAt, @Nullable LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.reviewImage = reviewImage;
        this.content = content;
        this.point = point;
    }
}
