package com.android.book;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;

public class Const {
	public static int version = 3;
//	public static final DisplayImageOptions DISPLAY_ICON_OPTIONS = new DisplayImageOptions.Builder()
//			.showImageForEmptyUri(R.drawable.icon_loading_placeholder)
//			.showImageOnFail(R.drawable.icon_loading_placeholder)
//			.showImageOnLoading(R.drawable.icon_loading_placeholder)
//			.resetViewBeforeLoading(true)
//			.cacheInMemory(true)
//			.cacheOnDisk(true)
//			.build();
//
//	public static final DisplayImageOptions DISPLAY_NORMAL_OPTIONS = new DisplayImageOptions.Builder()
//			.showImageForEmptyUri(R.drawable.transparent)
//			.showImageOnFail(R.drawable.transparent)
//			.showImageOnLoading(R.drawable.transparent)
//			.resetViewBeforeLoading(true)
//			.cacheInMemory(true)
//			.cacheOnDisk(true)
//			.build();
//
//	public static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS = new DisplayImageOptions.Builder()
//			.showImageForEmptyUri(R.drawable.image_loading_placeholder)
//			.showImageOnFail(R.drawable.image_loading_placeholder)
//			.showImageOnLoading(R.drawable.image_loading_placeholder)
//			.resetViewBeforeLoading(true)
//			.cacheInMemory(true)
//			.cacheOnDisk(true)
//			.build();

	/**
	 * 更多界面,GridLayoutManager的列数
	 */
	public static final int COLUMN_COUNT = 3;

	/**
	 * 免打扰时间
	 */
	public static final int DEFAULT_START = 23;
	public static final int DEFAULT_END = 7;

	/**
	 * 已读新闻链接
	 */
	public static final HashSet<String> NEWS_URLS = new HashSet<>(16);
	public static final int COLOR_READ = 0xb2000000;

	/**
	 * 通知关键词
	 */
	public interface NotifyKey {
		// 闹钟定时
		String UPDATE_TIME = "updateTime";
		String GIFT_NUMBER = "gift_number";
		String SUBJECT_NUMBER = "subject_number";
		String GAME_NUMBER = "game_number";
		String APP_NUMBER = "app_number";
		String NEWS_NUMBER = "news_number";
	}

	/**
	 * 更新时间格式
	 */
	public static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy.MM.dd", Locale.getDefault());
	/**
	 * 全局上下文
	 */
//	public static final Context ROOT_CONTEXT = PromotionApplication.CONTEXT;

	/**
	 * GooglePlay包名
	 */
	public static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";
	/**
	 * GooglePlay地址http前缀
	 */
	public static final String GOOGLE_PLAY_PREFFIX_HTTP = "http://play.google.com/store/apps/details?id=";
	/**
	 * GooglePlay地址https前缀
	 */
	public static final String GOOGLE_PLAY_PREFFIX_HTTPS = "https://play.google.com/store/apps/details?id=";
	/**
	 * GooglePlay地址market前缀
	 */
	public static final String GOOGLE_PLAY_PREFFIX_MARKET = "market://details?id=";
	/**
	 * GooglePlay地址market search前缀
	 */
	public static final String GOOGLE_PLAY_PREFFIX_SEARCH = "market://search?q=";

	/**
	 * YouTube包名
	 */
	public static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
	/**
	 * YouTube地址前缀
	 */
	public static final String YOUTUBE_PREFFIX_NORMAL = "https://www.youtube.com/watch?v=";
	/**
	 * YouTube地址前缀
	 */
	public static final String YOUTUBE_PREFFIX_SHORT = "https://youtu.be/";
	/**
	 * YouTube地址前缀
	 */
	public static final String YOUTUBE_PREFFIX_EMBED = "https://www.youtube.com/embed/";

