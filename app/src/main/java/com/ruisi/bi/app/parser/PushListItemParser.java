package com.ruisi.bi.app.parser;

import com.ruisi.bi.app.bean.PushListItem;
import com.ruisi.bi.app.bean.ReLoginBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by berlython on 16/5/13.
 */
public class PushListItemParser extends BaseParser {

    @Override
    public <T> T parse(String jsonStr) throws JSONException {

        List<PushListItem> items = new ArrayList<>();

        if (jsonStr != null && !jsonStr.equals("")) {
            if (jsonStr.contains("error")) {
                JSONObject jsonObject=new JSONObject(jsonStr);
                return (T) new ReLoginBean(jsonObject.optString("error"));
            } else {
                JSONArray array = new JSONArray(jsonStr);
                if(array.length() > 0){
                    for (int i = 0; i < array.length(); i++) {
                        PushListItem item = new PushListItem();
                        JSONObject obj = array.getJSONObject(i);
                        item.pushId = obj.getInt("push_id");
                        item.pushTitle =obj.getString("title");
                        item.crtdate = obj.getString("crtdate");
                        item.state = obj.getInt("state");
                        items.add(item);
                    }
                }
            }
        }

        return (T) items;
    }
}
