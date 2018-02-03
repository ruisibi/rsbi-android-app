package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.WeiduBean;

public class WeiduParser extends BaseParser{

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		ArrayList<WeiduBean> weidu=null;
		JSONArray array=new JSONArray(jsonStr);
		if(array.length()>0){
			weidu=new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj=array.getJSONObject(i);
				WeiduBean weiduBean=new WeiduBean();
				weiduBean.text=obj.getString("text");
				weiduBean.id=obj.getInt("id");
				weiduBean.tid=obj.getInt("tid");
				weiduBean.col_id=obj.getInt("col_id");
				weiduBean.tableColKey=obj.getString("tableColKey").equals("null")?"":obj.getString("tableColKey");
				weiduBean.ord=obj.getString("ord");
				weiduBean.calc=obj.getString("calc");
				weiduBean.groupname=obj.getString("groupname").equals("null")?"":obj.getString("groupname");
				weiduBean.dim_type=obj.getString("dim_type");
				weiduBean.iconCls=obj.getString("iconCls");
				weiduBean.dim_name=obj.getString("dim_name");
				weiduBean.ord2=obj.getString("ord2");
				weiduBean.aggre=obj.getString("aggre");
				weiduBean.ordcol=obj.getString("ordcol").equals("null")?"":obj.getString("ordcol");
//				weiduBean.rate=obj.getInt("rate");
				weiduBean.rate=1;

				weiduBean.tableName=obj.getString("tableName").equals("null")?"":obj.getString("tableName");
				weiduBean.iscas=obj.getString("iscas").equals("null")?"":obj.getString("iscas");
				weiduBean.calc_kpi=obj.getString("calc_kpi");
				weiduBean.col_type=obj.getInt("col_type");
				weiduBean.col_name=obj.getString("col_name");
				weiduBean.dimord=obj.getString("dimord");
				weiduBean.alias=obj.getString("alias");
				weiduBean.ttype=obj.getInt("ttype");
				weiduBean.valType=obj.getString("valType");
				weiduBean.unit=obj.getString("unit");
				weiduBean.grouptype=obj.getString("grouptype").equals("null")?"":obj.getString("grouptype");
				weiduBean.tname=obj.getString("tname").equals("null")?"":obj.getString("tname");
				weiduBean.tableColName=obj.getString("tableColName").equals("null")?"":obj.getString("tableColName");

                weiduBean.dateformat = obj.getString("dateformat");

				weidu.add(weiduBean);
			}
		}
		return (T) weidu;
	}

}