	public static final class RequestParam {
		/**
		 * 用户设备唯一标识，如00000000-54b3-e7c7-0000-000046bffd97
		 */
		public static final String UUID = "uuid";
		/**
		 * 语言代码，如en、zh
		 */
		public static final String LAUGUAGE = "language";
		/**
		 * 国家代码，如CN
		 */
		public static final String COUNTRY = "country";
		/**
		 * 屏幕分辨率，如720*1080
		 */
		public static final String SCREEN_RESOLUTION = "screen_type";
		/**
		 * 设备制造商名称，如Xiaomi
		 */
		public static final String MANUFACTURE = "manufacture";
		/**
		 * 设备型号，如MI 1S
		 */
		public static final String MODEL = "model";
		/**
		 * 系统版本号，如21（代表Android5.0）
		 */
		public static final String OS_VERSION = "android_version";
		/**
		 * 手机SIM卡运营商
		 */
		public static final String SIM_OPERATOR = "operator";
		/**
		 * 手机卡IMSI
		 */
		public static final String IMSI = "imsi";
		/**
		 * 手机卡IMEI
		 */
		public static final String IMEI = "imei";
		/**
		 * Android ID
		 */
		public static final String ANDROID_ID = "android_id";
		/**
		 * 本机是否安装了GooglePlay
		 */
		public static final String HAS_GOOGLE_MARKET = "has_google_market";
		/**
		 * 版本号
		 */
		public static final String VERSION_CODE = "ver_code";
		/**
		 * 要请求的数据页数
		 */
		public static final String PAGE = "p";
		/**
		 * 礼包id
		 */
		public static final String GIFT_ID = "id";
		/**
		 * 应用包名
		 */
		public static final String PACKAGE_NAME = "packageName";
		/**
		 * 当前应用自身包名
		 */
		public static final String PACKAGE_NAME_SELF = "packageNameSelf";
		/**
		 * 礼包对应的游戏计费点
		 */
		public static final String GIFT_GAME_CHARGE_POINT = "chargePoint";
		/**
		 * 上一次领取的兑换码
		 */
		public static final String GIFT_LAST_GIFT_CODE = "code";
		/**
		 * xml模板最后更新时间
		 */
		public static final String LAYOUT_TEMPLATE_UPDATE_TIME = "templateUpdateTime";
		/**
		 * 取消赞和踩
		 */
		public static final String CANCEL = "cancel";
		/**
		 * 专题id
		 */
		public static final String SUBJECT_ID = "id";
		/**
		 * Banner详情id
		 */
		public static final String BANNER_DETAIL_ID = "id";
		/**
		 * 应用或游戏的分类id
		 */
		public static final String CATEGORY_ID = "cid";
		/**
		 * 搜索关键词
		 */
		public static final String KEYWORDS = "keywords";
		/**
		 * 用户反馈
		 */
		public static final String FEEDBACK = "feedback";
		/**
		 * 邮箱
		 */
		public static final String EMAIL = "email";
		/**
		 * 是否是新闻
		 */
		public static final String IS_NEWS = "is_news";
		/**
		 * 是否是图片
		 */
		public static final String IS_IMAGES = "is_images";
		/**
		 * 图片类型
		 */
		public static final String IMAGE_TYPE = "type";
		/** 排序 */
		public static final String SORT = "sort";
		/** 是否是ROM版 */
		public static final String IS_ROM = "is_rom";
		/** 是否是系统应用 */
		public static final String IS_SYSTEM_APP = "is_system_app";
		/** 是否获取了静默安装权限 */
		public static final String CAN_SILENT = "canSilent";
	}

	public static final class ParamValue {
		/**
		 * 图片类型为gif
		 */
		public static final String GIFS = "gifs";
		/**
		 * 图片类型为图片
		 */
		public static final String IMAGES = "is_images";
		/** 按下载量排序 */
		public static final String DOWNLOAD = "download";
		/** 按评分排序 */
		public static final String RATE = "rate";
	}

	public static final class JsonField {
		/**
		 * 状态码
		 */
		public static final String STATUS = "status";
		/**
		 * 说明
		 */
		public static final String MSG = "msg";
		/**
		 * 是否还有下一页数据
		 */
		public static final String HAS_NEXT_PAGE = "hasNextPage";
		/**
		 * 数据
		 */
		public static final String DATA = "data";
		/**
		 * 搜索关键词
		 */
		public static final String KEYWORDS = "keywords";
		/**
		 * 专题标题
		 */
		public static final String SUBJECT_TITLE = "subject_title";
		/**
		 * 专题id
		 */
		public static final String SUBJECT_ID = "subject_id";
		/**
		 * 是否是新闻
		 */
		public static final String IS_NEWS = "is_news";
		/**
		 * 是否是图片
		 */
		public static final String IS_IMAGES = "is_images";
		/**
		 * 横幅推广
		 */
		public static final String BANNER = "banner";
		/**
		 * 默认搜索关键词
		 */
		public static final String DEFAULT_SEARCH_WORD = "defaultSearchWord";
		/**
		 * 标题
		 */
		public static final String TITLE = "title";
		/**
		 * 空字符串
		 */
		public static final String NULL = "null";

		public static final class News {
			public static final String LOGO = "logo";
			public static final String TITLE = "title";
			public static final String SOURCE = "source";
			public static final String ADDED_TIME = "added_time";
			public static final String TAG = "tag";
			public static final String IMAGES = "images";
			public static final String URL = "url";
			public static final String WIDTH = "width";
			public static final String HEIGHT = "height";
		}

