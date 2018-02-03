package com.ruisi.bi.app.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThemeBean {
	public int id;
	public String text;
	public String tp;
	public String dsource;
	public String tname;
	public String ord;
	public int ttype;
	public int pid;
	public int tid;
	public ArrayList<ThemeChildBean> childs;

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
			JSONArray array =new JSONArray();
			for (int i = 0; i < childs.size(); i++) {
				ThemeChildBean chile=childs.get(i);
				JSONObject obj01=new JSONObject();
				try {
					obj01.put("id", chile.id);
					obj01.put("text", chile.text);
					obj01.put("tp", chile.tp);
					obj01.put("dsource", chile.dsource);
					obj01.put("tname", chile.tname);
					obj01.put("ord", chile.ord);
					obj01.put("ttype", chile.ttype);
					obj01.put("pid", chile.pid);
					obj01.put("tid", chile.tid);
					obj01.put("isChecked", chile.isChecked);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				array.put(obj01);
			}
			obj.put("children", array);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString();
	}
}
