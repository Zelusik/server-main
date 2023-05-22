package com.zelusik.eatery.domain.member;

import com.zelusik.eatery.constant.FoodCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(indexes = {
        @Index(columnList = "category")
})
@Entity
public class FavoriteFoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_food_category_id")
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodCategory category;

    public static FavoriteFoodCategory of(Member member, FoodCategory category) {
        return new FavoriteFoodCategory(null, member, category);
    }

    public static FavoriteFoodCategory of(Long id, Member member, FoodCategory category) {
        return new FavoriteFoodCategory(id, member, category);
    }
}