		public static final class Banner {
			public static final String ICON_URL = "iconUrl";
			public static final String IMG_WIDTH = "imgWidth";
			public static final String IMG_HEIGHT = "imgHeight";
			public static final String PROCESS_TYPE = "processType";
			public static final String URL = "imageUrl";
			public static final String BANNER_DETAIL_ID = "id";
			public static final String GIFT_ID = "giftId";
			public static final String PACKAGE_NAME = "packageName";
			public static final String TITLE = "title";
			public static final String BG_COLOR = "bgColor";
			public static final String IMAGE_URL = "imageUrl";
			public static final String SPREAD_ID = "spreadId";
			public static final String SUBJECT_ID = "subject_id";
		}

		public static final class DynamicLayout {
			/**
			 * layout xml模板数组字段
			 */
			public static final String TEMPLATE = "template";
			/**
			 * xml模板最后更新时间
			 */
			public static final String LAYOUT_TEMPLATE_UPDATE_TIME = "templateUpdateTime";
			/**
			 * 与View无直接关系的数据字段
			 */
			public static final String EXTRA_DATA = "extraData";
			/**
			 * 动态item的数据
			 */
			public static final String VIEWS = "view";
			/**
			 * 模板类型
			 */
			public static final String XML_TYPE = "xmlType";
			/**
			 * 图片宽度
			 */
			public static final String IMAGE_WIDTH = "imgWidth";
			/**
			 * 图片高度
			 */
			public static final String IMAGE_HEIGHT = "imgHeight";
			/**
			 * 专题id
			 */
			public static final String SUBJECT_ID = "spreadId";
			/**
			 * 礼包id数组
			 */
			public static final String GIFT_IDS = "giftIds";
			/**
			 * 包名
			 */
			public static final String PACKAGE_NAME = "packageName";
			/**
			 * 礼包数组
			 */
			public static final String GIFTS = "gifts";
			/**
			 * 礼包id
			 */
			public static final String GIFT_ID = "giftId";
			/**
			 * 礼包名称
			 */
			public static final String GIFT_NAME = "giftName";
			/**
			 * 横幅推广
			 */
			public static final String BANNERS = "banners";
			/**
			 * 网址
			 */
			public static final String URL = "url";
			/**
			 * 标题
			 */
			public static final String TITLE = "title";
			/**
			 * GooglePlay网址
			 */
			public static final String MARKET_URL = "market_url";
			/**
			 * 商店包名
			 */
			public static final String STORE_PACKAGE_NAME = "storePackageName";
			/**
			 * 图片数组
			 */
			public static final String IMAGES = "images";
			/**
			 * 图片地址
			 */
			public static final String IMAGE_URL = "imageUrl";
		}

		/**
		 * 动态加载xml模板
		 */
		public static final class ViewInterface {
			/**
			 * 跳转逻辑
			 */
			public static final String PROCESS_TYPE = "processType";
			/**
			 * 文本内容
			 */
			public static final String TEXT = "text";
			/**
			 * 图标地址
			 */
			public static final String ICON = "text";
			/**
			 * <<下载>>按钮图片地址
			 */
			public static final String DOWN_ICON_URL = "downIconUrl";
			/**
			 * <<打开>>按钮图片地址
			 */
			public static final String OPEN_ICON_URL = "openIconUrl";
		}

		public static final class AppCategory {
			/**
			 * 应用分类
			 */
			public static final String APP_CATEGORIES = "Category";
			/**
			 * 分类唯一标识
			 */
			public static final String ID = "categoryId";
			/**
			 * 分类名称
			 */
			public static final String NAME = "typeName";
			/**
			 * 分类图标
			 */
			public static final String ICON_URL = "iconUrl";
		}

		public static final class AppAbout {
			public static final String PACKAGE_NAME = "packageName";
			public static final String NAME = "name";
			public static final String ICON_URL = "iconUrl";
			public static final String MARKET_URL = "marketUrl";
			public static final String VERSION_NAME = "versionName";
			public static final String DOWNLOAD_COUNT = "downloadCount";
			public static final String IS_FREE = "isFree";
			public static final String LAST_UPDATE_TIME = "lastUpdateTime";
			public static final String RATE = "rate";
			public static final String SIZE = "size";
		}

