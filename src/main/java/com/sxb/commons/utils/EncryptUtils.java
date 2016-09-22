package com.sxb.commons.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * 加密工具
 * 
 * @author shenjun
 * @date 2015年1月16日
 */
public class EncryptUtils {

	private static final String SHA1 = "SHA1";
	public static final String MD5 = "MD5";

	/**
	 * SHA加密
	 * 
	 * @param str
	 * @return
	 */
	public static String encryptSHA1(String str) {
		if (str == null || str == "")
			return null;
		StringBuffer hexString = new StringBuffer();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(SHA1);
			byte[] bytes = str.getBytes();
			md.update(bytes);
			byte[] reBytes = md.digest();
			for (int i = 0; i < reBytes.length; i++) {
				if ((0xff & reBytes[i]) < 0x10) {
					hexString.append("0" + Integer.toHexString((0xFF & reBytes[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & reBytes[i]));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hexString.toString();
	}

	/**
	 * SHA加密，密码未经base64加密
	 * 
	 * @param str
	 * @return
	 */
	public static String encryptSHAInner(String str) {
		if (str == null || str == "")
			return null;
		StringBuffer hexString = new StringBuffer();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(SHA1);
			byte[] bytes = str.getBytes();
			md.update(bytes);
			byte[] reBytes = md.digest();
			for (int i = 0; i < reBytes.length; i++) {
				if ((0xff & reBytes[i]) < 0x10) {
					hexString.append("0" + Integer.toHexString((0xFF & reBytes[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & reBytes[i]));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hexString.toString().toUpperCase();
	}

	/**
	 * MD5加密，获得密码
	 * 
	 * MD5(MD5(pwd)+login_salt)
	 * 
	 * @param pwd
	 * @return
	 */
	public static String encryptPassword(String pwd, String loginSalt) {
		if (pwd == null || pwd == "")
			return null;
		return MD5Str(MD5Str(pwd) + loginSalt);// 加密
	}

	/**
	 * MD5加密字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String MD5Str(String str) {
		if (str == null || str == "")
			return null;
		StringBuffer hexString = new StringBuffer();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(MD5);
			byte[] bytes = str.getBytes();
			md.update(bytes);
			byte[] reBytes = md.digest();
			for (int i = 0; i < reBytes.length; i++) {
				if ((0xff & reBytes[i]) < 0x10) {
					hexString.append("0" + Integer.toHexString((0xFF & reBytes[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & reBytes[i]));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hexString.toString();
	}

	// 解密
	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			// BASE64Decoder decoder = new BASE64Decoder();
			try {
				// b = decoder.decodeBuffer(s);
				b = Base64.decodeBase64(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 获取base64位解密密码
	 * 
	 * 传过来的加密方式为：base64(两位随机英文字母+base64(三位随机英文字母+密码+四位随机英文字母))
	 * 
	 * @param s
	 * @return
	 */
	public static String getPwdFromBase64(String s) {
		String s1 = getFromBase64(s).substring(2);
		String s2 = getFromBase64(s1);
		return s2.substring(3, s2.length() - 4);
	}
	
	public static String getBase64FromPwd(String s) {
		String s1 = RandomStringUtils.random(3, true, true) + s + RandomStringUtils.random(4, true, true);
		String s2 = Base64.encodeBase64String(s1.getBytes(StandardCharsets.UTF_8));
		return Base64.encodeBase64String((RandomStringUtils.random(2, true, true) + s2).getBytes(StandardCharsets.UTF_8));
	}

	// 测试用：
	public static void main(String[] args) {
		// 密码为123456
		// QlpWa2RSY3pFeU16UTFObFJHVVZJPQ== s123456
		// Q0ZUMUZSTVRJek5EVTJVRlpLVFE9PQ== 123456
		// System.out.println(getPwdFromBase64("QlpWa2RSY3pFeU16UTFObFJHVVZJPQ=="));
		// System.out.println(encryptSHA("Q0ZUMUZSTVRJek5EVTJVRlpLVFE9PQ=="));
		// System.out.println(getPwdFromBase64("WkRXa0pMTmpVME16SXhXVVpUU3c9PQ=="));

		// System.out.println(encryptPassword("Q0ZUMUZSTVRJek5EVTJVRlpLVFE9PQ==",
		// "12345"));
		System.out.println(encryptSHA1("123456"));

	}
}