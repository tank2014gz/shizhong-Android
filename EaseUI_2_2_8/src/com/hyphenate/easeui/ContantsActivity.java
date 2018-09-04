package com.hyphenate.easeui;

/**
 * Created by yuliyan on 15/12/26.
 */

import com.hyphenate.easeui.utils.PrefUtils;

import java.security.PublicKey;

/**
 * Activity中一些必要的常量
 */
public class ContantsActivity {

	public static class LoginModle {
		public static final String IS_OPENT_FIRST = "isOpenFirst";
		public static final String PHONE = "phone_num";
		public static final String PWD = "pass_word";
		public static final String LOGIN_PLATFORM = "login_platform";// 0:手机登录
//																		// 2：微博登录
//																		// 1：微信登录
//																		// 3：QQ
//																		// 登录
		public static final String APPID_PLATEFORM = "app_id";// 第三方平台的APPID需要保持一下
		public static final String PLATFORM_TYPE = "login_type";

		public static final String INDENTIFY = "indentify_num";
		public static final String REG_PLATFORM = "android";
		public static final String DEVICES_ID = "device_id";
		public static final String LOGIN_TOKEN = "login_token";
		public static final String LOGIN_USER_ID = "user_id";
		public static final String NICK_NAME = "nick_name";
		public static final String IS_LOGIN = "is_login";
		public static final String HEAD_URL = "head_url";
		public static final String BIRTHDAY = "birthday";
		public static final String SEX = "sex";
		public static final String PROVINCE = "provinceId";
		public static final String CITY = "city_id";
		public static final String DISTRICTID = "districtId";
		public static final String SIGNTURE = "signture";
		public static final String IS_CANBACK = "can_back";
		public static final String IS_FIRST_USER_IN_MAIN = "first_in_mian";
		public static final String IS_FIRST_USE_IN_VIDEO = "first_in_video";
		public static final String LAST_CHECKED_VERSION_DATE = "update_version_date";

		public  static  final  String IS_USER_FINISH_FAIL="is_user_finish_info_fail";//完善个人资料是否失败
		public  static  final  String GETUI_CLIENT_ID="clent_id";

	}

	public static class Image {
		public static final int BIG = 1080;
		public static final int MODLE = 540;
		public static final int MODLE_HDPI = 320;
		public static final int SMALL = 100;
	}

	public static class AD {

		public static final String AD_IMAGE_FILE = "ad_image";
		public static final String AD_TARGET_TYPE = "ad_target_type";
		public static final String AD_URL = "ad_url";// 链接
		public static final String AD_ID = "ad_id";// 视频详情

	}

	public  static  class  WEB{
		public static  final  String URL="WEB_URL";
		public  static  final  String TITLE="WEB_TITLE";
	}
	public static class JieQu {
		public static final String NEWS_URL = "news_url";
		public static final String BANNER_URL = "banner_url";
	}

	public  static  class  GeTui{
		public static final String CLIENT_ID="getui_client_id";
	}
	public static class Video {
		public static final String VIDEO_ID = "videoId";
		public static final String VIDEO_OBJ = "video";
		public static final String VIDEO_PATH = "video_path";
		public static final String VIDEO_DURING = "video_during";
		public static final String VIDEO_TASK_ID = "task_id";
		public static final String VIDEO_POSITION = "video_position";
		public static final String VIDEO_IS_LIKED = "video_is_liked";
		public static final String VIDEO_LIKE_NUM = "video_like_count";
		public static final String VIDEO_REPLY_COUNT = "video_reply_count";
		public static final String VIDEO_DELECT = "video_delect";
		public static final String VIDEO_IS_IMPORT = "is_import_video";
		public static final String VIDEO_COVER_PATH = "video_cover_path";
		public static final String VIDEO_ORIENTATION = "video_orientation";
		public static final String VIDEO_IS_FIRST_IMPORT_H = "video_first_import_h";// 第一次横向导入
		public static final String VIDEO_IS_FIRST_IMPORT_V = "video_first_import_v";// 第一次纵向导入
		public static final String VIDEO_WIDTH = "video_width";
		public static final String VIDEO_HEIGHT = "video_height";

	}

	public static class Club {
		public static final String CLUB_ID = "ID_club";
		public static final String CLUB_NAME = "NAME_club";
	}

	public static class Topic {
		public static final String TOPIC_ID = "ID_topic";
		public static final String TOPIC_NAME = "NAME_topic";
		public static final String TOPIC_PAGE = "page_count";
	}

	public static class Message {
		public static final String MESSAGE_TYPE = "message_type";

