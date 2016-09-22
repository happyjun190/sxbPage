package com.sxb.commons.constants;

import org.apache.commons.lang3.EnumUtils;


/**
 * redis常量，尽量采用“先定义，后使用”的原则。
 * 
 */
public class RedisConstants {
	/**
	 * Key的类型
	 */
	public enum KeyType {
		string, list, set, zset, hash;
		
		public static KeyType getKeyType(String type){
			return EnumUtils.getEnum(KeyType.class, type);
		}
	}
	
	/**
	 * 以秒为单位的时间常量。先定义，再使用，提高代码可读性
	 */
	public enum TimeInSecond {
		/** 5天 */
		_5_DAYS(5*24*3600),
		
		/** 24小时 */
		_24_HOURS(24*3600),
		
		/** 8小时 */
		_8_HOURS(8*3600),
		
		/** 120分钟 */
		_120_MINS(120*60),
		
		/** 10分钟 */
		_10_MINS(10*60),

		/** 5分钟 */
		_5_MINS(5*60),
		
		/** 无穷大 ≈68年 */
		INFINITE(Integer.MAX_VALUE),
		
		/** 不指定有效期，比如根据不同情况使用不同的有效期的情况、或其他不适用的情况 */
		NA(Integer.MIN_VALUE);
		
		TimeInSecond(int seconds){
			this.seconds = seconds;
		}
		
		public int val() {
			return seconds;
		}

		private int seconds;
	}
	
	/**
	 * Key的前缀：前缀String、类型、有效期（秒） 
	 * 类型如果不指定则默认为string
	 * 有效期如果不指定则默认为5天
	 */
	public enum Prefix {
		/** APP登录的token前缀，value=userId */
		APP_TOKEN("token.to.userid:"),
		
		/** WEB（PC采购）登录的token前缀，value=userId */
		WEB_TOKEN("web.token.to.userid:", TimeInSecond._24_HOURS),
		
		/** admin用户登录的token前缀 */
		ADMIN_TOKEN("ysb.admin.token:", TimeInSecond._8_HOURS),
		
		/** UserInfo信息 */
		USER_INFO("user.info:", KeyType.hash),
		
		/** 用户登录相关的信息，APP登录和WEB登录共用 */
		USER_LOGIN_INFO("userLoginInfo:", KeyType.hash, TimeInSecond.INFINITE),
		
		/** 验证码，Hash类型， 后面跟着cookieId */
		CAPTCHA("ysb.captcha:", KeyType.hash, TimeInSecond._5_MINS),
		
		/** 配送版的token */
		LS_TOKEN("token.to.userid.ysb-ls:"),
		
		/** 配送版的userInfo */
		LS_USER_INFO("user.info.ysb-ls:", KeyType.hash),
		
		/** 手机验证码 */
		PHONE_AUTHCODE("identifyCode:", TimeInSecond._10_MINS),
		
		/** 帮助问题点击量 */
		HELP_CLICK("ysb.helpclickset", KeyType.hash, TimeInSecond.INFINITE),
		
		/** 微信支付二维码CODEURL */
		WECHATPAY_CODEURL("web.nativewechat.codeurl.orderid:", TimeInSecond._120_MINS),
		
		/** 用户访问记录，防止频繁请求 ysb.req:<userId>.<method> */
		REQUEST("ysb.req:", TimeInSecond.NA),
		
		/** 连锁采购转账订单，push进去给PHP pop出来统一转账 */
		CHAIN_ORDER_YIBAO("ysb.admin.queue:chainyibao", KeyType.list, TimeInSecond.NA),
		
		/**某药店在某个限量特惠活动期间采购量，冒号后面跟着wholesaleId */
		DRUGSTOREAMOUNT_LIMITEDDEALS("{ysb.limiteddeals.}drugstoreamount:",KeyType.zset,TimeInSecond.NA),
		
		/** 访问限量特惠下单 用户记录，防止同一时间并发量过大，冒号后面跟着wholesaleId */
		USER_REQ_AMOUNT_LIMITEDDEALS("ysb.limiteddeals.userreqamount:", TimeInSecond.NA),
		
