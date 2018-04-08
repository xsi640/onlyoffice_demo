package com.suyang.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {
    public static String getPrefix(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort();
    }
}