		public static final class AppDetail {
			public static final String PACKAGE_NAME = "packageName";
			public static final String NAME = "name";
			public static final String ICON_URL = "iconUrl";
			public static final String MARKET_URL = "marketUrl";
			public static final String VERSION_NAME = "versionName";
			public static final String DOWNLOAD_COUNT = "downloadCount";
			public static final String LAST_UPDATE_TIME = "lastUpdateTime";
			public static final String RATE = "rate";
			public static final String SIZE = "size";

			public static final String SCREENSHOT_WIDTH = "screenshotWidth";
			public static final String SCREENSHOT_HEIGHT = "screenshotHeight";
			public static final String SCREENSHOT_URLS = "screenshotUrls";
			public static final String HAS_GIFT = "haveGift";
			public static final String GIFT_ID = "giftId";
			public static final String PRAISE_COUNT = "praiseCount";
			public static final String TREAD_COUNT = "treadCount";
			public static final String DESCRIPTION = "description";
			public static final String IS_FREE = "isFree";
			public static final String PAYMENT_AMOUNT = "paymentAmount";
			public static final String COMMENTS = "comments";
			public static final String RECOMMENDATION = "recommendation";
			public static final String USER_NAME = "userName";
			public static final String COMMENT_CONTENT = "commentContent";
			public static final String COMMENT_CONNTS = "commentCounts";
			public static final String RATE_SCORE = "rateScore";
		}

		public static final class App {
			public static final String ID = "id";
			public static final String RATING_NUM = "rating_num";
			public static final String DOWNLOAD_NUM = "download_num";
			public static final String NAME = "name";
			public static final String PACKAGE_NAME = "pkg_name";
			public static final String MARKET_URL = "market_url";
			public static final String ICON_URL = "icon_url";
		}

		public static final class Gift {
			public static final String ID = "id";
			public static final String GIFT_ID = "giftId";
			public static final String GID = "gid";
			public static final String GIFT_NAME = "giftName";
			public static final String GIFT_DESC = "giftDesc";
			public static final String GIFT_USAGE_DESC = "giftUsageDesc";
			public static final String GIFT_IMAGE = "image";
			public static final String GIFT_IMAGE_WIDTH = "imgWidth";
			public static final String GIFT_IMAGE_HEIGHT = "imgHeight";
			public static final String GAME_NAME = "name";
			public static final String GAME_ICON_URL = "icon_url";
			public static final String GAME_PACKAGE_NAME = "pkg_name";
			public static final String PACKAGE_NAME = "packageName";
			public static final String GAME_MARKET_URL = "market_url";
			public static final String GAME_INSTALL_COUNT = "download_num";
			public static final String GAME_RATING_SCORE = "rating_num";
			public static final String GIFT_CODE = "code";
			public static final String GAME_PRICE = "price";
			public static final String NEXT_GIFT_TIME = "nextTime";
			public static final String OTHER_GIFT = "gift";
			public static final String OTHER_GIFT_NAME = "name";
			public static final String GAME_CHARGE_POINT = "chargePoint";
			public static final String UPDATE_TIME = "updateTime";
			public static final String ACQUIRE_TIME = "acquireTime";
			public static final String GIFT_TAG = "giftTag";
			public static final String GIFT_ICON_URL = "giftIconUrl";
			public static final String IS_HOT = "isHot";
			public static final String IS_NEW = "isNew";
		}

		public static final class Subject {
			public static final String TITLE = "title";
			public static final String DESCRIPTION = "description";
			public static final String ADDED_TIME = "added_time";
		}

		public static final class Notice {
			/**
			 * 本应用更新通知
			 */
			public static final String APP_UPDATE = "appUpdate";
			/**
			 * 本应用是否有更新
			 */
			public static final String HAS_NEW = "hasNew";
			/**
			 * 本应用是否强制更新
			 */
			public static final String FORCING = "forcing";
			/**
			 * 本应用的最新版本 数字
			 */
			public static final String VERSION_CODE = "versionCode";
			/**
			 * 本应用更新,对话框提示内容
			 */
			public static final String DIALOG_CONTENT = "dialogContent";
			/**
			 * 本应用更新,服务器下载地址
			 */
			public static final String DOWNLOAD_URL = "downloadUrl";
			/**
			 * 更新通知
			 */
			public static final String NOTICE = "notice";
			/**
			 * 本应用更新通知
			 */
			public static final String TYPE_SELF = "self";
			/**
			 * 活动消息通知
			 */
			public static final String TYPE_MESSAGE = "message";
			/**
			 * 新闻通知
			 */
			public static final String TYPE_NEWS = "news";
			/**
			 * 游戏通知
			 */
			public static final String TYPE_GAME = "game";
			/**
			 * 应用通知
			 */
			public static final String TYPE_APP = "app";
			/**
			 * 礼包通知
			 */
			public static final String TYPE_GIFT = "gift";
			/**
			 * 专题通知
			 */
			public static final String TYPE_SUBJECT = "subject";
			/**
			 * id
			 */
			public static final String ID = "id";
			/**
			 * id
			 */
			public static final String SUBJECT_TYPE = "subjectType";
			/**
			 * 标题
			 */
			public static final String TITLE = "title";
			/**
			 * 内容
			 */
			public static final String CONTENT = "content";
			/**
			 * 图片地址
			 */
			public static final String IMAGE_URL = "imageUrl";
			/**
			 * 新闻地址
			 */
			public static final String NEWS_URL = "newsUrl";
			/**
			 * 礼包-游戏包名
			 */
			public static final String PKG_NAME = "pkg_name";

