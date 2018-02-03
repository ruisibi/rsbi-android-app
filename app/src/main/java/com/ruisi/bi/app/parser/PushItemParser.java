package com.ruisi.bi.app.parser;

import com.ruisi.bi.app.bean.PushItem;
import com.ruisi.bi.app.bean.ReLoginBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by berlython on 16/6/7.
 */
public class PushItemParser extends BaseParser {
    @Override
    public <T> T parse(String jsonStr) throws JSONException {

        PushItem pushItem = null;
        if (jsonStr != null && !jsonStr.equals("")) {
            if (jsonStr.contains("error")) {
                JSONObject jsonObject=new JSONObject(jsonStr);
                return (T) new ReLoginBean(jsonObject.optString("error"));
            } else {
                JSONObject object = new JSONObject(jsonStr);
                pushItem = new PushItem();

                pushItem.tid = object.getInt("tid");
                pushItem.pushType = object.getString("pushType");

                JSONObject dimObj = null;
                if (object.has("dim")) {
                    dimObj = object.getJSONObject("dim");
                }
                JSONArray kpiJsonArray = null;
                if (object.has("kpiJson")) {
                    kpiJsonArray = object.getJSONArray("kpiJson");
                }
                JSONObject jobObj = object.getJSONObject("job");
                if (dimObj != null) {
                    PushItem.Dim dim = pushItem.new Dim();
//                dim.type = dimObj.getString("type");
                    dim.id = dimObj.getInt("id");
                    dim.colname = dimObj.getString("colname");
                    dim.alias = dimObj.getString("alias");
                    dim.tname = dimObj.getString("tname");
                    dim.tableColKey = dimObj.getString("tableColKey");
                    dim.tableColName = dimObj.getString("tableColName");
                    if (dimObj.has("tableName")) {
                        dim.tableName = dimObj.getString("tableName");
                    }
                    dim.dimdesc = dimObj.getString("dimdesc");
                    dim.dimord = dimObj.getString("dimord");
                    dim.grouptype = dimObj.getString("grouptype");
                    dim.iscas = dimObj.getString("iscas");
                    dim.valType = dimObj.getString("valType");
                    dim.tid = dimObj.getInt("tid");
                    dim.dateformat = dimObj.getString("dateformat");

                    pushItem.dim = dim;
                }
                if (kpiJsonArray != null) {
                    int count = kpiJsonArray.length();
                    List<PushItem.KpiJsonItem> kpiJson = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        JSONObject kpiJsonObj = kpiJsonArray.getJSONObject(i);
                        PushItem.KpiJsonItem kpiJsonItem = pushItem.new KpiJsonItem();
                        kpiJsonItem.kpi_id = kpiJsonObj.getInt("kpi_id");
                        kpiJsonItem.kpi_name = kpiJsonObj.getString("kpi_name");
                        kpiJsonItem.col_name = kpiJsonObj.getString("col_name");
                        kpiJsonItem.aggre = kpiJsonObj.getString("aggre");
                        kpiJsonItem.fmt = kpiJsonObj.getString("fmt");
                        kpiJsonItem.alias = kpiJsonObj.getString("alias");
                        kpiJsonItem.tid = kpiJsonObj.getInt("tid");
                        kpiJsonItem.unit = kpiJsonObj.getString("unit");
                        kpiJsonItem.rate = kpiJsonObj.getString("rate");
                        if (kpiJsonObj.has("opt")){
                            kpiJsonItem.opt = kpiJsonObj.getString("opt");
                        }
                        if (kpiJsonObj.has("val1")) {
                            kpiJsonItem.val1 = kpiJsonObj.getString("val1");
                        }
                        if (kpiJsonObj.has("val2")) {
                            kpiJsonItem.val2 = kpiJsonObj.getString("val2");
                        }
                        kpiJson.add(kpiJsonItem);
                    }
                    pushItem.kpiJson = kpiJson;
                }

                PushItem.Job job = pushItem.new Job();
                if (pushItem.pushType.equals("month")) {
                    job.day = jobObj.getString("day");
                }
                job.hour = jobObj.getString("hour");
                job.minute = jobObj.getString("minute");
                pushItem.job = job;
            }
        }
        return (T)pushItem;
    }
}
