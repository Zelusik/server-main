package com.zelusik.eatery.app.domain.member;

import com.zelusik.eatery.app.domain.S3File;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE profile_image SET deleted_at = CURRENT_TIMESTAMP WHERE profile_image_id = ?")
@Entity
public class ProfileImage extends S3File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long id;

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    public static ProfileImage of(Member member, String originalName, String storedName, String url) {
        return ProfileImage.builder()
                .member(member)
                .originalName(originalName)
                .storedName(storedName)
                .url(url)
                .build();
    }

    public static ProfileImage of(Long id, Member member, String originalName, String storedName, String url, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return ProfileImage.builder()
                .id(id)
                .member(member)
                .originalName(originalName)
                .storedName(storedName)
                .url(url)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ProfileImage(Long id, Member member, String originalName, String storedName, String url, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(originalName, storedName, url, createdAt, updatedAt, deletedAt);
        this.id = id;
    }
}
