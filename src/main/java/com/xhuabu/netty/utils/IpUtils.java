/*
 * Copyright (c) 2015 www.caniu.com - 版权所有
 * 
 * This software is the confidential and proprietary information of
 * luckin Group. You shall not disclose such confidential information 
 * and shall use it only in accordance with the terms of the license 
 * agreement you entered into with www.cainiu.com
 */
package com.xhuabu.netty.utils;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * 描述:
 *
 * @author  boyce
 * @created 2015年5月18日 下午2:26:18
 * @since   v1.0.0
 */
public class IpUtils {
	public static String getUserIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	public static String getAddrIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

			ip = request.getHeader("Proxy-Client-IP") + "from Proxy-Client-IP";
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP")+ "from WL-Proxy-Client-IP";
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr()+"from getRemoteAddr";
		}

		return ip;
	}
	public static boolean isDecimal(String number) {
		String regularExpression = "[0-9.]*";
		if (number.matches(regularExpression)) {
			return true;
		}
		return false;
	}

}
