package com.zelusik.eatery.domain;

import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(name = "idx__bookmark__member_id", columnList = "member_id"),
        @Index(name = "idx__bookmark__place_id", columnList = "place_id"),
        @Index(name = "idx__bookmark__created_at", columnList = "createdAt")
})
@Entity
public class Bookmark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "place_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    public static Bookmark of(Member member, Place place) {
        return Bookmark.builder()
                .member(member)
                .place(place)
                .build();
    }

    public static Bookmark of(Long id, Member member, Place place, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return Bookmark.builder()
                .id(id)
                .member(member)
                .place(place)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Bookmark(Long id, Member member, Place place, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.member = member;
        this.place = place;
    }
}
