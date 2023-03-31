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
@SQLDelete(sql = "UPDATE curation SET deleted_at = CURRENT_TIMESTAMP WHERE curation_id = ?")
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

    private LocalDateTime deletedAt;

    public static Curation of(String title) {
        return of(null, title, null, null, null);
    }

    public static Curation of(Long id, String title, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return Curation.builder()
                .id(id)
                .title(title)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Curation(Long id, String title, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.title = title;
        this.deletedAt = deletedAt;
    }
}
