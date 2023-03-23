package com.zelusik.eatery.app.domain.review;

import com.zelusik.eatery.app.domain.S3File;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE review_file SET deleted_at = CURRENT_TIMESTAMP WHERE review_file_id = ?")
@Entity
public class ReviewFile extends S3File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_file_id")
    private Long id;

    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    public static ReviewFile of(Review review, String originalName, String storedName, String url) {
        return new ReviewFile(review, originalName, storedName, url);
    }

    private ReviewFile(Review review, String originalName, String storedName, String url) {
        this.review = review;
        this.originalName = originalName;
        this.storedName = storedName;
        this.url = url;
    }
}
