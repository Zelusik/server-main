package com.zelusik.eatery.domain.member;

import com.zelusik.eatery.domain.S3Image;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ProfileImage extends S3Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long id;

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime deletedAt;

    public static ProfileImage of(Member member, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl) {
        return of(
                null,
                member,
                originalName,
                storedName,
                url,
                thumbnailStoredName,
                thumbnailUrl,
                null,
                null,
                null
        );
    }

    public static ProfileImage of(Long id, Member member, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return ProfileImage.builder()
                .id(id)
                .member(member)
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

    public void softDelete() {
        this.setDeletedAt(LocalDateTime.now());
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ProfileImage(Long id, Member member, String originalName, String storedName, String url, String thumbnailStoredName, String thumbnailUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(originalName, storedName, url, thumbnailStoredName, thumbnailUrl, createdAt, updatedAt);
        this.id = id;
        this.member = member;
        this.deletedAt = deletedAt;
    }
}
