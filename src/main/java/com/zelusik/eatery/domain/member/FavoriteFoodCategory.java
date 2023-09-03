package com.zelusik.eatery.domain.member;

import com.zelusik.eatery.constant.FoodCategoryValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(indexes = {
        @Index(name = "idx__favorite_food_category__category", columnList = "category")
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
    private FoodCategoryValue category;

    public static FavoriteFoodCategory of(Member member, FoodCategoryValue category) {
        return new FavoriteFoodCategory(null, member, category);
    }

    public static FavoriteFoodCategory of(Long id, Member member, FoodCategoryValue category) {
        return new FavoriteFoodCategory(id, member, category);
    }
}
