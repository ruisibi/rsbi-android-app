package com.ruisi.bi.app.bean;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class WeiduBean {
	public boolean isChecked;
	public boolean canClicked;
	public int col_id;
	public String tableColKey;

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

	public int rate;
	public String tableName;

	public String iscas;
	public int tid;

	public String calc_kpi;
	public int col_type;

	public String col_name;
	public String text;

	public String dimord;
	public String alias;

	public String dateformat;

	public int ttype;
	public String valType;

	public String unit;
	public String grouptype;

	public String tname;
	public String tableColName;

	public String type;
	public String value;
	public String name;
	public ArrayList<WeiduOptionBean> options;

	public String min;
	public String max;
	
	public String vals;
	public String startmt;
	public String endmt;
	public String filtertype;
	public String startdt;
	public String enddt;
public String selectType;	

public String Lievals;
public String Liestartmt;
public String Lieendmt;
public String Liefiltertype;
public String Liestartdt;
public String Lieenddt;
public String LieselectType;	

	@Override
	public String toString() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("dim_name", dim_name);
			obj.put("iconCls", iconCls);
			obj.put("dim_type", dim_type);
			obj.put("groupname", groupname);
			obj.put("calc", calc);
			obj.put("ord", ord);
			obj.put("tableColKey", tableColKey);
			obj.put("col_id", col_id);
			obj.put("rate", rate);
			obj.put("ordcol", ordcol);
			obj.put("aggre", aggre);
			obj.put("id", id);
			obj.put("ord2", ord2);
			obj.put("col_type", col_type);
			obj.put("calc_kpi", calc_kpi);
			obj.put("tid", tid);
			obj.put("iscas", iscas);
			obj.put("tableName", tableName);
			obj.put("tableColName", tableColName);
			obj.put("tname", tname);
			obj.put("grouptype", grouptype);
			obj.put("unit", unit);
			obj.put("valType", valType);
			obj.put("ttype", ttype);
			obj.put("alias", alias);
			obj.put("dimord", dimord);
			obj.put("text", text);
			obj.put("col_name", col_name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	public static WeiduBean creatObj(String json) {
		Gson f = new Gson();
		WeiduBean bean = f.fromJson(json, WeiduBean.class);
		return bean;
	}
}
