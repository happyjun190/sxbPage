package com.sxb.commons.utils;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
 
 /**
 * 文件名：DateUtils.java 日期处理相关工具类
 */
public class DateUtils {
     
    /**定义常量**/
    public static final String DATE_JFP_STR="yyyyMM";
    public static final String DATE_JFP_STR2="yyyy年M月";
    public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_SMALL_STR = "yyyy-MM-dd";
    public static final String DATE_KEY_STR = "yyyyMMddHHmmss";
    public static final String DATE_MONTH_STR = "yyyy-MM";
    public static final String DATE_DAY_STR = "yyyyMMdd";
    public static final String DATE_FILE_STR = "yyyy/MM/dd";
    public static final String DATE_SMALL_STR_CN = "yyyy年MM月dd日";
    public static final String DATE_HOURMINUTE_STR = "HH:mm";
    public static final String DATE_WHOLESALE_TEHUI = "MM月dd日 HH:mm"; 
    public static final String DATE_WHOLESALE_TEHUI2 = "MM月dd日HH:mm"; 
    public static final String DATE_FULL_STR_2 = "yyyy-MM-dd HH:mm";
    public static final String DATE_YEARMOUTHHOURMINUTE_STR = "yyyy-MM-dd HH:mm";
    
    public static final String DATE_MMdd = "M月d日"; 
    public static final String DATE_MP_REPAYMENT = "M月dd日HH时mm分";
    public static final String DATE_MdHm = "M月d日H时m分"; 

    public static final ZoneId DEFAULT_ZONEID = ZoneId.systemDefault();
    /**
     * 使用预设格式提取字符串日期
     * @param strDate 日期字符串
     * @return
     */
    public static Date parse(String strDate) {
        return parse(strDate,DATE_FULL_STR);
    }
    
    /**
     * 使用用户格式提取字符串日期
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */
    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 两个时间比较
     * @param date
     * @return
     */
    public static int compareDateWithNow(Date date1){
        Date date2 = new Date();
        int rnum =date1.compareTo(date2);
        return rnum;
    }
     
    /**
     * 两个时间比较(时间戳比较)
     * @param date
     * @return
     */
    public static int compareDateWithNow(long date1){
        long date2 = dateToUnixTimestamp();
        if(date1>date2){
            return 1;
        }else if(date1<date2){
            return -1;
        }else{
            return 0;
        }
    }
     
 
    /**
     * 获取系统当前时间
     * @return
     */
    public static String getNowTime() {
    	return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FULL_STR));
