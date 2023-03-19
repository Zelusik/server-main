package com.zelusik.eatery.app.domain.curation;

import com.zelusik.eatery.app.domain.S3File;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE curation_elem_file SET deleted_at = CURRENT_TIMESTAMP WHERE curation_elem_file_id = ?")
@Entity
public class CurationElemFile extends S3File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curation_elem_file_id")
    private Long id;
}
