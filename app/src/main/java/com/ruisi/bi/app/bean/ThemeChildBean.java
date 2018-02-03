package com.ruisi.bi.app.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class ThemeChildBean {
	public int id;
	public String text;
	public String tp;
	public String dsource;
	public String tname;
	public String ord;
	public int ttype;
	public int pid;
	public int tid;
	public boolean isChecked;
	@Override
	public String toString() {
		JSONObject obj=new JSONObject();
		try {
			obj.put("id", id);
			obj.put("text", text);
			obj.put("tp", tp);
			obj.put("dsource", dsource);
			obj.put("tname", tname);
			obj.put("ord", ord);
			obj.put("ttype", ttype);
			obj.put("pid", pid);
			obj.put("tid", tid);
			obj.put("isChecked", isChecked);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString();
	}
}
