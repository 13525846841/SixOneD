package com.yksj.healthtalk.net.http;

import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookiesManager implements CookieJar {
    private HashMap<HttpUrl, List<Cookie>> mCookies = new HashMap<>();
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0){
            mCookies.put(url, cookies);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return mCookies.get(url);
    }
}
