package com.zelusik.eatery.global.util;

import java.util.Random;

public class NicknameGenerator {

    private static final String[] FLAVOR_LIST = {"달달한", "바삭한", "매콤한", "쫄깃한", "아삭한", "촉촉한", "고소한", "알싸한", "얼얼한", "얼큰한", "진한", "싱거운", "짭잘한", "짭조름한", "담백한", "느끼한", "새콤한", "신선한", "달콤한", "구수한", "삼삼한", "밍밍한", "칼칼한"};
    private static final String[] FOOD_LIST = {"햄버거", "감자튀김", "감자탕", "닭발구이", "피자", "치킨", "비빔밥", "떡볶이", "갈비", "곱창전골", "삼겹살", "호떡", "만두", "물회", "연어", "타코야끼", "쌀국수", "냉면", "샤브샤브", "제육볶음", "쫄면", "붕어빵", "마라탕", "마라샹궈", "김치찌개", "된장찌개", "카레", "돈까스", "짬뽕", "짜장면", "유린기", "초밥", "딱새우", "칼국수", "새우튀김", "어묵우동", "육회", "가라아게", "콘스프", "간장새우", "파스타", "볶음밥", "소세지", "리조또", "깐풍기", "칠리새우", "크림새우", "군만두", "꿔바로우", "분짜", "스프링롤", "팟타이", "똠양꿍", "닭강정", "떡갈비", "핫도그", "샐러드", "닭갈비", "계란말이", "탄두리"};

    public static String generateRandomNickname() {
        Random rand = new Random();
        int flavorIdx = rand.nextInt(FLAVOR_LIST.length);
        int foodIdx = rand.nextInt(FOOD_LIST.length);
        return FLAVOR_LIST[flavorIdx] + " " + FOOD_LIST[foodIdx];
    }
}
