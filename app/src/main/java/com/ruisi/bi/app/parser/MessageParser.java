package com.ruisi.bi.app.parser;

import com.ruisi.bi.app.bean.MenuBean;
import com.ruisi.bi.app.bean.Message;
import com.ruisi.bi.app.bean.ReLoginBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by berlython on 16/6/7.
 */
public class MessageParser extends BaseParser {
    @Override
    public <T> T parse(String jsonStr) throws JSONException {
        List<Message> messages = new ArrayList<>();

        if (jsonStr!=null&&!jsonStr.equals("")) {
            if (jsonStr.contains("error")) {
                JSONObject jsonObject=new JSONObject(jsonStr);
                return (T) new ReLoginBean(jsonObject.optString("error"));
            }

            JSONArray array=new JSONArray(jsonStr);

            if(array.length()>0){
                for (int i = 0; i < array.length(); i++) {
                    Message message=new Message();
                    JSONObject obj=array.getJSONObject(i);
                    message.id = obj.getInt("id");
                    message.datadate = obj.getString("datadate");
                    message.title = obj.getString("title");
                    message.state = obj.getString("state");
                    message.pushType = obj.getString("pushType");
                    message.crtdate = obj.getJSONObject("crtdate").getLong("time");
                    messages.add(message);
                }
            }
        }

        return (T) messages;
    }
}
