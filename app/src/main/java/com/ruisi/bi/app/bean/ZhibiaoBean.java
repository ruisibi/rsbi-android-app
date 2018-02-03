package com.ruisi.bi.app.bean;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class ZhibiaoBean {
	public boolean isChecked;
	public int col_id;
	public String tableColKey;
	public String fmt;
	public String state;
	public String ord;
	public String calc;
	public String groupname;
	public String dim_type;
	public String iconCls;
	public String dim_name;
	public String ord2;
	public int id;
	public String aggre;
	public String ordcol;
	public String rate;
	public String tableName;
	public String iscas;
	public int tid;
	public String calc_kpi;
	public int col_type;
	public String col_name;
	public String text;
	public String dimord;
	public String alias;
	public int ttype;
	public String valType;
	public String unit;
	public String grouptype;
	public String tname;
	public String tableColName;

	@Override
	public String toString() {
		JSONObject obj=new JSONObject();
		try {
			obj.put("tid", tid);
			obj.put("unit", unit);
			obj.put("rate", rate);
			obj.put("alias", alias);
			obj.put("fmt", fmt);
			obj.put("aggre", aggre);
			obj.put("col_name", col_name);
			obj.put("aggre", aggre);
			obj.put("text", text);
			obj.put("col_id", col_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
	public static ZhibiaoBean creatObj(String json) {
		Gson f = new Gson();
		ZhibiaoBean bean = f.fromJson(json, ZhibiaoBean.class);
		return bean;
	}
}