		/** 访问限量特惠下单数 也就是库存,冒号后面跟着wholesaleId */
		ORDERAMOUNT_LIMITEDDEALS("{ysb.limiteddeals.}orderamount:", TimeInSecond.NA),
		
		/** 某个用户的购物车数量，冒号后面跟着userId*/
		USER_BUYCAR_AMOUNT("ysb.buycar.mount:", TimeInSecond.NA),
		
		// === 分割线， 以下这些即将被废弃。新的请定义在上面，即将被废弃的请移到下面来： === 
		
		/** 店员版用户经度，有序集合： zadd(GPS_LNG, userId, longitude); */
		//GPS_LNG("ysb.gps.lng", KeyType.zset, TimeInSecond.NA),
		/** 店员版用户纬度，有序集合： zadd(GPS_LAT, userId, latitude); */
		//GPS_LAT("ysb.gps.lat", KeyType.zset, TimeInSecond.NA),
		
		/** 店员版用户的gps，hash集合，加上userId */
		//USER_GPS("ysbuser.gps:", KeyType.hash, TimeInSecond._24_HOURS),
		
		/** 大众版usertoken前缀（用于生成大众版usertoken，以通过大众版服务器接口拦截器，调用其接口）*/
		PUB_TOKEN("token.to.userid.pub:");
		
		
		
		
		/**
		 * 定义了前缀，缺省类型为string、有效期5天
		 */
		Prefix(String id){
			this.id = id;
			this.type = KeyType.string;
			this.ttl = TimeInSecond._5_DAYS;
		}

		/**
		 * 定义了前缀和有效期，缺省类型为string
		 */
		Prefix(String id, TimeInSecond ttl){
			this.id = id;
			this.type = KeyType.string;
			this.ttl = ttl;
		}
		
		/**
		 * 定义了前缀和类型，缺省有效期5天
		 */
		Prefix(String id, KeyType type){
			this.id = id;
			this.type = type;
			this.ttl = TimeInSecond._5_DAYS;
		}
		
		/**
		 * 定义了前缀、类型、和有效期
		 */
		Prefix(String id, KeyType type, TimeInSecond ttl){
			this.id = id;
			this.type = type;
			this.ttl = ttl;
		}

		public String id() {
			return id;
		}
		
		public KeyType type() {
			return type;
		}
		
		public int ttl() {
			return ttl.val();
		}

		@Override
		public String toString() {
			return id;
		}

		private String id;	//前缀字符串
		private KeyType type;	//类型
		private TimeInSecond ttl;	//过期时间（秒）
		
		
		public static Prefix findPrefixById(String prefixId){
			for(Prefix p : Prefix.values()){
				if(p.id().equals(prefixId)){
					return p;
				}
			}
			return null;
		}
	}
	
	/**
	 * 验证码hash中相关的field 
	 */
	public enum CapchaInfo {
		CODE,
		CHECKED;
	}
	
	/**
	 * UserInfo hash中相关的field
	 * 每个字段包括：id、过期时间（秒） 
	 * Key: Prefix.USER_INFO + userId
	 */
	public enum UserInfo {
		/** 用户中文名 */
		USER_NAME("uname"),
		
		/** 所属药店id */
		STORE_ID("storeId"),
		
		/** 所属药店的类型：0-连锁门店；1-零售单体；2-连锁总部 */
		STORE_TYPE("storeType"),
		
		/** 药采购控制地区审核状态：0-未审核（未申请） 1-已审核通过 2-审核不通过 3-待审核 */
		CON_STATUS("conStatus"),
		
		/** 所属药店的子类型：0-零售单体 1-第三终端 2-连锁直营 3-连锁加盟 4-连锁总部 5-商业公司 */
		STORE_SUBTYPE("storeSubType"),
		
		/** 月结购激活标志：0-不涉及月结购 1-待激活（已分配额度） 2-已激活  3-被禁用 */
		MONTH_PAY_STATUS("monthPayStatus"),
		
		/** 该用户在当前药店是否有采购记录：0-有（可以看到控销药的价格） 1-没有（看不到控销药的价格） */
		IS_RESTRICT("isRestrict"),
		
		/** 药店所属的区域id */
		AREA_ID("areaId"),
		
