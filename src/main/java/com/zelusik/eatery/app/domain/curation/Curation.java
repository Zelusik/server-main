package com.zelusik.eatery.app.domain.curation;

import com.zelusik.eatery.app.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE curation SET deleted_at = CURRENT_TIMESTAMP WHERE curation_id = ?")
@Entity
public class Curation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curation_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    private LocalDateTime deletedAt;
}
