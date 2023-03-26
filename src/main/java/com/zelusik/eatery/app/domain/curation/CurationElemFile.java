package com.zelusik.eatery.app.domain.curation;

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
@SQLDelete(sql = "UPDATE curation_elem_file SET deleted_at = CURRENT_TIMESTAMP WHERE curation_elem_file_id = ?")
@Entity
public class CurationElemFile extends S3Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curation_elem_file_id")
    private Long id;

    public static CurationElemFile of(String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl) {
        return of(null, originalName, storedName, url, thumbnailStoredName, thumbnailUrl, null, null, null);
    }

    public static CurationElemFile of(Long id, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return CurationElemFile.builder()
                .id(id)
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
    private CurationElemFile(Long id, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(originalName, storedName, url, thumbnailStoredName, thumbnailUrl, createdAt, updatedAt, deletedAt);
        this.id = id;
    }
}
