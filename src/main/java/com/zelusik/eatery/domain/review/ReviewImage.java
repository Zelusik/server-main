package com.zelusik.eatery.domain.review;

import com.zelusik.eatery.domain.S3Image;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ReviewImage extends S3Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @OneToMany(mappedBy = "reviewImage")
    private List<ReviewImageMenuTag> menuTags = new LinkedList<>();

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime deletedAt;

    public static ReviewImage of(Review review, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl) {
        return of(null, review, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, null, null, null);
    }

    public static ReviewImage of(Long id, Review review, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewImage(id, review, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, createdAt, updatedAt, deletedAt);
    }

    private ReviewImage(Long id, Review review, String originalName, String storedName, String thumbnailStoredName, String url, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(originalName, storedName, url, thumbnailStoredName, thumbnailUrl, createdAt, updatedAt);
        this.id = id;
        this.review = review;
        this.deletedAt = deletedAt;
    }

    public void softDelete() {
        this.setDeletedAt(LocalDateTime.now());
    }
}
