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
@SQLDelete(sql = "UPDATE review_file SET deleted_at = CURRENT_TIMESTAMP WHERE review_file_id = ?")
@Entity
public class ReviewFile extends S3Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_file_id")
    private Long id;

    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    public static ReviewFile of(Review review, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl) {
        return ReviewFile.builder()
                .review(review)
                .originalName(originalName)
                .storedName(storedName)
                .url(url)
                .thumbnailStoredName(thumbnailStoredName)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ReviewFile(Review review, String originalName, String storedName, String thumbnailStoredName, String url, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(originalName, storedName, url, thumbnailStoredName, thumbnailUrl, createdAt, updatedAt, deletedAt);
        this.review = review;
    }
}
