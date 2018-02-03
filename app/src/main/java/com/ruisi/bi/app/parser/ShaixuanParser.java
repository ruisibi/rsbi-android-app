package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.ShaixuanBean;
import com.ruisi.bi.app.bean.WeiduOptionBean;

public class ShaixuanParser extends BaseParser {

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
//		ArrayList<WeiduOptionBean> options=null;
		ShaixuanBean bean=null;
		if (jsonStr != null) {
			JSONObject obj = new JSONObject(jsonStr);
			String type=obj.getString("type");
			bean=new ShaixuanBean();
			bean.type=type;
			if (type.equals("month")) {
				JSONObject endObj=obj.getJSONObject("endMonth");
				bean.endName=endObj.getString("name");
				bean.endOptions=new ArrayList<>();
				JSONArray endArray=endObj.getJSONArray("options");
				for (int i = 0; i < endArray.length(); i++) {
					JSONObject option =endArray.getJSONObject(i);
					WeiduOptionBean optionBean=new WeiduOptionBean();
					optionBean.text=option.getString("name");
					optionBean.value=option.getString("id");
					bean.endOptions.add(optionBean);
				}
				JSONObject startObj=obj.getJSONObject("startMonth");
				bean.startName=startObj.getString("name");
				bean.startOptions=new ArrayList<>();
				JSONArray startArray=startObj.getJSONArray("options");
				for (int i = 0; i < startArray.length(); i++) {
					JSONObject option =startArray.getJSONObject(i);
					WeiduOptionBean optionBean=new WeiduOptionBean();
					optionBean.text=option.getString("name");
					optionBean.value=option.getString("id");
					bean.startOptions.add(optionBean);
				}
			}else if (type.equals("other")) {
				bean.startOptions=new ArrayList<>();
				JSONArray array=obj.getJSONArray("options");
				for (int i = 0; i < array.length(); i++) {
					JSONObject option =array.getJSONObject(i);
					WeiduOptionBean otherBean=new WeiduOptionBean();
					otherBean.text=option.getString("name");
					otherBean.value=option.getString("id");
					bean.startOptions.add(otherBean);
				}
			}else if (type.equals("day")) {
				bean.startDayMin=obj.getJSONObject("startDay").getString("min");
				bean.startDayName=obj.getJSONObject("startDay").getString("name");
				bean.endDayMax=obj.getJSONObject("endDay").getString("max");
				bean.endDayName=obj.getJSONObject("endDay").getString("name");
			}
			
		}

		return (T) bean;
	}

}