			/**
			 * 更新标志
			 */
			public static final String UPDATE = "update";
			/**
			 * 精选更新标志
			 */
			public static final String DAYILY = "dayily";
			/**
			 * 礼包更新标志
			 */
			public static final String GIFT = "gift";
			/**
			 * 专题更新标志
			 */
			public static final String SPREAD = "spread";
			/**
			 * 新闻更新标志
			 */
			public static final String NEWS = "news";
			/**
			 * 应用更新标志
			 */
			public static final String APP = "app";
			/**
			 * 游戏更新标志
			 */
			public static final String GAME = "game";
		}

		public static final class GameGiftList {
			/**
			 * 游戏图标地址
			 */
			public static final String ICON_URL = "app_icon";
			/**
			 * 游戏名称
			 */
			public static final String GAME_NAME = "app_name";
			/**
			 * 礼包数组
			 */
			public static final String GIFTS = "gifts";
			/**
			 * 游戏截图
			 */
			public static final String SCREENSHOTS = "screenshots";
		}
	}

	public static final class JsonFieldValue {
		public static final String EXPAND = "expand";
		public static final String COLLAPSE = "collapse";

		/**
		 * 请求后返回的状态码的取值
		 */
		public static final class Status {
			/**
			 * 请求成功
			 */
			public static final int SUCCESS = 1;
			/**
			 * 没有数据
			 */
			public static final int FAILED = -1;
		}
	}

