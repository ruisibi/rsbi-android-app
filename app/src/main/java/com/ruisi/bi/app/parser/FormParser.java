package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.adapter.TableAdapter.TableCell;
import com.ruisi.bi.app.adapter.TableAdapter.TableRow;
import com.ruisi.bi.app.adapter.TableAdapter.TableRowHead;
import com.ruisi.bi.app.bean.FormBean;
import com.ruisi.bi.app.bean.FormDataChildBean;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.WeiduOptionBean;

public class FormParser extends BaseParser {

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		FormBean formBean = null;
		if (jsonStr != null) {
			formBean = new FormBean();
			JSONObject obj = new JSONObject(jsonStr);
			JSONArray objArray = obj.getJSONArray("comps");
			ArrayList<WeiduBean> options = new ArrayList<>();
			JSONArray paramsArray = obj.optJSONArray("params");
			if(paramsArray!=null)
			for (int i = 0; i < paramsArray.length(); i++) {
				JSONObject paramsObj = paramsArray.getJSONObject(i);
				WeiduBean weiduBean = new WeiduBean();
				if (paramsObj.getString("name").toString().equals("null"))
					weiduBean.name = "";
				else
					weiduBean.name = paramsObj.getString("name");
				weiduBean.type = paramsObj.getString("type");
				if (weiduBean.type.equals("dateSelect")) {
					if (!paramsObj.getString("max").equals("")) {
						weiduBean.max=paramsObj.getString("max");
					}
					if (!paramsObj.getString("min").equals("")) {
						weiduBean.min=paramsObj.getString("min");
					}
				}else{
					weiduBean.value = paramsObj.optString("value");
					ArrayList<WeiduOptionBean> optionList=new ArrayList<>();
					JSONArray optionArray = paramsObj.getJSONArray("options");
					for (int j = 0; j < optionArray.length(); j++) {
						JSONObject optionObj = optionArray.getJSONObject(j);
						WeiduOptionBean optionBean=new WeiduOptionBean();
						optionBean.text=optionObj.getString("text");
						optionBean.value=optionObj.getString("value");
						optionList.add(optionBean);
					}
					weiduBean.options = optionList;
				}
				options.add(weiduBean);
			}
			formBean.options=options;
			ArrayList<TableRow> TableRows = new ArrayList<>();
			for (int i = 0; i < objArray.length(); i++) {
				JSONObject objdata = objArray.getJSONObject(i);
				JSONArray headArray = objdata.getJSONArray("head");
				TableCell headCell = null;
				ArrayList<TableCell> tableCell01 = new ArrayList<>();
				ArrayList<TableCell> tableCell02 = new ArrayList<>();
				for (int k = 0; k < headArray.length(); k++) {
					JSONArray heads = headArray.getJSONArray(k);
					for (int l = 0; l < heads.length(); l++) {
						if (heads.get(l).toString().equals("null"))
							continue;
						JSONObject hang = heads.getJSONObject(l);
						if (k == 0 && l == 0) {
							FormDataChildBean fdcb = new FormDataChildBean();
							fdcb.colSpan = hang.getInt("colSpan");
							fdcb.rowSpan = hang.getInt("rowSpan");
							fdcb.value = hang.getString("name");
							if (headArray.length() == 1)
								headCell = new TableCell(fdcb, 300, 80, 0);
							else
								headCell = new TableCell(fdcb, 300,
										80 * headArray.length() + 2, 0);
						} else if (k == 0 && l != 0) {
							FormDataChildBean fdcb = new FormDataChildBean();
							fdcb.colSpan = hang.getInt("colSpan");
							fdcb.rowSpan = hang.getInt("rowSpan");
							fdcb.value = hang.getString("name");
							TableCell cell = new TableCell(fdcb, 300, 80, 0);
							tableCell01.add(cell);
						} else if (k == 1) {
							FormDataChildBean fdcb = new FormDataChildBean();
							fdcb.colSpan = hang.getInt("colSpan");
							fdcb.rowSpan = hang.getInt("rowSpan");
							fdcb.value = hang.getString("name");
							TableCell cell = new TableCell(fdcb, 300, 80, 0);
							tableCell02.add(cell);
						}
					}
				}
				TableRowHead thead = new TableRowHead(tableCell01, tableCell02,
						headCell);
				TableRows.add(thead);

				JSONArray dataArray = objdata.getJSONArray("data");
				for (int k = 0; k < dataArray.length(); k++) {
					JSONArray dataArrays = dataArray.getJSONArray(k);
					ArrayList<TableCell> hangCells = new ArrayList<TableCell>();
					for (int l = 0; l < dataArrays.length(); l++) {
						JSONObject hang = dataArrays.getJSONObject(l);
						FormDataChildBean fdcb = new FormDataChildBean();
						fdcb.colSpan = hang.getInt("colSpan");
						fdcb.type = hang.getInt("type");
						fdcb.rowSpan = hang.getInt("rowSpan");
						fdcb.fmt = hang.getString("fmt");
						fdcb.trueValue = hang.getString("trueValue");
						fdcb.value = hang.getString("value");
						TableCell cell = new TableCell(fdcb, 300, 80, 0);
						hangCells.add(cell);
					}
					TableRow t = new TableRow(hangCells, false);
					TableRows.add(t);
				}
			}
			formBean.TableRows = TableRows;
		}
		return (T) formBean;
	}

}
