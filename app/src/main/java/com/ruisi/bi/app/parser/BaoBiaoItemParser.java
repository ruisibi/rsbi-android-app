package com.ruisi.bi.app.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.BaobiaoItemBean;
import com.ruisi.bi.app.bean.ReLoginBean;

public class BaoBiaoItemParser extends BaseParser {

    @Override
    public <T> T parse(String jsonStr) throws JSONException {
        if (jsonStr!=null&&!jsonStr.equals("")) {
            if (jsonStr.contains("error")) {
                JSONObject jsonObject=new JSONObject(jsonStr);
                return (T) new ReLoginBean(jsonObject.optString("error"));
            }
        }
        JSONArray array = new JSONArray(jsonStr);
        ArrayList<BaobiaoItemBean> data = null;
        data = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            BaobiaoItemBean bean = new BaobiaoItemBean();
            bean.rid = obj.getString("rid");
            bean.title = obj.getString("title");
            bean.url = obj.getString("url");
            bean.cataName = obj.getString("cataName");
            bean.iscollect = obj.getInt("iscollect");
            data.add(bean);
        }

        return (T) data;
    }

}