		public static final String MESSAGES_HAS = "messages_has";
		public static final String MESSAGE_NOTIC = "1003";// 通知
		public static final String MESSAGE_RECOMMEND_PERSON = "4";// 推个人
		public static final String MESSAGE_RECOMMEND_VIDEO = "5";// 推荐视频
		public static final String MESSAGE_RECOMMEND_TOPIC = "6";// 推荐话题
		public static final String MESSAGE_RECOMMEND_NEWS = "7";// 推荐资讯
		public static final String MESSAGE_RECOMMEND_CLUB = "8";// 推荐舞社
		public static final String MESSAGE_HAS_NOTIC = "has_notic";
		public static final String MESSAGE_COMMENT = "1002";
		public static final String MESSAGE_COMMENT_VIDEO = "3";// 评论视频
		public static final String MESSAGE_HAS_COMMENT = "has_comment";
		public static final String MESSAGE_LIKE = "1001";
		public static final String MESSAGE_LIKE_VIDEO = "0";// 视频点赞
		public static final String MESSAGE_LIKE_REPLY = "1";// 评论点赞
		public static final String MESSAGE_HAS_LIKE = "has_like";
		public static final String MESSAGE_NEW_FRIEND = "1000";
		public static final String MESSAGE_ATTENT_PERSION = "2";// 谁关注我
		public static final String MESSAGE_HAS_NEW_FRIEND = "has_new_friend";
	}

	public static class Action {
		public  static  final  String ACTION_GET_GETUI_CLIENT_ID="android.intent.action.get.getui.clientid";
		public  static  final  String ACTION_GET_GETUI_MESSAGE="android.intent.action.get_getui.message";
		public static final int ACTION_INPUT_VIDEO = 0x1001;
		public static final int ACTION_MODIFY_NICKNAME = 0x1002;
		public static final int ACTION_MODIFY_SIGNTURE = 0x1003;
		public static final int ACTION_MODIFY_USERINFO = 0x1004;
		public static final int ACTION_CHANGE_VIDEO_COVER = 0x1005;
		public static final int ACTION_PUSH_VIDEO = 0x1006;
		public static final int ACTION_SKIP_VIDEO_DETALE = 0x1007;

		public static final int RESULT_LOGOUT=0x1009;
		public static  final int REQUEST_ACTION_LOOK_OVER_MEMBER_INFO=0x1010;//查看个人资料
		public  static  final  int REQUEST_ACTION_GO_LOOK_OVER_VIDER_INFO=0x1011;//查看视频详情
		public  static  final  int REQUEST_ACTION_GO_LOOK_OVER_TOPIC_DETAIL=0x1012;// 查看话题详情
		public  static  final  int REQUEST_ACTION_GO_LOOK_OVER_TOPIC_LIST=0x1014;//查看话题列表
		public static  final  int REQUEST_ACTION_GO_LOOK_OVER_CREW_DETAIL=0x1013;//查看舞社详情
		public  static  final  int REQUEST_ACTION_GO_LOOK_NEWS_LIST=0x1015;// 查看资讯列表
		public  static  final  int REQUEST_ACTION_GO_LOOK_CLUBS_LIST=0x1016;//查看舞社列表
		public  static  final  int REQUEST_ACTION_GO_LOOK_RANK_LIST=0X1017;//查看排行榜列表
		public static final int REQUEST_ACTION_GO_LOOK_MUSIC_LIST=0x1024;
		public  static  final  int REQUEST_ACTION_GO_LOOK_NERABY_LIST=0X1018;//查看附近列表
		public  static final int REQUEST_ACTION_GO_LOOK_CHART_LIST=0x1019;//查看聊天会话列表
		public  static final int REQUEST_ACTION_GO_LOOK_MESSEGE_LIST=0x1019;//查看推送消息列表
		public  static  final String CACHE_GETUI_INFO_DATA="getui_data";


		public static final int RESULT_ACTION_REG_SUCCESS=0x1020;
		public  static  final  int RESULT_ACTION_FIND_BACK_SUCCESS=0x1023;


	}

	public static class Extra {
		public static final String EXTRA_FINISH_APP = "finish_app";
		public static final String IS_FINISH_BACK_ACTIVITY = "isFinish_hasActivity";// 是否删除已经存在的activity
		public  static final  String IS_GET_GETUI_CLIENT_ID="is_get_getui_clientid";
		public  static final String EXTRA_GET_GETUI_CLIENT_ID="get_getui_clientid";
		public  static  final  String IS_GET_GETUI_MESSAGE="is_get_getui_message";
		public static  final  String EXTRA_GET_GETUI_TASKID="get_getui_taskid";
		public  static final  String EXTRA_GET_GETUI_MESSAGEID="get_getui_messsageid";
		public static  final  String EXTRA_GET_GETUI_MESSGE="get_getui_message";
		public static  final String EXTRA_GET_GETUI_MESSAGE="get_getui_message";
	}


	public  static class MUSIC{
		public static final String music_calss_name="music_class_name";
	}

}
