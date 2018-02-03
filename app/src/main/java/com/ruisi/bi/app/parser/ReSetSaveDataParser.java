package com.ruisi.bi.app.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.ReSetSaveDataBean;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.ZhibiaoBean;

public class ReSetSaveDataParser extends BaseParser{

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		ReSetSaveDataBean bean=null;
		if (jsonStr!=null&&!jsonStr.equals("")) {
			bean=new ReSetSaveDataBean();
			JSONObject obj=new JSONObject(jsonStr);
			bean.id=obj.getInt("id");
			bean.subjectid=obj.getInt("subjectid");
			bean.subjectname=obj.getString("subjectname");
			bean.tid=obj.getInt("tid");
			JSONArray param=obj.getJSONArray("params");
			for (int i = 0; i < param.length(); i++) {
				JSONObject paramobj=param.getJSONObject(i);
				WeiduBean ppp=new WeiduBean();
				ppp.col_id=paramobj.getInt("id");
				ppp.tableColKey=paramobj.getString("tableColKey");
				ppp.grouptype=paramobj.getString("grouptype");
				ppp.dimord=paramobj.getString("dimord");
				ppp.tableName=paramobj.getString("tableName");
				ppp.text=paramobj.getString("name");
				ppp.name=paramobj.getString("name");
				ppp.tname=paramobj.getString("tname");
				ppp.tid=paramobj.getInt("tid");
				ppp.dim_type=paramobj.getString("type");
				ppp.valType=paramobj.getString("valType");
				ppp.col_name=paramobj.getString("colname");
				ppp.tableColName=paramobj.getString("tableColName");
				if (paramobj.has("alias")) {
					ppp.alias = paramobj.getString("alias");
				}
				bean.params=ppp;
			}
			JSONObject handlieobj=obj.getJSONObject("table").getJSONObject("tableJson");
			JSONArray colsArray=handlieobj.getJSONArray("cols");
			for (int i = 0; i < colsArray.length(); i++) {
				JSONObject colsobj=colsArray.getJSONObject(i);
				WeiduBean ppp=new WeiduBean();
				if (!colsobj.has("tid")) {
					continue;
				}
				ppp.col_id=colsobj.getInt("id");
				ppp.grouptype=colsobj.getString("grouptype");
				ppp.tableName=colsobj.getString("tableName");
				ppp.valType=colsobj.getString("valType");
				ppp.col_name=colsobj.getString("colname");
				ppp.tid=colsobj.getInt("tid");
				ppp.tname=colsobj.getString("tname");
				ppp.tableColName=colsobj.getString("tableColName");
				ppp.dimord=colsobj.getString("dimord");
				ppp.tableColKey=colsobj.getString("tableColKey");
				ppp.dim_type=colsobj.getString("type");
				ppp.dim_name=colsobj.getString("dim_name");
				ppp.iscas=colsobj.getString("iscas");
				ppp.text=colsobj.getString("dimdesc");
				ppp.vals=colsobj.optString("vals");
				ppp.startmt=colsobj.optString("startmt");
				if (ppp.startmt!=null&&!ppp.startmt.equals("")) {
					ppp.selectType="month";
				}
				ppp.endmt=colsobj.optString("endmt");
				ppp.startdt=colsobj.optString("startdt");
				if (ppp.startdt!=null&&!ppp.startdt.equals("")) {
					ppp.selectType="day";
				}
				ppp.enddt=colsobj.optString("enddt");
				ppp.filtertype=colsobj.optString("filtertype");
				if (colsobj.has("alias")) {
					ppp.alias = colsobj.getString("alias");
				}
				bean.cols=ppp;
			}
			JSONArray rowsArray=handlieobj.getJSONArray("rows");
			for (int i = 0; i < rowsArray.length(); i++) {
				JSONObject rowsobj=rowsArray.getJSONObject(i);
				WeiduBean ppp=new WeiduBean();
				ppp.col_id=rowsobj.getInt("id");
				ppp.grouptype=rowsobj.getString("grouptype");
				ppp.tableName=rowsobj.getString("tableName");
				ppp.valType=rowsobj.getString("valType");
				ppp.col_name=rowsobj.getString("colname");
				ppp.tid=rowsobj.getInt("tid");
				ppp.tname=rowsobj.getString("tname");
				ppp.tableColName=rowsobj.getString("tableColName");
				ppp.dimord=rowsobj.getString("dimord");
				ppp.tableColKey=rowsobj.getString("tableColKey");
				ppp.dim_type=rowsobj.getString("type");
				ppp.dim_name=rowsobj.getString("dim_name");
				ppp.iscas=rowsobj.getString("iscas");
				ppp.text=rowsobj.getString("dimdesc");
				
				ppp.vals=rowsobj.optString("vals");
				ppp.startmt=rowsobj.optString("startmt");
				if (ppp.startmt!=null&&!ppp.startmt.equals("")) {
					ppp.selectType="month";
				}
				ppp.endmt=rowsobj.optString("endmt");
				ppp.startdt=rowsobj.optString("startdt");
				if (ppp.startdt!=null&&!ppp.startdt.equals("")) {
					ppp.selectType="day";
				}
				ppp.enddt=rowsobj.optString("enddt");
				ppp.filtertype=rowsobj.optString("filtertype");
				if (rowsobj.has("alias")) {
					ppp.alias = rowsobj.getString("alias");
				}
				bean.rows=ppp;
			}
			JSONArray zhibiaoArray=obj.getJSONObject("table").getJSONArray("kpiJson");
			for (int i = 0; i < zhibiaoArray.length(); i++) {
				JSONObject zhibiaoobj=zhibiaoArray.getJSONObject(i);
				ZhibiaoBean zhibao=new ZhibiaoBean();
				zhibao.col_name=zhibiaoobj.getString("col_name");
				zhibao.aggre=zhibiaoobj.getString("aggre");
				zhibao.unit=zhibiaoobj.getString("unit");
				zhibao.rate=zhibiaoobj.getString("rate");
				zhibao.fmt=zhibiaoobj.getString("fmt");
				zhibao.text=zhibiaoobj.getString("kpi_name");
				zhibao.col_id=zhibiaoobj.getInt("kpi_id");
				zhibao.tid=zhibiaoobj.getInt("tid");
				zhibao.alias=zhibiaoobj.getString("alias");
				bean.kpiJson=zhibao;
			}
		}
		return (T) bean;
	}

}
