package com.ruisi.bi.app.parser;

import com.ruisi.bi.app.bean.Message;
import com.ruisi.bi.app.bean.MessageObj;
import com.ruisi.bi.app.bean.ReLoginBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by berly on 2016/8/14.
 */
public class Message2Parsar extends BaseParser {
    @Override
    public <T> T parse(String jsonStr) throws JSONException {
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }
        if (jsonStr!=null&&!jsonStr.equals("")) {
            if (jsonStr.contains("error")) {
                JSONObject jsonObject=new JSONObject(jsonStr);
                return (T) new ReLoginBean(jsonObject.optString("error"));
            }
        }

        JSONObject object = new JSONObject(jsonStr);
        MessageObj messageObj = new MessageObj();
        messageObj.hasNext = object.getBoolean("hasNext");
        List<Message> messages = new ArrayList<>();
        if (object.has("rows")) {
            JSONArray array = object.getJSONArray("rows");
            int count = array.length();

            for (int i = 0; i < count; i++) {
                Message message = new Message();
                JSONObject rowObj = array.getJSONObject(i);
                message.id = rowObj.getInt("id");
                message.datadate = rowObj.getString("datadate");
                message.title = rowObj.getString("title");
                message.state = rowObj.getString("state");
                message.pushType = rowObj.getString("pushType");
                message.crtdate = rowObj.getJSONObject("crtdate").getLong("time");
                messages.add(message);
            }
        }
        messageObj.rows = messages;

        return (T)messageObj;
    }
}
