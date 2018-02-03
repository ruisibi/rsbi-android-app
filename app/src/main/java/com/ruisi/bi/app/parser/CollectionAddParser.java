package com.ruisi.bi.app.parser;

import com.ruisi.bi.app.bean.ReLoginBean;

import org.json.JSONException;
import org.json.JSONObject;

public class CollectionAddParser extends BaseParser{
    @Override
    public <T> T parse(String jsonStr) throws JSONException {
        if (jsonStr!=null&&!jsonStr.equals("")) {
            JSONObject jsonObject=new JSONObject(jsonStr);
            return (T)jsonObject;
        }
        
        return null;
    }
}
