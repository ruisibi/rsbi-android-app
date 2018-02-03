package com.ruisi.bi.app.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisi.bi.app.bean.MenuBean;
import com.ruisi.bi.app.bean.ReLoginBean;

public class MenuParser extends BaseParser{

    @Override
    public <T> T parse(String jsonStr) throws JSONException {

        List<MenuBean> menus = new ArrayList<>();

        if (jsonStr!=null&&!jsonStr.equals("")) {
            if (jsonStr.contains("error")) {
                JSONObject jsonObject=new JSONObject(jsonStr);
                return (T) new ReLoginBean(jsonObject.optString("error"));
            }

            JSONArray array=new JSONArray(jsonStr);

            if(array.length()>0){
                for (int i = 0; i < array.length(); i++) {
                    MenuBean menuBean=new MenuBean();
                    JSONObject obj=array.getJSONObject(i);
                    menuBean.name=obj.getString("name");
                    menuBean.note=obj.getString("note");
                    menuBean.pic=obj.getString("pic");
                    menuBean.uri=obj.getString("url");
                    menuBean.id = obj.getInt("id");
                    menus.add(menuBean);
                }
            }
        }

        return (T) menus;
    }

}
