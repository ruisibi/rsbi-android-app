package com.ruisi.bi.app.bean;

import java.io.Serializable;

/**
 * cookie 序列化类
 * Created by hq on 2018/1/27.
 */

public class CookieSerializBean implements Serializable {

    public String domain;
    public String name;
    public String value;
    public long expiresAt;
    public String path;

    public CookieSerializBean(){

    }

    public CookieSerializBean(String domain, String path, String name, String value, long expiresAt){
        this.domain = domain;
        this.path = path;
        this.name = name;
        this.value = value;
        this.expiresAt = expiresAt;
    }
}
