package com.ruisi.bi.app.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.UserBean;

public class LoginParser extends BaseParser{

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		JSONObject jsonObject=new JSONObject(jsonStr);
//		if (jsonObject.optString("error")!=null) {
//			return (T) new ReLoginBean(jsonObject.optString("error"));
//		}
		boolean isSuccessed=jsonObject.getBoolean("result");
		UserBean user=null;
		if(isSuccessed){
			user=new UserBean();
			JSONObject obj=jsonObject.getJSONObject("user");
			//user.name=obj.getString("loginName");
			user.userid=obj.getString("userId");
			user.token=jsonObject.getString("token");
		}else {
			user=new UserBean();
			user.sysUser=jsonObject.getString("msg");
		}
		return (T) user;
	}

	

}