		/** 所属药店是否连锁总部 */
		//IS_CHAIN_HEAD("isChainHead"), //被STORE_TYPE代替
		
		/** 头像URL */
		HEAD_URL("headUrl"),
		
		/** 手机号码 */
		PHONE_NO("phoneNo"),
		
		/** 药店代表名字 */
		R_NAME("rName"),
		
		/** 药店代表电话号码*/
		R_PHONE("rPhone");
		
		/** 缺省的过期时间为5天 */
		UserInfo(String id){
			this.id = id;
			this.ttl = TimeInSecond._5_DAYS;
		}
		
		UserInfo(String id, TimeInSecond ttl){
			this.id = id;
			this.ttl = ttl;
		}

		public String id() {
			return id;
		}
		
		public int ttl() {
			return ttl.val();
		}
		
		@Override
		public String toString() {
			return id;
		}
		
		private String id;	//field String
		private TimeInSecond ttl;	//有效时间
	}
	
	public enum UserLoginInfo {
		/** APP登录后的token */
		APP_TOKEN("appToken"),
		/** APP最后登录时间  */
		APP_LOGIN_TIME("appLoginTime"),
		/** APP最后登录版本  */
		APP_VERSION("appVersion"),
		/** APP最后登录平台  */
		APP_PLATFORM("appPlatform"),

		/** Web登录后token */
		WEB_TOKEN("webToken"),
		/** WEB最后登录时间  */
		WEB_LOGIN_TIME("webLoginTime"),
		/** WEB最后登录版本  */
		WEB_VERSION("webVersion"),
		/** WEB最后登录平台  */
		WEB_PLATFORM("webPlatform");
		
		UserLoginInfo(String id){
			this.id = id;
		}
		
		public String id() {
			return id;
		}

		@Override
		public String toString() {
			return id;
		}
		
		private String id;	//field String
	}
	
	/**
	 * 配送版UserInfo hash中相关的field
	 * 每个字段包括：id、过期时间（秒） 
	 * Key: Prefix.LS_USER_INFO + userId
	 */
	public enum UserInfoLs {
		/** 登录后获取的token */
		TOKEN("userTokenYsb-ls", TimeInSecond._5_DAYS);
		
		UserInfoLs(String id, TimeInSecond ttl){
			this.id = id;
			this.ttl = ttl;
		}

		public String id() {
			return id;
		}
		
		public int ttl() {
			return ttl.val();
		}
		
		@Override
		public String toString() {
			return id;
		}
		
		private String id;	//field String
		private TimeInSecond ttl;	//有效时间
	}

	/**
	 * 用户GPS信息，hash field名字
	 * 过期时间和Prefix.USER_GPS相同
	 * key: Prefix.USER_GPS + userId
	 */
	public enum GpsInfo {
		/** 经度 */
		LNG("lng"),
		/** 维度 */
		LAT("lat"),
		/** 是否在店 */
		ISINSTORE("isInstore");
		
		GpsInfo(String id){
			this.id = id;
		}

		public String id() {
			return id;
		}
		
		@Override
		public String toString() {
			return id;
		}
		
		private String id;	//field String
	}
	
	/**
	 * 组装usertoken的前半部分
	 */
	//public static final String USERTOKENHEAD = "token.to.userid:";
	//public static final int USERTOKEN_EXPIRED = 5 * 24 * 3600; // 5天
	//public static final String USERTOKENHEAD_YSB_LS = "token.to.userid.ysb-ls:";// 店员版配送工具

	/**
	 * 大众版usertoken前缀（用于生成大众版usertoken，以通过大众版服务器接口拦截器，调用其接口）
	 */
	//public static final String USERTOKENHEAD_PUB = "token.to.userid.pub:";