	public static final class Url {
		/**
		 * 根域名
		 */
		private static final String ROOT_URL = "http://play-data.locktheworld.com/";
//		private static final String ROOT_URL = "http://10.80.3.186/";
		/**
		 * 新的获取通知的接口 //  [ROOT_URL + "Apps_Update/announce"]
		 * ["http://api.locktheworld.com/custom/game_new.php" ]
		 */
		public static final String UPDATE_NEW = ROOT_URL + "Apps_Update/announce";
		/**
		 * 定点检查更新的接口 6:00 12:00
		 */
		public static final String UPDATE_NEW_TIME = ROOT_URL + "Apps_Update/timer";
		/**
		 * 检查更新的接口
		 */
		public static final String UPDATE = ROOT_URL + "Apps_Update";
		/**
		 * 收藏的接口
		 */
		public static final String COLLECT = ROOT_URL + "Apps_Update/addFavorite";
		/**
		 * 领取礼包的接口
		 */
		public static final String GET_GIFT_CODE = ROOT_URL
				+ "Apps_Gifts/getCode";
		/**
		 * 获取精选列表
		 */
//		public static final String URL_CHOICE = "http://api.locktheworld.com/custom/choice.php";
//		public static final String URL_CHOICE = ROOT_URL + "Apps_Dayily";
		public static final String URL_CHOICE = ROOT_URL + "Apps_Spreaddayily";
		/**
		 * 在线礼包列表
		 */
		public static final String ONLINE_GIFT = ROOT_URL + "Apps_Gifts";
		/**
		 * 本地礼包列表
		 */
		public static final String LOCAL_GIFT = ROOT_URL
				+ "Apps_Gifts/localGift";
		/**
		 * 某个游戏的所有礼包
		 */
		public static final String GAME_GIFTS = ROOT_URL
				+ "Apps_Gifts/localGiftDetail";
		/**
		 * 获取礼包详情
		 */
		public static final String GIFT_DETAIL = ROOT_URL
				+ "Apps_Gifts/GiftDetail";
		/**
		 * 获取游戏列表
		 */
		public static final String URL_GAME_ALL = ROOT_URL + "Apps_Games";
		/**
		 * 获取应用列表
		 */
		public static final String URL_APP_ALL = ROOT_URL + "Apps_Apps";
		/**
		 * 获取专题列表
		 */
		public static final String URL_SUBJECT = ROOT_URL + "Apps_Spread";
		/**
		 * 获取应用详情
		 */
		public static final String URL_APP_DETAIL = ROOT_URL
				+ "Apps_Apps/appDetail";
		/**
		 * 获取专题详情
		 */
		public static final String URL_SUBJECT_DETAIL = ROOT_URL
				+ "Apps_Spread/spreadDetail";
		/**
		 * 获取Banner详情
		 */
		public static final String URL_BANNER_DETAIL = ROOT_URL
				+ "Apps_Spread/bannerDetail";
		/**
		 * 赞应用
		 */
		public static final String URL_PRAISE = ROOT_URL + "Apps_Like/like";
		/**
		 * 踩应用
		 */
		public static final String URL_TREAD = ROOT_URL + "Apps_Like/hate";
		/**
		 * 最新游戏
		 */
		public static final String GAME_NEW = ROOT_URL + "Apps_Games";
		/**
		 * 游戏下载排行
		 */
		public static final String GAME_DOWNLOAD = ROOT_URL
				+ "Apps_Games/download";
		/**
		 * 游戏评分排行
		 */
		public static final String GAME_RATE = ROOT_URL + "Apps_Games/score";
		/**
		 * 最新应用
		 */
		public static final String APP_NEW = ROOT_URL + "Apps_Apps";
		/**
		 * 应用下载排行
		 */
		public static final String APP_DOWNLOAD = ROOT_URL + "Apps_Apps/download";
		/**
		 * 应用评分排行
		 */
		public static final String APP_RATE = ROOT_URL + "Apps_Apps/score";
		/**
		 * 关键词接口
		 */
		public static final String APP_SEARCH_KEYS = ROOT_URL + "Apps_Update/sendKeywords";
		/**
		 * 搜索应用接口
		 */
		public static final String APP_SEARCH = ROOT_URL + "Apps_Apps/searchApp";
		/**
		 * 用户反馈接口
		 */
		public static final String FEEDBACK = ROOT_URL + "Apps_Update/feedback";
		/**
		 * 推荐带礼包游戏接口
		 */
		public static final String LOCAL_GIFT_GAME = ROOT_URL + "Apps_Gifts/getCircleGift";
		/**
		 * 随机2个礼包接口
		 */
		public static final String GUIDE_GIFT = ROOT_URL + "Apps_Gifts/getRandGift";
		/**
		 * ROM版本更新
		 */
		public static final String ROM_UPDATE = ROOT_URL + "Apps_Update/romUpdate";
	}

	public static final class Action {
		/**
		 * 应用详情页
		 */
//		public static final String ACTION_APP_DETAIL = AppDetailActivity.ACTION_APP_DETAIL;
		/**
		 * 专题详情页
		 */
//		public static final String ACTION_SUBJECT_DETAIL = SubjectDetailActivity.ACTION_ENTER_SUBJECT_DETAIL_PAGE;
		/**
		 * 游戏中兑换礼包的页面动作
		 */
		public static final String JOY_SDK_INTENT_ACTION_FREE_KEY = "joy.sdk.intent.action.free.key";
//		/**
//		 * 主页动作
//		 */
//		public static final String ACTION_GIFT_PROMOTION = MainActivity.ACTION_MAIN;
//		/**
//		 * 进入礼包详情页
//		 */
//		public static final String ACTION_GIFT_DETAIL = GiftDetailActivity.ACTION_GIFT_DETAIL;
//		/**
//		 * 某个游戏的所有礼包列表
//		 */
//		public static final String ACTION_GAME_GIFTS = GameGiftListActivity.ACTION_GAME_GIFTS;
	}

	public static final class Bucket {
		public static final String MY_GIFT = "MY_GIFT";

	}

	public static final class PrefName {
		/**
		 * 布局xml模板相关
		 */
		public static final String LAYOUT_XML = "LAYOUT_XML";
		/**
		 * 通知相关
		 */
		public static final String NOTIFICATION = "NOTIFICATION";
		/**
		 * 默认
		 */
		public static final String MAIN = "MAIN";

	}

