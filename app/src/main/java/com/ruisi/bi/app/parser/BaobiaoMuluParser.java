package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.ruisi.bi.app.bean.BaobiaoMuluBean;
import com.ruisi.bi.app.bean.ReLoginBean;

public class BaobiaoMuluParser extends BaseParser{

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		JSONArray array=new JSONArray(jsonStr);
		ArrayList<BaobiaoMuluBean> data = null;
		if (array.length()>0) {
			data=new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj=array.getJSONObject(i);
				BaobiaoMuluBean bean=new BaobiaoMuluBean();
				bean.name=obj.getString("name");
				bean.id=obj.getInt("id");
				data.add(bean);
			}
		}
		return (T) data;
	}

}