	//public static final String PHONEHEAD = "identifyCode:";
	
	
	/**
	 * 记录ysbuserinfo信息
	 */
	//public static final String USER_INFO_YSB = "user.info:";// hash缓存的缓存key值user.info.ysb:#{userId}
	//public static final String USER_INFO_YSB_USERTOKEN = "usertoken";// hash缓存的hashkey值，存放usertoken
	//public static final String USER_INFO_YSB_LOGINTYPE = "logintype";//  登录类型(1-手机号码  2-邮箱  3-QQ号码  4-Q药网旧用户)
	//public static final String USER_INFO_YSB_LOGINTIME = "logintime";//  登录时间
	
	
	/**
	 * 记录LogisticUserinfo信息
	 */
	//public static final String USER_INFO_YSB_LS = "user.info.ysb-ls:";// hash缓存的缓存key值user.info.ysb-ls:#{userId}
	//public static final String USER_INFO_YSB_USERTOKEN_LS = "userTokenYsb-ls";// hash缓存的hashkey值，存放usertokenYsb-ls
	
	
	/**
	 * 短信验证码
	 */
	//public static final String PHONEHEAD = "identify.code.pub:";
	//public static final int PHONEHEAD_EXPIRED = 10 * 60; // 10分钟
	//public static final int PHONEHEAD_FROZEN_TIME = 30; // 30秒内不能再次请求
	
	/**
	 * 电信短信接口token
	 */
	//public static final String TELECOM_SMS_TOKEN_HEAD = "telecom.sms.token";

	/**
	 * 店员版用户经度
	 */
	//public static final String YSB_GPS_LNG = "ysb.gps.lng";
	/**
	 * 店员版用户纬度
	 */
	//public static final String YSB_GPS_LAT = "ysb.gps.lat";
	
	/**
	 * 店员版用户的gps，hash集合
	 */
	//public static final String YSBUSER_GPS = "ysbuser.gps:";// 加上userId
	//public static final String YSBUSER_GPS_LNG = "lng";// 经度
	//public static final String YSBUSER_GPS_LAT = "lat";// 纬度
	//public static final String YSBUSER_GPS_ISINSTORE = "isInstore";// 是否在店
	
	/** 用户访问记录，防止频繁请求 ysb.req:<userId>.<method> */
	//public static final String YSB_REQUEST = "ysb.req:";
	
	//public static final String YSB_CHAINYIBAO = "ysb.admin.queue:chainyibao";
	
	public enum Verifytype {
		/** 注册用户 */
		Regist(0),
		/** 发送验证码到旧绑定手机 */
		SendToOldPhone(1),
		/** 更换绑定手机*/
		ChangePhone(2),
		/** 体现 */
		WithDraw(3),
		/** 重置密码 */
		ResetPwd(4),
		/** 短信登录  */
		SmsLogin(5),
		/** 白条申请  */
		CreditpayApply(6),
		/** 职业药师注册  */
		cpaRegist(7);

		private int index;

		private Verifytype(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
		@Override
		public String toString() {
			return String.valueOf(index);
		}
	}

	
	//web登录相关：
	/** 验证码，Hash类型， 后面跟着cookie Id */
	//public static final String CAPTCHA = "ysb.captcha:";
	/** 验证码，field，验证码内容*/
	//public static final String CAPTCHA_CODE = "code";
	/** 验证码，field，验证码是否已经验证过 */
	//public static final String CAPTCHA_CHECKED = "checked";
	/** 验证码失效时间，分钟 */
	//public static final int CAPTCHA_EXPIRED = 5;
	
	/** web token 在redis中的前缀 */
	//public static final String WEB_TOKEN_PREFIX = "ysb.web.token:";
	
	/** web token 缺省过期时间（单位：秒）：24小时 */
	//public static final int WEB_TOKEN_EXPIRED = 24 * 3600;
	
	//管理登录相关
	/** admin token 在redis中的前缀 */
//	public static final String ADMIN_TOKEN_PREFIX = "ysb.admin.token:";
	
	/** admin token 缺省过期时间（单位：秒）：8小时，假设不加班 */
	//public static final int ADMIN_TOKEN_EXPIRED = 8 * 3600;
	
	
	// 帮助问题相关：
	/** 帮助问题点击量redis key值set集合 */
	//public static final String HELPCLICKSET = "ysb.helpclickset";
	
	//微信支付二维码CODEURL
	//public static final String WEB_NATIVEWECHAT_CODEURL = "web.nativewechat.codeurl.orderid:";
	//public static final int WEB_NATIVEWECHAT_CODEURL_EXPIRED = 120 * 60; // 120分钟
}