//        SimpleDateFormat df = new SimpleDateFormat(DATE_FULL_STR);
//        return df.format(new Date());
    }
     
    /**
     * 获取系统当前时间
     * @return
     */
    public static String getNowTime(String type) {
    	return LocalDateTime.now().format(DateTimeFormatter.ofPattern(type));
//        SimpleDateFormat df = new SimpleDateFormat(type);
//        return df.format(new Date());
    }
     
    /**
     * 获取系统当前计费期
     * @return
     */
    public static String getJFPTime() {
    	return getNowTime(DATE_JFP_STR);
//        SimpleDateFormat df = new SimpleDateFormat(DATE_JFP_STR);
//        return df.format(new Date());
    }
    
    /**
     * 将指定的日期转换成Unix时间戳
     * @param String date 需要转换的日期 yyyy-MM-dd HH:mm:ss
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp(String date) {
        long timestamp = 0;
        try {
            timestamp = new SimpleDateFormat(DATE_FULL_STR).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
     
    /**
     * 将指定的日期转换成Unix时间戳
     * @param String date 需要转换的日期 yyyy-MM-dd
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp(String date, String dateFormat) {
        long timestamp = 0;
        try {
            timestamp = new SimpleDateFormat(dateFormat).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
     
    /**
     * 将当前日期转换成Unix时间戳
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp() {
    	return LocalDateTime.now().atZone(DEFAULT_ZONEID).toInstant().toEpochMilli();
//        long timestamp = new Date().getTime();
//        return timestamp;
    }
    
    /**
     * 将当前日期转换成Unix时间戳
     * @return String 时间戳
     */
    public static String dateToStringUnixTimestamp(){
    	return String.valueOf(dateToUnixTimestamp() / 1000);
//        return String.valueOf(new Date().getTime() / 1000);
    }
  
    /**
     * 将当前日期转换成Int类型，便于插入数据库中
     * @return int (长度10位)
     * @author zhangzhang
     */
     public static int dateToInt(){
    	return  (int) (dateToUnixTimestamp() / 1000);
//    	String strTime= new Date().getTime()+"";
//    	return Integer.parseInt(strTime.substring(0,10));
     }
     
    /**
     * 将Unix时间戳转换成日期
     * @param long timestamp 时间戳
     * @return String 日期字符串
     */
    public static Date unixTimestampToDate(long timestamp) {
        SimpleDateFormat sd = new SimpleDateFormat(DATE_FULL_STR);
        sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return new Date(timestamp);
    }
    
    /**
     * 日期转String
     * @return
     */
    public static String getStringTime(Date date,String type) {
        SimpleDateFormat df = new SimpleDateFormat(type);
        return df.format(date);
    }
    
    public static String getStringTime(long timestamp,String type){
    	if(timestamp == 0){
    		return "";
    	}
		long tt = (long)timestamp * 1000;
		Date date = new Date(tt);
		SimpleDateFormat df = new SimpleDateFormat(type);
		return df.format(date);
    }
    
    public static long calendarToUnixTimestamp(Date date,String type){
    	SimpleDateFormat df = new SimpleDateFormat(type);
    	String d = df.format(date);
    	return dateToUnixTimestamp(d, type);
    }
    /**
     * 根据时间返回月份
     * @param timestamp （10位数字）秒为单位
     * @return 月份
     */
    public static int getMonth(long timestamp){
    	return getZonedDateTimeBySecond(timestamp).getMonthValue();
//    	Calendar calendar = Calendar.getInstance();
//		Date date = DateUtils.unixTimestampToDate(timestamp);
//		long dateTime = date.getTime() * 1000;// 以毫秒为单位
//		date.setTime(dateTime);
//		calendar.setTime(date);
//    	return calendar.get(Calendar.MONTH) + 1;
    }
    /**
     * 根据时间返回月份
     * @param timestamp （10位数字）秒为单位
     * @return 年份
     */
    public static int getYear(long timestamp){
    	return getZonedDateTimeBySecond(timestamp).getYear();
//    	Calendar calendar = Calendar.getInstance();
//    	Date date = DateUtils.unixTimestampToDate(timestamp);
//    	long dateTime = date.getTime() * 1000;// 以毫秒为单位
//    	date.setTime(dateTime);
//    	calendar.setTime(date);
//    	return calendar.get(Calendar.YEAR) ;
    }
    
    public static int getDay(long timestamp){
    	return getZonedDateTimeBySecond(timestamp).getDayOfMonth();
//    	Date date = new Date(timestamp * 1000L);
//    	Calendar calendar = Calendar.getInstance();
//    	calendar.setTime(date);
//    	return calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    
    public static int getHour(long timestamp){
    	return getZonedDateTimeBySecond(timestamp).getHour();
//    	Calendar calendar = Calendar.getInstance();
//		Date date = DateUtils.unixTimestampToDate(timestamp);
//		long dateTime = date.getTime() * 1000;// 以毫秒为单位
//		date.setTime(dateTime);
//		calendar.setTime(date);
//    	return calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 获取给定分钟以后的时间
     * @param minute
     * @param stringTime //for example 20150522095524
     * @param type
     * @return
     */
    public static String getAfterMinuteTime(int minute,String stringTime,String type){
    	Date date = parse(stringTime,type);
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(Calendar.MINUTE, minute);
    	return getStringTime(calendar.getTime(),type);
    }
    
    /**
     * 转换不同格式的String 日期
     * @param strDate
     * @param fromPattern
     * @param toPattern
     * @return
     * @throws ParseException
     */
    public static String parse(String strDate,String fromPattern,String toPattern){
    	try{
    		SimpleDateFormat df = new SimpleDateFormat(fromPattern);
    		Date date = df.parse(strDate);
    		df = new SimpleDateFormat(toPattern);
    		return df.format(date);
    	}catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 获取两个时间的分钟差
     */
    public static int getIntervalMinutes(String startDate,String endDate){
		long startL = parse(startDate).getTime();
		long endL = parse(endDate).getTime();
		return (int) ((endL-startL)/(60*1000));
    }
    
    /**
     * 根据数据库的时间获取找药易的时间格式
     * 规则：规则： <1 min:刚刚；1min-60min:X分钟前；1h-24h: X小时前；>24h:YYYY-dd-mm）
     * @param time 数据库的时间（10位长度）
     * @return
     */
    public static String getEasyFindDateFormat(long time){
    	String result="";
    	int currentTime=(int) (System.currentTimeMillis()/1000);
		int flag=(int) (currentTime-time);
		int min=(int)flag/60;
		if(min<=1 && min >=0){
			result="刚刚";
		}else if(min<=60 && min>1){ 
			result=min+"分钟前";
		}else if(min >60 && min<= 24*60){
			result=(int)min/60+"小时前";
		}else {
			result= DateUtils.getStringTime(time, DateUtils.DATE_SMALL_STR);
			result=result.substring(5);
		}
		return result;
    }
    
    
    /**
	 * 获取后几天的时间戳23:59:59
	 */
	public static int getAfterDate(int day) {
		return (int) (LocalDate.now().plusDays(day).atTime(23, 59, 59).atZone(DEFAULT_ZONEID).toEpochSecond());
//		Date d = parse(getNowTime(DateUtils.DATE_DAY_STR+"235959"),DATE_KEY_STR);
//		Calendar c = Calendar.getInstance();
//		c.setTime(d);
//		c.add(Calendar.DATE, day);
//		Date beforeDate = c.getTime();
//		return (int)(beforeDate.getTime()/1000);
	}
	
	
	/**
	 * 获取后几天的时间戳23:59:59
	 */
	public static int getAfterMonth(Date date,int month) {
		LocalDateTime ldt = date.toInstant().atZone(DEFAULT_ZONEID).toLocalDateTime();;
		return (int) (ldt.plusMonths(month).atZone(DEFAULT_ZONEID).toEpochSecond());
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		c.add(Calendar.MONTH, month);
//		Date beforeDate = c.getTime();
//		return (int)(beforeDate.getTime()/1000);
	}
	
	/**
	 * 获取每月第一天时间戳
	 * @return
	 */
	public static long getFirstDayOfThisMonth(){
		LocalDate today = LocalDate.now();
		LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
		return firstDayOfThisMonth.atStartOfDay().atZone(DEFAULT_ZONEID).toEpochSecond();
	}
	
	
	/**
	 * 日期格式互换
	 * @param stringTime
	 * @param type
	 * @param type2
	 * @return
	 */
    public static String timeTransfer(String stringTime,String type,String type2){
    	DateFormat format1 = new SimpleDateFormat(type);         
    	DateFormat format2= new SimpleDateFormat(type2);    
    	
    	Date date = null;
    	String str = null;
    	
    	try {
    		date = format1.parse(stringTime);
    		str = format2.format(date);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return str;
    }
    
    public static String getUpdateTimeStr(long updateTime){
    	return getUpdateTimeStr(new Timestamp(updateTime*1000));
    }
    
	public static String getUpdateTimeStr(Timestamp updateTime){
		Date now = new Date(System.currentTimeMillis());
		Date yesterday = new Date(System.currentTimeMillis() - 24 * 3600 * 1000);
		return org.apache.commons.lang3.time.DateUtils.isSameDay(now, updateTime) ? "今天" :
			org.apache.commons.lang3.time.DateUtils.isSameDay(yesterday, updateTime) ? "昨天" : String.format("%d-%d", updateTime.getMonth()+1, updateTime.getDate());
	}
	
	
	/** 
     * 格式化时间 
     * @param time 
     * @return 
     */  
//	public static String formatDateTime(long timestamp) {
//		if(timestamp == 0){
//			return "";
//		}
//		Date date = null;
//		try {
//			date = unixTimestampToDate(timestamp * 1000);
//		} catch (Exception e) {
//			return "";
//		}
//
//		Calendar current = Calendar.getInstance();
//		Calendar today = Calendar.getInstance(); // 今天
//
//		
//		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
//		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
//		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
//		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
//		today.set(Calendar.HOUR_OF_DAY, 0);
//		today.set(Calendar.MINUTE, 0);
//		today.set(Calendar.SECOND, 0);
//
//		Calendar yesterday = Calendar.getInstance(); // 昨天
//
//		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
//		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
//		yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
//		yesterday.set(Calendar.HOUR_OF_DAY, 0);
//		yesterday.set(Calendar.MINUTE, 0);
//		yesterday.set(Calendar.SECOND, 0);
//		current.setTime(date);
//		if (current.after(today)) {
//			return "今天 ";
//		} else if (current.before(today) && current.after(yesterday)) {
//			return "昨天 ";
//		} else {
//			return current.get(Calendar.MONTH)+1+"-"+ current.get(Calendar.DAY_OF_MONTH);
//		}
//	}  
	
	/** 
     * 格式化时间 
     * @param time 
     * @return 
     */  
	public static String formatDateTime(long timestamp) {
		if (timestamp == 0) {
			return "";
		}
		LocalDate p = getZonedDateTimeBySecond(timestamp).toLocalDate();
		LocalDate current = LocalDate.now();
		if (p.isEqual(current)) {
			return "今天 ";
		} else if (p.equals(current.minusDays(1))) {
			return "昨天 ";
		} else {
			return p.getMonthValue() + "-" + p.getDayOfMonth();
		}
	}
	
	 /**
     * 根据秒获取ZonedDateTime(系统时区)
     * @param timestamp
     * @return
     */
	private static ZonedDateTime getZonedDateTimeBySecond(long timestamp) {
		return Instant.ofEpochSecond(timestamp).atZone(DEFAULT_ZONEID);
	}
	
	/**
	 * 获取给定时间当月第一天时间戳
	 * @return
	 */
	public static long getFirstDayOfMonth(String text){
		LocalDate time = LocalDate.parse(text);
		LocalDate firstDayOfMonth = time.with(TemporalAdjusters.firstDayOfMonth());
		return firstDayOfMonth.atStartOfDay().atZone(DEFAULT_ZONEID).toEpochSecond();
	}
	
	/**
	 * 获取给定时间当月最后一天时间戳
	 * @return
	 */
	public static long getFirstDayOfNextMonth(String text){
		LocalDate time = LocalDate.parse(text);
		LocalDate firstDayOfNextMonth = time.with(TemporalAdjusters.lastDayOfMonth());
		return firstDayOfNextMonth.atStartOfDay().plusDays(1).atZone(DEFAULT_ZONEID).toEpochSecond();
	}
	
	/**
	 * 获取每月第一天时间戳
	 * @return
	 */
	public static String getFirstDayStrOfThisMonth(){
		LocalDate today = LocalDate.now();
		LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
		return firstDayOfThisMonth.toString();
	}
	
	
	public static void main(String[] args) {
		System.out.println(getFirstDayStrOfThisMonth());
	}
	

}