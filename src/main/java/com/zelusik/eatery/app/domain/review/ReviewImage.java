package com.zelusik.eatery.app.domain.review;

import com.zelusik.eatery.app.domain.S3Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE review_image SET deleted_at = CURRENT_TIMESTAMP WHERE review_image_id = ?")
@Entity
public class ReviewImage extends S3Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    private LocalDateTime deletedAt;

    public static ReviewImage of(Review review, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl) {
        return of(null, review, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, null, null, null);
    }

    public static ReviewImage of(Long id, Review review, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return ReviewImage.builder()
                .id(id)
                .review(review)
                .originalName(originalName)
                .storedName(storedName)
                .url(url)
                .thumbnailStoredName(thumbnailStoredName)
                .thumbnailUrl(thumbnailUrl)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ReviewImage(Long id, Review review, String originalName, String storedName, String thumbnailStoredName, String url, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(originalName, storedName, url, thumbnailStoredName, thumbnailUrl, createdAt, updatedAt);
        this.id = id;
        this.review = review;
        this.deletedAt = deletedAt;
    }
}
