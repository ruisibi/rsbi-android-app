package com.ruisi.bi.app.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.ReLoginBean;

public class SaveSelectDataParser extends BaseParser{

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		return (T) jsonStr;
	}

}
