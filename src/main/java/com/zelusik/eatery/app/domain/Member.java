package com.zelusik.eatery.app.domain;

import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.constant.member.LoginType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE member_id = ?")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @JoinColumn(name = "terms_info_id")
    @OneToOne(fetch = FetchType.LAZY)
    private TermsInfo termsInfo;

    @Column(nullable = false, unique = true)
    private String socialUid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String email;

    @Column(nullable = false)
    private String nickname;

    private Integer ageRange;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime deletedAt;

    public static Member of(TermsInfo termsInfo, String uid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender) {
        return new Member(termsInfo, uid, loginType, email, nickname, ageRange, gender);
    }

    private Member(TermsInfo termsInfo, String socialUid, LoginType loginType, String email, String nickname, Integer ageRange, Gender gender) {
        this.termsInfo = termsInfo;
        this.socialUid = socialUid;
        this.loginType = loginType;
        this.email = email;
        this.nickname = nickname;
        this.ageRange = ageRange;
        this.gender = gender;
    }
}
