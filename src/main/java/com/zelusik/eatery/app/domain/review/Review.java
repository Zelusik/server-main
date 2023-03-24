package com.zelusik.eatery.app.domain.review;


import com.zelusik.eatery.app.domain.BaseTimeEntity;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.place.Place;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE review SET deleted_at = CURRENT_TIMESTAMP WHERE review_id = ?")
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @JoinColumn(name = "writer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @JoinColumn(name = "place_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @Column(length = 400)
    private String autoCreatedContent;

    @Setter(AccessLevel.PRIVATE)
    @Column(length = 400)
    private String content;

    @OneToMany(mappedBy = "review")
    private List<ReviewKeyword> keywords = new LinkedList<>();

    @OneToMany(mappedBy = "review")
    List<ReviewFile> reviewFiles = new LinkedList<>();

    private LocalDateTime deletedAt;

    public static Review of(Member writer, Place place, String autoCreatedContent, String content) {
        return new Review(writer, place, autoCreatedContent, content);
    }

    private Review(Member writer, Place place, String autoCreatedContent, String content) {
        this.writer = writer;
        this.place = place;
        this.autoCreatedContent = autoCreatedContent;
        this.content = content;
    }

    public void update(String content) {
        this.setContent(content);
    }
}
