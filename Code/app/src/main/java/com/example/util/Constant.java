package com.example.util;


import com.example.videostreamingapp.BuildConfig;

import java.io.Serializable;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SERVER_URL = BuildConfig.SERVER_URL;

    public static final String API_URL = SERVER_URL + "api/v1/";

    public static final String ARRAY_NAME = "VIDEO_STREAMING_APP";

    public static final String DYNAMIC_HOME_URL = API_URL + "dynamic_home_screen";
    public static final String LOGIN_URL = API_URL + "login";
    public static final String CONTINUE_WATCHING_URL = API_URL + "c_watchlist_add";
    public static final String CONTINUE_WATCHING_GET_URL = API_URL + "c_watchlist_get";
    public static final String LOGIN_WITH_FACEBOOK_URL = API_URL + "facebook";
    public static final String REGISTER_URL = API_URL + "signup";
    public static final String LANGUAGE_URL = API_URL + "languages";
    public static final String GENRE_URL = API_URL + "genres";
    public static final String SHOW_BY_LANGUAGE_URL = API_URL + "shows_by_language";
    public static final String SHOW_BY_GENRE_URL = API_URL + "shows_by_genre";
    public static final String MOVIE_BY_LANGUAGE_URL = API_URL + "movies_by_language";
    public static final String UPCOMING_MOVIES = API_URL + "movies_by_upcoming";
    public static final String SHOW_ALL = API_URL + "show_all";
    public static final String MOVIE_BY_GENRE_URL = API_URL + "movies_by_genre";
    public static final String SPORT_CATEGORY_URL = API_URL + "sports_category";
    public static final String SPORT_BY_CATEGORY_URL = API_URL + "sports_by_category";
    public static final String TV_CATEGORY_URL = API_URL + "livetv_category";
    public static final String MENU_CATEGORY_URL = API_URL + "menu_category";
    public static final String TV_BY_CATEGORY_URL = API_URL + "livetv_by_category";
    public static final String MOVIE_DETAILS_URL1 = API_URL + "movies_details1";
    public static final String LIKE_MOVIE = API_URL + "movie_like";
    public static final String DISLIKE_MOVIE = API_URL + "movie_dislike";
    public static final String LIKE_COUNT_MOVIE = API_URL + "movie_like_count";
    public static final String SPORT_DETAILS_URL = API_URL + "sports_details";
    public static final String TV_DETAILS_URL = API_URL + "livetv_details";
    public static final String SHOW_DETAILS_URL = API_URL + "show_details";
    public static final String EPISODE_ONE_LIST_URL = API_URL + "episodes1";
    public static final String PLAN_LIST_URL = API_URL + "subscription_plan";
    public static final String RENTAL_LIST_URL = API_URL + "rental_plan";
    public static final String PROFILE_URL = API_URL + "profile";
    public static final String EDIT_PROFILE_URL = API_URL + "profile_update";
    public static final String CHANGE_PASSWORD = API_URL + "ChangePassword";
    public static final String SETTING_URL = API_URL + "settings";
    public static final String APP_DETAIL_ONE_URL = API_URL + "app_details1";
    public static final String APP_UPDATE_CHECK = API_URL + "android_v_chek";
    public static final String SEARCH_URL = API_URL + "search";
    public static final String PAYMENT_SETTING_URL = API_URL + "payment_settings";
    public static final String DASH_BOARD_URL = API_URL + "dashboard";

    public static final String TRANSACTION_URL = API_URL + "transaction_add";
    public static final String CHECK_PROMOCODE = API_URL + "check_promocode";
    public static final String FORGOT_PASSWORD_URL = API_URL + "forgot_password";
    public static final String WATCH_HOUR = API_URL + "watch_hours";
    public static final String STRIPE_TOKEN_URL = API_URL + "stripe_token_get";
    public static final String EPISODE_RECENTLY_URL = API_URL + "episodes_recently_watched";
    /* Watch List */
    public static final String SAVED_TO_WATCHLIST = API_URL + "save-to-watchlist";
    public static final String REMOVE_TO_WATCHLIST = API_URL + "remove-watchlist-video";
    public static final String GET_WATCHLIST = API_URL + "get_watchlist";
    public static final String GET_TRANSACTION_LIST = API_URL + "get_transaction_list";

    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_ID = "category_id";

    public static final String LANGUAGE_ID = "language_id";
    public static final String LANGUAGE_NAME = "language_name";
    public static final String LANGUAGE_IMAGE = "language_image";

    public static final String GENRE_ID = "genre_id";
    public static final String GENRE_NAME = "genre_name";
    public static final String GENRE_IMAGE = "genre_image";
    public static final String GENRE_LIST = "genre_list";

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_FROM = "movie_from";

    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_DESC = "description";
    public static final String MOVIE_POSTER = "movie_poster";
    public static final String MOVIE_IMAGE = "movie_image";
    public static final String MOVIE_LANGUAGE = "language_name";
    public static final String MOVIE_DATE = "release_date";
    public static final String MOVIE_URL = "video_url";
    public static final String MOVIE_TYPE = "video_type";
    public static final String MOVIE_DURATION = "movie_duration";
    public static final String MOVIE_ACCESS = "movie_access";
    public static final String DOWNLOAD_ENABLE = "download_enable";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String IMDB_RATING = "imdb_rating";
    public static final String MOVIE_SHARE_URL = "movie_share_url";
    public static final String LIKE = "Like";
    public static final String DISLIKE = "Dislike";
    public static final String TRAILER_URL = "trailer_url";
    public static final String IS_AVAILABLE = "is_available";
    public static final String RELATED_MOVIE_ARRAY_NAME = "related_movies";
    public static final String SOCIAL_TYPE = "s_type";


    public static final String SHOW_ID = "show_id";
    public static final String SHOW_NAME = "show_name";
    public static final String SHOW_TITLE = "show_title";
    public static final String SHOW_DESC = "show_info";
    public static final String SHOW_POSTER = "show_poster";
    public static final String SHOW_LANGUAGE = "show_lang";
    public static final String RELATED_SHOW_ARRAY_NAME = "related_shows";

    public static final String SEASON_ARRAY_NAME = "season_list";
    public static final String SEASON_ID = "season_id";
    public static final String SEASON_NAME = "season_name";

    public static final String EPISODE_ID = "episode_id";
    public static final String EPISODE_TITLE = "episode_title";
    public static final String EPISODE_IMAGE = "episode_image";
    public static final String EPISODE_DESCRIPTION = "description";
    public static final String EPISODE_TYPE = "video_type";
    public static final String EPISODE_URL = "video_url";
    public static final String EPISODE_ACCESS = "video_access";
    public static final String EPISODE_DATE = "release_date";
    public static final String EPISODE_DURATION = "duration";

    public static final String SPORT_ID = "sport_id";
    public static final String SPORT_TITLE = "sport_title";
    public static final String SPORT_IMAGE = "sport_image";
    public static final String SPORT_URL = "video_url";
    public static final String SPORT_DESC = "description";
    public static final String SPORT_TYPE = "video_type";
    public static final String SPORT_CATEGORY = "category_name";
    public static final String SPORT_DATE = "date";
    public static final String SPORT_DURATION = "sport_duration";
    public static final String SPORT_ACCESS = "sport_access";
    public static final String RELATED_SPORT_ARRAY_NAME = "related_sports";
    public static final String RELATED_TV_ARRAY_NAME = "related_live_tv";


    public static final String TV_ID = "tv_id";
    public static final String TV_TITLE = "tv_title";
    public static final String TV_IMAGE = "tv_logo";
    public static final String TV_ACCESS = "tv_access";
    public static final String TV_URL = "tv_url";
    public static final String TV_DESC = "description";
    public static final String TV_TYPE = "tv_url_type";
    public static final String TV_CATEGORY = "category_name";

    public static final String PLAN_ID = "plan_id";
    public static final String PLAN_NAME = "plan_name";
    public static final String PLAN_DURATION = "plan_duration";
    public static final String PLAN_DESC = "plan_description";
    public static final String PLAN_PRICE = "plan_price";
    public static final String CURRENCY_CODE = "currency_code";
    public static final String PAY_PAL_ON = "paypal_payment_on_off";
    public static final String PAY_PAL_SANDBOX = "paypal_mode";
    public static final String PAY_PAL_CLIENT = "paypal_client_id";
    public static final String STRIPE_PUBLISHER = "stripe_publishable_key";
    public static final String STRIPE_ON = "stripe_payment_on_off";
    public static final String APP_PRIVACY_POLICY = "app_privacy";

    public static final String USER_NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE = "phone";
    public static final String USER_ADDRESS = "user_address";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_PLAN_STATUS = "check_plan";
    public static final String RENTAL_PLAN_STATUS = "rental_plan";

    public static final String FILTER_NEWEST = "new";
    public static final String FILTER_OLDEST = "old";
    public static final String FILTER_ALPHA = "alpha";
    public static final String FILTER_RANDOM = "rand";


    //User details field
    public static java.lang.String FB_NAME = "name";
    public static String FB_PICTURE = "picture";
    public static String FB_DATA = "data";
    public static String FB_PICTURE_URL = "url";
    public static String FB_ID = "id";


    public static int GET_SUCCESS_MSG;
    public static String VERIFYOTP;
    public static final String MSG = "msg";
    public static final String SUCCESS = "success";
    public static final String STATUS = "status";
    public static final String VERIFY = "verify";
    public static int AD_COUNT = 0;
    public static int AD_COUNT_SHOW;

    // ======================== Exo Download Action ===================
    public static final String EXO_DOWNLOAD_ACTION_START="EXO_DOWNLOAD_START";
    public static final String EXO_DOWNLOAD_ACTION_PAUSE="EXO_DOWNLOAD_PAUSE";
    public static final String EXO_DOWNLOAD_ACTION_CANCEL="EXO_DOWNLOAD_CANCEL";

    public static boolean isBanner = false, isInterstitial = false;
    public static String adMobBannerId, adMobInterstitialId, adMobPublisherId;

    /*Premium Position */
    public static String POSITION = "premium_position_movie";

}