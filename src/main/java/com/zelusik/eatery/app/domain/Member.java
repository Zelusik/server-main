package com.zelusik.eatery.app.domain;

import com.zelusik.eatery.app.domain.constant.LoginType;
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

    @JoinColumn(name = "terms_info_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private TermsInfo termsInfo;

    @Column(nullable = false)
    private String uid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String email;

    @Column(nullable = false)
    private String nickname;

    private LocalDateTime deletedAt;

    public static Member of(String uid, LoginType loginType, String email, String nickname) {
        return new Member(uid, loginType, email, nickname);
    }

    private Member(String uid, LoginType loginType, String email, String nickname) {
        this.uid = uid;
        this.loginType = loginType;
        this.email = email;
        this.nickname = nickname;
    }
}
