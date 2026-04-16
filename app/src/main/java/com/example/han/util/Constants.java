package com.example.han.util;

public class Constants {
    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final String PREFS_NAME = "han_prefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";

    public static final int PAGE_SIZE = 10;

    public static class PostType {
        public static final String ALL = "";
        public static final String HANFU = "Hanfu";
        public static final String POETRY = "Poetry";
        public static final String MUSIC = "Music";
        public static final String ETIQUETTE = "Etiquette";
        public static final String SOLAR = "Solar";
        public static final String USER_POST = "UserPost";

        public static String[] getDisplayNames() {
            return new String[]{"全部", "汉服", "诗词", "音乐", "礼仪", "节气", "用户发布"};
        }

        public static String[] getValues() {
            return new String[]{ALL, HANFU, POETRY, MUSIC, ETIQUETTE, SOLAR, USER_POST};
        }
    }

    public static class SortType {
        public static final String CREATED_AT = "createdAt";
        public static final String LIKES_COUNT = "likesCount";
    }
}
