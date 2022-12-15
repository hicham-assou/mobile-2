package be.hicham.v2_nhi_shop.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_USERNAME = "chatPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmtoken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_ARTICLE = "article";
    public static final String KEY_ARTICLE_ID = "articleId";
    public static final String KEY_TIMESTAMP_ARTICLE = "timestamp";
    public static final String KEY_COLLECTION_ARTICLES = "articles";
    public static final String KEY_TITLE_ARTICLE = "title";
    public static final String KEY_DESCRIPTION_ARTICLE = "description";
    public static final String KEY_PRICE_ARTICLE = "price";
    public static final String KEY_LOCALISATION_ARTICLE = "localisation";
    public static final String KEY_IMAGE_ARTICLE = "imageArticle";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAxlZf-0c:APA91bFjcl2W_qK0vJ-0qT1Qx5Y5oqOLz_1sQDhFhxQyCmuV1aU8sfht7VYB5AGL9lugRjzRasucWABa354cy5nLn1iPdxF-s44j_vN3qdBsgFSp6fqxMt8bR_n1UawBtyNh9ATw9i2Z"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }



}
