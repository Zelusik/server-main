package com.zelusik.eatery.app.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE place SET deleted_at = CURRENT_TIMESTAMP WHERE place_id = ?")
@Entity
public class ReviewFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_file_id")
    private Long id;

    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column(nullable = false)
    private String uploaderIp;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String url;

    private LocalDateTime deletedAt;

    public static ReviewFile of(Review review, String uploaderIp, String originalName, String storedName, String url) {
        return new ReviewFile(review, uploaderIp, originalName, storedName, url);
    }

    private ReviewFile(Review review, String uploaderIp, String originalName, String storedName, String url) {
        this.review = review;
        this.uploaderIp = uploaderIp;
        this.originalName = originalName;
        this.storedName = storedName;
        this.url = url;
    }
}