	public static final class PrefKey {
		/**
		 * 是否第一次启动
		 */
		public static final String FIRST_LAUNCHER = "first_launcher";
		/**
		 * 定义请求服务器时间间隔和时间点
		 */
		public static final String TIME_APP_UPDATE = "updateTime";
		/**
		 * 上次弹出更新对话框的时间 month;day
		 */
		public static final String NOTIFY_TIME_APP_UPDATE = "NOTIFY_TIME_APP_UPDATE";
		/**
		 * 通知弹出次数：用户从未领取过礼包，通知用户去领取礼包兑换码
		 */
		public static final String NOTIFY_TIME_GET_GIFT = "NOTIFY_TIME_GET_GIFT";
		/**
		 * 通知弹出次数：通知用户有新的礼包数据
		 */
		public static final String NOTIFY_TIME_NEW_GIFT_DATA = "NOTIFY_TIME_NEW_GIFT_DATA";
		/**
		 * 最新的礼包数据MD5值
		 */
		public static final String NEWEST_GIFT_DATA_MD5 = "NEWEST_GIFT_DATA_MD5";
		/**
		 * 是否领取过礼包
		 */
		public static final String HAS_GOT_GIFT = "HAS_GOT_GIFT";
		/**
		 * 应用分类
		 */
		public static final String APP_CATEGORIES = "APP_CATEGORIES";
		/**
		 * 游戏分类
		 */
		public static final String GAME_CATEGORIES = "GAME_CATEGORIES";
		/**
		 * 当前应用分类
		 */
		public static final String CURRENT_APP_CATEGORY = "CURRENT_APP_CATEGORY";
		/**
		 * 当前游戏分类
		 */
		public static final String CURRENT_GAME_CATEGORY = "CURRENT_GAME_CATEGORY";
		/**
		 * 表示服务器当前时间
		 */
		public static final String SERVER_CURRENT_TIME = "currentTime";
		/**
		 * 定点更新表示手机当前时间
		 */
		public static final String CURRENT_TIME = "mt";

		/**
		 * 礼包更新时间
		 */
		public static final String MESSAGE_UPDATE_TIME = "messageUpdateTime";
		/**
		 * 礼包更新时间
		 */
		public static final String GIFT_UPDATE_TIME = "giftUpdateTime";
		/**
		 * 专题更新时间
		 */
		public static final String SPREAD_UPDATE_TIME = "spreadUpdateTime";
		/**
		 * 精选更新时间
		 */
		public static final String DAYILY_UPDATE_TIME = "dayilyUpdateTime";
		/**
		 * 新闻更新时间
		 */
		public static final String NEWS_UPDATE_TIME = "articleUpdateTime";
		/**
		 * 应用更新时间
		 */
		public static final String APP_UPDATE_TIME = "appUpdateTime";
		/**
		 * 游戏更新时间
		 */
		public static final String GAME_UPDATE_TIME = "gameUpdateTime";
		/**
		 * 用户反馈之邮箱地址
		 */
		public static final String FEEDBACK_EMAIL = "FEEDBACK_EMAIL";
		/**
		 * 用户反馈之用户意见
		 */
		public static final String FEEDBACK_COMMENT = "FEEDBACK_COMMENT";
		/**
		 * 搜索历史记录
		 */
		public static final String SEARCH_HISTORY = "SEARCH_HISTORY";
		/**
		 * 默认搜索关键词
		 */
		public static final String DEFAULT_SEARCH_WORD = "DEFAULT_SEARCH_WORD";

		public static final class LayoutTemplate {
			/**
			 * 布局xml模板最后更新时间
			 */
			public static final String LAST_UPDATE_TIME = "LAST_UPDATE_TIME";
		}

		public static final class Setting {
			/**
			 * 开启推送
			 */
			public static final String PUSH_OPEN = "push_open";
			/**
			 * 专题推荐
			 */
			public static final String CHOICE_PROMOTION = "choice_promotion";
			/**
			 * 游戏礼包
			 */
			public static final String GAME_GIFT = "push_gift";
			/**
			 * 新闻
			 */
			public static final String NEWS = "push_news";
			/**
			 * 游戏
			 */
			public static final String GAME = "push_game";
			/**
			 * 应用
			 */
			public static final String APP = "push_app";
			/**
			 * 提示音
			 */
			public static final String VOICE = "voice";
			/**
			 * 免打扰
			 */
			public static final String NONE_NOTICE = "none_notice";
			/**
			 * 免打扰开始时间(小时)
			 */
			public static final String NONE_START_HOURS = "none_start_hours";
			/**
			 * 免打扰开始时间(分钟)
			 */
			public static final String NONE_START_MINUTES = "none_start_minutes";
			/**
			 * 免打扰结束时间(小时)
			 */
			public static final String NONE_END_HOURS = "none_end_hours";
			/**
			 * 免打扰结束时间(分钟)
			 */
			public static final String NONE_END_MINUTES = "none_end_minutes";
		}
	}

