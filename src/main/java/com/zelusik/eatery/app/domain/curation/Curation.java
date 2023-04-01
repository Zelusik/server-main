package com.zelusik.eatery.app.domain.curation;

import com.zelusik.eatery.app.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = {
        @Index(columnList = "createdAt")
})
@Entity
public class Curation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curation_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "curation")
    private List<CurationElem> curationElems = new LinkedList<>();

    public static Curation of(String title) {
        return of(null, title, null, null);
    }

    public static Curation of(Long id, String title, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return Curation.builder()
                .id(id)
                .title(title)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Curation(Long id, String title, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.title = title;
    }
}
