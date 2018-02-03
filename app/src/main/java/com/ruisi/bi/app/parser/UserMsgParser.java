package com.ruisi.bi.app.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateFormat;

import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.UserBean;

public class UserMsgParser extends BaseParser{

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		UserBean bean=null;
		if (jsonStr!=null&&!jsonStr.equals("")) {
			bean=new UserBean();
			JSONObject obj=new JSONObject(jsonStr);
			bean.company=obj.getString("company");
			bean.staff_id=obj.getString("staff_id");
			bean.login_name=obj.getString("login_name");
			bean.state=obj.getString("state");
			bean.log_cnt=obj.getString("log_cnt");
			
			JSONObject date=obj.getJSONObject("log_date");
			DateFormat df = new DateFormat();
			String dddd=(String) df.format("yyyy-MM-dd hh:mm:ss", date.getLong("time"));
			bean.log_date=dddd;
			bean.date_start=obj.getString("date_start");
			bean.date_end=obj.getString("date_end");
		}
		return (T) bean;
	}

}
