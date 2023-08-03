package com.zelusik.eatery.domain;

import com.zelusik.eatery.constant.MenuKeywordCategory;
import com.zelusik.eatery.converter.MenuKeywordsConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MenuKeyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_keyword_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MenuKeywordCategory category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Convert(converter = MenuKeywordsConverter.class)
    private List<String> keywords;

    public static MenuKeyword of(@NonNull MenuKeywordCategory category, @NonNull String name, @NonNull List<String> keywords) {
        return new MenuKeyword(null, category, name, keywords, null, null);
    }

    public static MenuKeyword of(@Nullable Long id, @NonNull MenuKeywordCategory category, @NonNull String name, @NonNull List<String> keywords, @Nullable LocalDateTime createdAt, @Nullable LocalDateTime updatedAt) {
        return new MenuKeyword(id, category, name, keywords, createdAt, updatedAt);
    }

    private MenuKeyword(@Nullable Long id, @NonNull MenuKeywordCategory category, String name, @NonNull List<String> keywords, @Nullable LocalDateTime createdAt, @Nullable LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.category = category;
        this.name = name;
        this.keywords = keywords;
    }
}