	public static final class AlarmActionType {
		/**
		 * 用户从未领取过礼包，通知用户去领取礼包兑换码
		 */
		public static final int NOTIFY_GET_GIFT = 1;
		/**
		 * 通知用户礼包倒计时完成，可以领取下一个礼包兑换码了
		 */
		public static final int NOTIFY_TIME_COUNT_DOWN = 2;
		/**
		 * 通知用户去领取倒计时结束后未领取的礼包兑换码
		 */
		public static final int NOTIFY_GET_NEW_GIFT = 4;
		/**
		 * 检查更新和通知
		 */
		public static final int NOTIFY_CHECK_UPDATE = 9;
		/**
		 * 定点通知
		 */
		public static final int NOTIFY_CHECK_UPDATE_TIME = 109;
	}

	public static final class ExtraParam {
		/**
		 * 额外参数：来自哪里
		 */
		public static final String FROM_WHERE = "FROM_WHERE";
		/**
		 * 额外参数：礼包
		 */
//		public static String GIFT = GiftDetailFragment.EXTRA_GIFT;
//		/**
//		 * 额外参数：礼包id
//		 */
//		public static String GIFT_ID = GiftDetailFragment.EXTRA_GIFT_ID;
//		/**
//		 * 额外参数：应用包名
//		 */
//		public static String GAME_PACKAGE_NAME = GiftDetailFragment.EXTRA_GAME_PACKAGE_NAME;
//		/**
//		 * 额外参数：游戏计费点
//		 */
//		public static String GAME_CHARGE_CODE = GiftDetailFragment.EXTRA_GAME_CHARGE_CODE;
//		/**
//		 * 额外参数：游戏计费点
//		 */
//		public static String GIFT_CODE = GiftDetailFragment.EXTRA_GIFT_CODE;
	}

	public static final class ViewAttr {
		public static final String BACKGROUND_COLOR = "backgroundColor";
		public static final String BACKGROUND_COLOR_CLICK = "backgroundColor_click";
		public static final String IS_ICON = "is_icon";

		public static final class CardView {
			public static final String cardBackgroundColor = "cardBackgroundColor";
			public static final String cardCornerRadius = "cardCornerRadius";
			public static final String cardElevation = "cardElevation";
			public static final String cardMaxElevation = "cardMaxElevation";
		}

		public static final class FloatingActionButton {
			public static final String shadowRadius = "shadowRadius";
			public static final String shadowDx = "shadowDx";
			public static final String shadowDy = "shadowDy";
			public static final String shadowColor = "shadowColor";
			public static final String drawable = "drawable";
			public static final String fab_color = "fab_color";
		}

		public static final class RippleView {
			public static final String rippleColor = "rippleColor";
			public static final String rippleDimension = "rippleDimension";
			public static final String rippleOverlay = "rippleOverlay";
			public static final String rippleAlpha = "rippleAlpha";
			public static final String rippleDuration = "rippleDuration";
			public static final String rippleFadeDuration = "rippleFadeDuration";
			public static final String rippleHover = "rippleHover";
			public static final String rippleBackground = "rippleBackground";
			public static final String rippleDelayClick = "rippleDelayClick";
			public static final String ripplePersistent = "ripplePersistent";
			public static final String rippleInAdapter = "rippleInAdapter";
		}
	}

	public static final class AlarmTime {
		/**
		 * 数据更新时间
		 */
		public static final long UPDATE_TIME = 1000 * 60 * 60;
		/**
		 * 领取礼包提醒
		 */
		public static final long GIFT_TIME = 1 * 60 * 60 * 1000;
		/** 数据更新时间 */
	}

	public static final class FontPath {
		public static final String ROBOTO_MEDIUM = "fonts/Roboto-Medium.ttf";
		public static final String ROBOTO_REGULAR = "fonts/Roboto-Regular.ttf";
	}

//	public static final class Font {
//		public static final Typeface ROBOTO_MEDIUM = Typeface
//				.createFromAsset(PromotionApplication.CONTEXT.getAssets(),
//						FontPath.ROBOTO_MEDIUM);
//		public static final Typeface ROBOTO_REGULAR = Typeface.createFromAsset(
//				PromotionApplication.CONTEXT.getAssets(),
//				FontPath.ROBOTO_REGULAR);
//	}

	/**
	 * 字符串key分隔符
	 */
	public static String SPLIT = "##";
	/**
	 * 自动提示宽度
	 */
	public static float AUTO_WIDTH = 0.8f;
	/**
	 * 自动提示高度
	 */
	public static int AUTO_HEIGHT = -2;

}