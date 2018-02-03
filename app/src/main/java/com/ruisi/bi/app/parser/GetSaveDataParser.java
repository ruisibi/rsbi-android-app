package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.SaveBean;

public class GetSaveDataParser extends BaseParser{

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		ArrayList<SaveBean> datas=null;
		if (jsonStr!=null&&!jsonStr.equals("")) {
			JSONArray array =new JSONArray(jsonStr);
			datas=new ArrayList<SaveBean>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj=array.getJSONObject(i);
				SaveBean bean=new SaveBean();
				bean.id=obj.getInt("id");
				bean.name=obj.getString("name");
				bean.crtdate=obj.getString("crtdate");
				datas.add(bean);
			}
		}
		return (T) datas;
	}

}
