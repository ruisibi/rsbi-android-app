package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.ThemeBean;
import com.ruisi.bi.app.bean.ThemeChildBean;

public class ThemeParser extends BaseParser{

	@Override
	public <T> T parse(String jsonStr) throws JSONException {
		if (jsonStr!=null&&!jsonStr.equals("")) {
			if (jsonStr.contains("error")) {
				JSONObject jsonObject=new JSONObject(jsonStr);
				return (T) new ReLoginBean(jsonObject.optString("error"));
			}
		}
		JSONArray array=new JSONArray(jsonStr);
		ArrayList<ThemeBean> themes=null;
		if(array.length()>0){
			themes=new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				ThemeBean themeBean=new ThemeBean();
				JSONObject obj=array.getJSONObject(i);
				themeBean.id=obj.getInt("id");
				themeBean.text=obj.getString("text");
				themeBean.tp=obj.getString("tp");
				themeBean.dsource=obj.getString("dsource");
				themeBean.tname=obj.getString("tname");
				themeBean.ord=obj.getString("ord");
				themeBean.ttype=obj.getInt("ttype");
				themeBean.pid=obj.getInt("pid");
				themeBean.tid=obj.getInt("tid");
				JSONArray childs=obj.getJSONArray("children");
				ArrayList<ThemeChildBean> childBeans=new ArrayList<ThemeChildBean>();
				for (int j = 0; j < childs.length(); j++) {
					JSONObject child=childs.getJSONObject(j);
					ThemeChildBean childBean=new ThemeChildBean();
					childBean.id=child.getInt("id");
					childBean.text=child.getString("text");
					childBean.tp=child.getString("tp");
					childBean.dsource=child.getString("dsource").equals("null")?"":child.getString("dsource");
					childBean.tname=child.getString("tname");
					childBean.ord=child.getString("ord");
					childBean.ttype=child.getInt("ttype");
					childBean.pid=child.getInt("pid");
					childBean.tid=child.getInt("tid");
					childBeans.add(childBean);
				}
				themeBean.childs=childBeans;
				themes.add(themeBean);
			}
		}
		return (T) themes;
	}

}
