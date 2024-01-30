package com.zelusik.eatery.domain.report_place.entity;

import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.report_place.dto.ReportPlaceReasonOption;
import com.zelusik.eatery.global.common.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table
@Entity
public class ReportPlace extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_place_id")
    private Long id;

    @JoinColumn(name = "reporter_id")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporter;

    @JoinColumn(name = "place_id")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReportPlaceReasonOption reasonOption;

    @NotNull
    private String reasonDetail;

    public ReportPlace(Long id, Member reporter, Place place, ReportPlaceReasonOption reasonOption, String reasonDetail, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.reporter = reporter;
        this.place = place;
        this.reasonOption = reasonOption;
        this.reasonDetail = reasonDetail;
    }

    public static ReportPlace create(Member reporter, Place place, ReportPlaceReasonOption reasonOption, String reasonDetail) {
        return create(null, reporter, place, reasonOption, reasonDetail, null, null);
    }

    public static ReportPlace create(Long id, Member reporter, Place place, ReportPlaceReasonOption reasonOption, String reasonDetail, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new ReportPlace(id, reporter, place, reasonOption, reasonDetail, createdAt, updatedAt);
    }
}
