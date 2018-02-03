package com.ruisi.bi.app.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by berlython on 16/6/7.
 */
public class Message implements Serializable{

    private static final long serialVersionUID = -3697214751976213404L;

    public Integer id;

    public String datadate;

    public String title;

    public String state;

    public String pushType;

    public Long crtdate;
}
