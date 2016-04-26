package com.ptit.ptitroyal.data;

/**
 * Created by HoangTien on 4/20/16.
 */
public class Constants {

    public static final String TAG = "TienDH";
    public static final String URL_HOST = "http://40.83.127.97:3000";
    public static final String API_VERIFY_TOKEN = URL_HOST + "/api/access-token/verify";
    public static final String API_LOGIN = URL_HOST + "/api/login";
    public static final String API_LOGOUT = URL_HOST + "/api/logout";
    public static final String API_GET_POST = URL_HOST + "/api/me/posts";
    public static final String API_PROFILE = URL_HOST + "/api/me/about";
    public static final String API_GCM = URL_HOST + "/api/me/gcm";
    public static final String GET_NUMBER_OF_NOTIFICATIONS_URL = URL_HOST + "/api/me/notis_new";

    public static final String FACEBOOK_TOKEN = "fb_token";
    public static final String STATUS = "status";
    public static final String RESULT = "result";


    public static final String SUCCESS = "success";
    public static final String PTIT_ROYAL_PREFERENCES = "PTIT_ROYAL";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String GCM_ID = "gcm_id";
    public static final String TYPE = "type";

    public static final String FAIL = "fail";
    public static final String SERVER_ERROR_MESSAGE = "Xảy ra lỗi. Vui lòng thử lại!";
    public static final String STUDY = "study";
    public static final String ACTIVITIES = "relax";
    public static final String GASTRONOMY = "food";


    // Notify infor
    public static final String ID = "id";
    public static final String FROM = "from";
    public static final String POST = "post";
    public static final String CREATE_DATE = "create_date";
    public static final String NAME = "name";
    public static final String AVATAR = "avatar";
    public static final String READ = "read";
    //
    public static final String CONTENT = "content";
    public static final String IMAGE_UPLOAD = "image";
    public static final String POST_ID_TOPIC_REPLACE = ":id";


    //
    public static final String GET_NOTIS = URL_HOST + "/api/me/notis";
    public static final String DO_POST_STATUS = URL_HOST + "/api/topics/" + POST_ID_TOPIC_REPLACE + "/posts";


}
