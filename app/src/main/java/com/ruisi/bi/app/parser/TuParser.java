package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ruisi.bi.app.adapter.TableAdapter.TableCell;
import com.ruisi.bi.app.adapter.TableAdapter.TableRow;
import com.ruisi.bi.app.adapter.TableAdapter.TableRowHead;
import com.ruisi.bi.app.bean.FormDataChildBean;
import com.ruisi.bi.app.bean.QuxianBean;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.WeiduOptionBean;
import com.ruisi.bi.app.bean.YVals;
import com.ruisi.bi.app.bean.YValsEntry;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;

public class TuParser extends BaseParser {

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		 LineData data = null;
		 ArrayList<Object> dataR=null;
		if (jsonStr != null) {
			dataR=new ArrayList<>();
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
			dataR.add(options);
			for (int i = 0; i < objArray.length(); i++) {
				JSONObject objdata = objArray.getJSONObject(i);
				JSONArray headArray = objdata.getJSONArray("yVals");
				ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
				for (int j = 0; j < headArray.length(); j++) {
					if (j>APIContext.most_show_count)break;
					JSONObject itemData = headArray.getJSONObject(j);
					JSONArray itemArray = itemData.getJSONArray("Entry");
					ArrayList<Entry> yVals2 = new ArrayList<Entry>();
					for (int k = 0; k < itemArray.length(); k++) {
						if (k>APIContext.most_show_count)break;
						JSONObject entryData = itemArray.getJSONObject(k);
						yVals2.add(new Entry(entryData.getInt("value"), k));
					}
					 LineDataSet set2 = new LineDataSet(yVals2, itemData.getString("label"));
				        set2.setAxisDependency(AxisDependency.RIGHT);
				        set2.setColor(AppContext.colors[j]);
				        set2.setCircleColor(Color.BLACK);
				        set2.setLineWidth(2f);
				        set2.setCircleSize(3f);
				        set2.setFillAlpha(65);
				        set2.setFillColor(Color.RED);
				        set2.setDrawCircleHole(false);
				        set2.setHighLightColor(Color.rgb(244, 117, 117));
				        dataSets.add(set2);
				}
				JSONArray xValsArray = objdata.getJSONArray("xVals");
				ArrayList<String> xVals = new ArrayList<String>();
				for (int j = 0; j < xValsArray.length(); j++) {
					if (j>APIContext.most_show_count)break;
					xVals.add(xValsArray.getString(j));
				}
				data = new LineData(xVals, dataSets);
				data.setValueTextColor(Color.TRANSPARENT);
				data.setValueTextSize(9f);
			}
			dataR.add(data);
		}
		return (T) dataR;
	}

}
