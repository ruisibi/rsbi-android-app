package com.ruisi.bi.app.net;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.ruisi.bi.app.common.APIContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * httpclient cookie 保持类
 * Created by hq on 2018/1/26.
 */

public class MyCookieJar implements CookieJar {

    //Cookie缓存区
    private static List<Cookie> cookies = new ArrayList<Cookie>();

    public static void clearCookie(){
        cookies.clear();
    }

    public static List<Cookie> getCookies(){
        return cookies;
    }

    public static void setCookies(List<Cookie> cookies){
        MyCookieJar.cookies = cookies;
    }

    @Override
    public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
        if(arg1 != null && arg1.size() > 0) {
            for(Cookie ck : arg1){
                String name = ck.name();
                int pos = exist(name);
                if(pos != -1){
                    cookies.remove(pos);
                }
                cookies.add(ck);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl arg0) {
        return cookies;
    }

    /**
     * 返回位置， -1表示不存在
     * @param name
     * @return
     */
    private int exist(String name){
        int ret = -1;
        for(int i=0; i< cookies.size(); i++){
            Cookie ck = cookies.get(i);
            if(ck.name().equals(name)){
                ret = i;
                break;
            }
        }
        return ret;
    }

    /**
     * 同步cookie
     * @param url
     */
    public static void syncCookes(Context context, String url){
        //同步cookie
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie( true );
        cookieManager.removeSessionCookie();// 移除
        cookieManager.removeAllCookie();
        StringBuilder sbCookie = new StringBuilder();//创建一个拼接cookie的容器,为什么这么拼接，大家查阅一下http头Cookie的结构
        List<Cookie> cookies =  MyCookieJar.getCookies();
        for(int i=0; i<cookies.size(); i++){
            Cookie ck = cookies.get(i);
            sbCookie.append(ck.name()+"="+ck.value());
            if(i != cookies.size() - 1){
                sbCookie.append("; ");
            }
        }
        String cookieValue = sbCookie.toString();
        String u = APIContext.HOST.replace("http://", "").replace("https://", "");
        if (u.contains("/")) {
            u = u.substring(0, u.indexOf('/'));
        }
        cookieManager.setCookie(u, cookieValue);//为url设置cookie
        CookieSyncManager.getInstance().sync();//同步cookie
    }
}
