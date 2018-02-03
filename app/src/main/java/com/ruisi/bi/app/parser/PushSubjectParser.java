package com.ruisi.bi.app.parser;

import com.ruisi.bi.app.bean.PushSubject;
import com.ruisi.bi.app.bean.ReLoginBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by berlython on 16/5/27.
 */
public class PushSubjectParser extends BaseParser {
    @Override
    public <T> T parse(String jsonStr) throws JSONException {
        List<PushSubject> items = new ArrayList<>();

        if (jsonStr != null && !jsonStr.equals("")) {
            if (jsonStr.contains("error")) {
                JSONObject jsonObject=new JSONObject(jsonStr);
                return (T) new ReLoginBean(jsonObject.optString("error"));
            } else {
                JSONArray array = new JSONArray(jsonStr);
                if(array.length() > 0){
                    for (int i = 0; i < array.length(); i++) {
                        PushSubject item = new PushSubject();
                        JSONObject obj = array.getJSONObject(i);
                        item.tId = obj.getInt("tid");
                        item.tName =obj.getString("tname");
                        item.tDesc = obj.getString("tdesc");
                        items.add(item);
                    }
                }
            }
        }

        return (T) items;
    }
}
