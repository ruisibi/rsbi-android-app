package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.WeiduOptionBean;
import com.ruisi.bi.app.common.APIContext;

public class TuBingxingParser extends BaseParser {

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		PieData data = null;
		 ArrayList<Object> dataR=null;
		if (jsonStr != null) {
			dataR=new ArrayList<>();
			JSONObject obj = new JSONObject(jsonStr);
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
			
			JSONArray objArray = obj.getJSONArray("comps");
			for (int i = 0; i < objArray.length(); i++) {
				JSONObject objdata = objArray.getJSONObject(i);
				JSONArray headArray = objdata.getJSONArray("yVals");
				ArrayList<Entry> yVals1 = new ArrayList<Entry>();
				for (int j = 0; j < headArray.length(); j++) {
					if (j>APIContext.most_show_count)break;
					JSONObject yObj = headArray.getJSONObject(j);
					yVals1.add(new Entry(yObj.getInt("value"), j));
				}
				JSONArray xArray = objdata.getJSONArray("xVals");
				ArrayList<String> xVals = new ArrayList<String>();
				for (int j = 0; j < xArray.length(); j++) {
					if (j>APIContext.most_show_count)break;
					xVals.add(xArray.getString(j));
				}
				PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");
				dataSet.setSliceSpace(3f);
				dataSet.setSelectionShift(5f);
				ArrayList<Integer> colors = new ArrayList<Integer>();

				for (int c : ColorTemplate.VORDIPLOM_COLORS)
					colors.add(c);

				for (int c : ColorTemplate.JOYFUL_COLORS)
					colors.add(c);

				for (int c : ColorTemplate.COLORFUL_COLORS)
					colors.add(c);

				for (int c : ColorTemplate.LIBERTY_COLORS)
					colors.add(c);

				for (int c : ColorTemplate.PASTEL_COLORS)
					colors.add(c);

				colors.add(ColorTemplate.getHoloBlue());

				dataSet.setColors(colors);
				data = new PieData(xVals, dataSet);
				  data.setValueFormatter(new PercentFormatter());
			        data.setValueTextSize(11f);
			        data.setValueTextColor(Color.WHITE);
			}
			dataR.add(data);
		}

		return (T) dataR;
	}

}
