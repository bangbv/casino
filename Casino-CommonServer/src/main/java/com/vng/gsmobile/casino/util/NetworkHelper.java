package com.vng.gsmobile.casino.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.couchbase.client.java.document.json.JsonObject;

public class NetworkHelper {

	public static String getLocalIp() {
		InetAddress IP;
		try {
			IP = InetAddress.getLocalHost();
			return IP.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLocaltion(HttpServletRequest request) {
		String clientIp = getClientIp(request);
		System.out.println("clientIp:"+clientIp);
		JsonObject io = LocalCache.get("28_20170316");
		JsonObject ipVnLo = io.getObject("ipVn");
		Map<String, Object> ipVnlm = ipVnLo.toMap();
		for (Entry<String, Object> e : ipVnlm.entrySet()) {
			String cidr = e.getKey();
			String[] cidrA = cidr.split("/");
			String location = (String) e.getValue();
			String vnIp = cidrA[0];
			int subnet = Integer.valueOf(cidrA[1]);
			if (isVnIp(vnIp, subnet, clientIp)) {
				return location;
			}
		}
		return "QD";
	}

	private static String getClientIp(HttpServletRequest request) {
		String[] IP_HEADER_CANDIDATES = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
				"HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
				"HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

		for (String header : IP_HEADER_CANDIDATES) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}

	private static boolean isVnIp(String ip, int subnet, String ipCheck) {
		// Step 1. Convert IPs into ints (32 bits).
		// E.g. 157.166.224.26 becomes 10011101 10100110 11100000 00011010
		int addrC = ipToInt(ipCheck);
		int addr = ipToInt(ip);
		// Step 2. Get CIDR mask
		int mask = (-1) << (32 - subnet);
		// Step 3. Find lowest IP address
		int lowest = addr & mask;
		// Step 4. Find highest IP address
		int highest = lowest + (~mask);
		if ((addrC >= lowest) && (highest <= highest)) {
			return true;
		}
		return false;
	}
	
	private static int ipToInt(String ip){
		String[] ipA = ip.split("\\.");
		int ip0 = Integer.valueOf(ipA[0]);
		int ip1 = Integer.valueOf(ipA[1]);
		int ip2 = Integer.valueOf(ipA[2]);
		int ip3 = Integer.valueOf(ipA[3]);
		int addr = ((ip0 << 24) & 0xFF000000) | ((ip1 << 16) & 0xFF0000) | ((ip2 << 8) & 0xFF00) | (ip3 & 0xFF);		
		return addr;
	}
}
