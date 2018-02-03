package com.ruisi.bi.app.parser;

import com.ruisi.bi.app.bean.CollectionBean;
import com.ruisi.bi.app.bean.ReLoginBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CollectionParser extends BaseParser {
    @Override
    public <T> T parse(String jsonStr) throws JSONException {
        List<CollectionBean> list = new ArrayList<>();
        if (!StringUtil.isBlank(jsonStr)) {
            if (jsonStr.contains("error")) {
                JSONObject jsonObject=new JSONObject(jsonStr);
                return (T) new ReLoginBean(jsonObject.optString("error"));
            }
            JSONArray array = new JSONArray(jsonStr);
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject obj = array.getJSONObject(i);
                CollectionBean collection = new CollectionBean();
                collection.rid = obj.getString("rid");
                collection.title = obj.getString("title");
                collection.url = obj.getString("url");
                list.add(collection);
            }

        }
        return (T)list;
    }
}
