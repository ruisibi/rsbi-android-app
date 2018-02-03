package com.ruisi.bi.app.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class MenuBean {
	public String name;
	public String pic;
	public String uri;
	public String note;
	public int id;

	@Override
	public String toString() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("name", name);
			obj.put("pic", pic);
			obj.put("uri", uri);
			obj.put("note", note);
			obj.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
}
