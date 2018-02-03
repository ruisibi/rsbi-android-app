package com.ruisi.bi.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ruisi.bi.app.adapter.WeiduAdapter;
import com.ruisi.bi.app.adapter.WeiduShowAdapter;
import com.ruisi.bi.app.adapter.ZhibiaoAdapter;
import com.ruisi.bi.app.adapter.ZhibiaoShowAdapter;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.ReSetSaveDataBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.ShaixuanBean;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.ZhibiaoBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.ReSetSaveDataParser;
import com.ruisi.bi.app.parser.SaveSelectDataParser;
import com.ruisi.bi.app.parser.ShaixuanParser;
import com.ruisi.bi.app.parser.WeiduParser;
import com.ruisi.bi.app.parser.ZhibiaoParser;
import com.ruisi.bi.app.view.LoadingDialog;
import com.ruisi.bi.app.view.MyPopwindow;
import com.ruisi.bi.app.view.ShaixuanPopwindow;

public class ConditionAcitivity extends Activity implements
        ServerCallbackInterface, OnClickListener {

    public int ThemeId, subjectid;
    public String subjectname;
    private String zhibiaoUUID = "", weiduUUID = "", saveSelectDataUUID = "",
            getsaveSelectDataUUID = "";
    private Button weidu, weidu_hang, weidu_lie, zhibiao;
    private List<WeiduBean> weidus = new ArrayList<>();
    private List<ZhibiaoBean> zhibiaos = new ArrayList<>();
    private WeiduAdapter weiduAdapter;
    private ZhibiaoAdapter zhibiaoAdapter;
    private WeiduShowAdapter weiduShowAdapter, weiduShowHangAdapter,
            weiduShowLieAdapter;
    private ZhibiaoShowAdapter zhibiaoShowAdapter;
    private ListView weidu_lv, weidu_hang_lv, weidu_lie_lv, zhibiao_lv;

    private List<WeiduBean> weidusShow = new ArrayList<>();
    private List<WeiduBean> weidusHangShow = new ArrayList<>();
    private List<WeiduBean> weidusLieShow = new ArrayList<>();
    private List<ZhibiaoBean> zhibiaosShow = new ArrayList<>();
    private ImageView back;
    private boolean isOverWeidu, isOverZhibiao;
    private boolean isUpdata;
    private int id;
    private boolean isFromSaveDataActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.act_condition_fragment_layout);
        id = getIntent().getIntExtra("id", 0);
        APIContext.id=id;
        if (id != 0) {
            isUpdata = true;
            isFromSaveDataActivity=true;
            sendGetSaveData();
        } else {
            ThemeId = getIntent().getIntExtra("ThemeId", 0);
            subjectname = getIntent().getStringExtra("subjectname");
            subjectid = getIntent().getIntExtra("subjectid", 0);
            LoadingDialog.createLoadingDialog(this);
            sendZhibiaoRequest();
            sendWeiduRequest();
            
        }
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        weidu = (Button) findViewById(R.id.ConditionFragment_Weidu_actv);
        weidu_hang = (Button) findViewById(R.id.ConditionFragment_Weidu_hang_actv);
        weidu_lie = (Button) findViewById(R.id.ConditionFragment_Weidu_lie_actv);
        zhibiao = (Button) findViewById(R.id.ConditionFragment_Zhibiao_actv);

        weidu_lv = (ListView) findViewById(R.id.ConditionFragment_Weidu_listview);
        weidu_hang_lv = (ListView) findViewById(R.id.ConditionFragment_Weidu_hang_listview);
        weidu_lie_lv = (ListView) findViewById(R.id.ConditionFragment_Weidu_lie_listview);
        zhibiao_lv = (ListView) findViewById(R.id.ConditionFragment_Weidu_zhibiao_listview);

        weiduShowAdapter = new WeiduShowAdapter(this, weidusShow, 0);
        weidu_lv.setAdapter(weiduShowAdapter);
        zhibiaoShowAdapter = new ZhibiaoShowAdapter(this, zhibiaosShow);
        zhibiao_lv.setAdapter(zhibiaoShowAdapter);
        weiduShowHangAdapter = new WeiduShowAdapter(this, weidusHangShow, 1);
        weidu_hang_lv.setAdapter(weiduShowHangAdapter);
        weiduShowLieAdapter = new WeiduShowAdapter(this, weidusLieShow, 2);
        weidu_lie_lv.setAdapter(weiduShowLieAdapter);

        zhibiao.setOnClickListener(this);
        weidu.setOnClickListener(this);
        weidu_hang.setOnClickListener(this);
        weidu_lie.setOnClickListener(this);
        weiduAdapter = new WeiduAdapter(this, weidus);
        zhibiaoAdapter = new ZhibiaoAdapter(this, zhibiaos);

        // if (UserMsg.getFenxiId(ThemeId)!=-1) {
        // isUpdata=true;
        // sendGetSaveData();
        // }
    }

    private void sendGetSaveData() {
        LoadingDialog.createLoadingDialog(this);
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.getfenxiupdata;
        rv.parser = new ReSetSaveDataParser();
        rv.Type = APIContext.GET;
        getsaveSelectDataUUID = UUID.randomUUID().toString();
        rv.uuId = getsaveSelectDataUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", id + "");
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    private void sendZhibiaoRequest() {
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.Zhibiao;
        // rv.modulePath = APIContext.Login;
        rv.parser = new ZhibiaoParser();
        rv.Type = APIContext.GET;
        zhibiaoUUID = UUID.randomUUID().toString();
        rv.uuId = zhibiaoUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tableid", ThemeId + "");
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    private void sendWeiduRequest() {
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.Weidu;
        rv.parser = new WeiduParser();
        rv.Type = APIContext.GET;
        weiduUUID = UUID.randomUUID().toString();
        rv.uuId = weiduUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tableid", ThemeId + "");
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (object instanceof ReLoginBean) {
            Toast.makeText(this, "用户未登录！", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
           finish();
            return;
        }
        if (saveSelectDataUUID.equals(uuid)) {
            if (object != null && !object.equals("") && !isUpdata) {
                id = Integer.parseInt((String) object);
                isUpdata = true;
                Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
            }
            LoadingDialog.dimmissLoading();
            return;
        }
        if (shaixuan.equals(uuid)) {
            ShaixuanBean bean = (ShaixuanBean) object;
            if (bean.type.equals("other")) {
                if (shaixuan_isHand && vat_hang != null) {
                    String[] hangs = vat_hang.split(",");
                    for (int i = 0; i < hangs.length; i++) {
                        for (int j = 0; j < bean.startOptions.size(); j++) {
                            if (hangs[i].equals(bean.startOptions.get(j).value)) {
                                bean.startOptions.get(j).isChecked = true;
                            }
                        }
                    }
                }
                if (!shaixuan_isHand && vat_lie != null) {
                    String[] hangs = vat_lie.split(",");
                    for (int i = 0; i < hangs.length; i++) {
                        for (int j = 0; j < bean.startOptions.size(); j++) {
                            if (hangs[i].equals(bean.startOptions.get(j).value)) {
                                bean.startOptions.get(j).isChecked = true;
                            }
                        }
                    }
                }
                ShaixuanPopwindow.getShaixuanPopwindow(this, bean.startOptions,
                        back, head_text);
            } else if (bean.type.equals("month")) {
                if (shaixuan_isHand && shaixuan_time_hang_start != null) {
                    for (int i = 0; i < bean.startOptions.size(); i++) {
                        if (shaixuan_time_hang_start.equals(bean.startOptions
                                .get(i).value)) {
                            bean.startOptions.get(i).isChecked = true;
                        }
                    }
                    for (int i = 0; i < bean.endOptions.size(); i++) {
                        if (shaixuan_time_hang_end.equals(bean.endOptions
                                .get(i).value)) {
                            bean.endOptions.get(i).isChecked = true;
                        }
                    }
                }
                if (!shaixuan_isHand && shaixuan_time_lie_start != null) {
                    for (int i = 0; i < bean.startOptions.size(); i++) {
                        if (shaixuan_time_lie_start.equals(bean.startOptions
                                .get(i).value)) {
                            bean.startOptions.get(i).isChecked = true;
                        }
                    }
                    for (int i = 0; i < bean.endOptions.size(); i++) {
                        if (shaixuan_time_lie_end
                                .equals(bean.endOptions.get(i).value)) {
                            bean.endOptions.get(i).isChecked = true;
                        }
                    }
                }
                ShaixuanPopwindow.getShaixuanTimePopwindow(this, bean, back,
                        head_text, shaixuan_isHand);
            } else if (bean.type.equals("day")) {
                if (shaixuan_isHand&&shaixuan_time_hang_start!=null) {
                    bean.startName=shaixuan_time_hang_start;
                    bean.endName=shaixuan_time_hang_end;
                }
                if (!shaixuan_isHand&&shaixuan_time_lie_start!=null) {
                    bean.startName=shaixuan_time_lie_start;
                    bean.endName=shaixuan_time_lie_end;
                }
                ShaixuanPopwindow.getShaixuanDatPopwindow(this, bean, back,
                        head_text, shaixuan_isHand);
            }
            LoadingDialog.dimmissLoading();
            return;
        }

        if (weiduUUID.equals(uuid)) {
            isOverWeidu = true;
            weidus.clear();
            weidus.addAll((Collection<? extends WeiduBean>) object);
            weiduAdapter.notifyDataSetChanged();

        }
        if (zhibiaoUUID.equals(uuid)) {
            isOverZhibiao = true;
            zhibiaos.clear();
            zhibiaos.addAll((Collection<? extends ZhibiaoBean>) object);
            zhibiaoAdapter.notifyDataSetChanged();
        }
        if (getsaveSelectDataUUID.equals(uuid)) {
            ReSetSaveDataBean bean = (ReSetSaveDataBean) object;
            ThemeId = bean.tid;
            subjectname = bean.subjectname;
            subjectid = bean.subjectid;
            APIContext.ThemeId=ThemeId;
            resetShowData(bean);
            sendZhibiaoRequest();
            sendWeiduRequest();
        }
        if (isOverZhibiao && isOverWeidu) {
            LoadingDialog.dimmissLoading();
            return;
        }

    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (weiduUUID.equals(uuid)) {
            isOverWeidu = true;
        }
        if (zhibiaoUUID.equals(uuid)) {
            isOverZhibiao = true;
        }
        if (getsaveSelectDataUUID.equals(uuid)) {
            LoadingDialog.dimmissLoading();
        }
        if (isOverZhibiao && isOverWeidu)
            LoadingDialog.dimmissLoading();
        if (shaixuan.equals(uuid))
            LoadingDialog.dimmissLoading();
        Toast.makeText(this, errorMessage.getErrorDes(), Toast.LENGTH_SHORT).show();
    }

    public void resetShowData(ReSetSaveDataBean bean) {
        if (bean.params != null) {
            weidusShow.add(bean.params);
            weiduShowAdapter.notifyDataSetChanged();
        }
        if (bean.cols != null) {
            weidusLieShow.add(bean.cols);
            vat_lie=bean.cols.vals;
            shaixuan_time_lie_type=bean.cols.selectType;
            if (shaixuan_time_lie_type!=null&&shaixuan_time_lie_type.equals("month")){
                shaixuan_time_lie_start=bean.cols.startmt;
                shaixuan_time_lie_end=bean.cols.endmt;
            }else if (shaixuan_time_lie_type!=null&&shaixuan_time_lie_type.equals("day")) {
                shaixuan_time_lie_start=bean.cols.startdt;
                shaixuan_time_lie_end=bean.cols.enddt;
            }
            weiduShowLieAdapter.notifyDataSetChanged();
        }
        if (bean.rows != null) {
            weidusHangShow.add(bean.rows);
            vat_hang=bean.rows.vals;
            shaixuan_time_hang_type=bean.rows.selectType;
            if (shaixuan_time_hang_type!=null&&shaixuan_time_hang_type.equals("month")){
                shaixuan_time_hang_start=bean.rows.startmt;
                shaixuan_time_hang_end=bean.rows.endmt;
            }else if (shaixuan_time_hang_type!=null&&shaixuan_time_hang_type.equals("day")) {
                shaixuan_time_hang_start=bean.rows.startdt;
                shaixuan_time_hang_end=bean.rows.enddt;
            }
            weiduShowHangAdapter.notifyDataSetChanged();
        }
        if (bean.kpiJson != null) {
            zhibiaosShow.add(bean.kpiJson);
            zhibiaoShowAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.ConditionFragment_Zhibiao_actv:
            if (zhibiaos.size() > 0) {
                MyPopwindow.getSaveNotePoPwindow(this, zhibiao, zhibiaoAdapter,
                        3);
            }
            break;
        case R.id.ConditionFragment_Weidu_actv:
            if (weidus.size() > 0) {
                for (int i = 0; i < weidus.size(); i++) {
                    weidus.get(i).isChecked = false;
                    for (int j = 0; j < weidusShow.size(); j++) {
                        if (weidus.get(i).text.equals(weidusShow.get(j).text))
                            weidus.get(i).isChecked = true;
                    }
                }
                weiduAdapter.setType(0);
                weiduAdapter.notifyDataSetChanged();
                MyPopwindow.getSaveNotePoPwindow(this, weidu, weiduAdapter, 0);
            }
            break;
        case R.id.ConditionFragment_Weidu_hang_actv:
            if (weidus.size() > 0) {
                for (int i = 0; i < weidus.size(); i++) {
                    weidus.get(i).isChecked = false;
                    for (int j = 0; j < weidusHangShow.size(); j++) {
                        if (weidus.get(i).text
                                .equals(weidusHangShow.get(j).text))
                            weidus.get(i).isChecked = true;
                    }
                    weidus.get(i).canClicked = false;
                    for (int j = 0; j < weidusLieShow.size(); j++) {
                        if (weidus.get(i).text
                                .equals(weidusLieShow.get(j).text) || weidus.get(i).grouptype.equals(weidusLieShow.get(j).grouptype))
                            weidus.get(i).canClicked = true;
                    }
                }
                weiduAdapter.setType(1);
                weiduAdapter.notifyDataSetChanged();
                MyPopwindow.getSaveNotePoPwindow(this, weidu, weiduAdapter, 1);
            }
            break;
        case R.id.ConditionFragment_Weidu_lie_actv:
            if (weidus.size() > 0) {
                for (int i = 0; i < weidus.size(); i++) {
                    weidus.get(i).isChecked = false;
                    for (int j = 0; j < weidusLieShow.size(); j++) {
                        if (weidus.get(i).text
                                .equals(weidusLieShow.get(j).text))
                            weidus.get(i).isChecked = true;
                    }
                    weidus.get(i).canClicked = false;
                    for (int j = 0; j < weidusHangShow.size(); j++) {
                        if (weidus.get(i).text
                                .equals(weidusHangShow.get(j).text) || weidus.get(i).grouptype.equals(weidusHangShow.get(j).grouptype))
                            weidus.get(i).canClicked = true;
                    }
                }
                weiduAdapter.setType(2);
                weiduAdapter.notifyDataSetChanged();
                MyPopwindow.getSaveNotePoPwindow(this, weidu, weiduAdapter, 2);
            }
            break;
        case R.id.back:
            if (isFromSaveDataActivity) {
                startActivity(new Intent(this, AnalysisActivity.class));
            }
            this.finish();
            break;
        case R.id.save_bt:
            if (isUpdata) {
                try {
                    saveSelectData(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                showDialog();
            }
            
            break;
        case R.id.tofrom:
//            if (!AppContext.isNetworkConnected(this)) {
//                FormActivity.startThis(this, null, ThemeId + "","");
//                return;
//            }
            if (zhibiaosShow.size() > 0
                    && (weidusHangShow.size() > 0 || weidusLieShow.size() > 0)) {
                try {
                    FormActivity.startThis(this, getConditionJson(), ThemeId
                            + "",subjectname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "至少选择一个指标及一个行标签或列标签", Toast.LENGTH_SHORT).show();
            }
            break;
        case R.id.totu:
//            if (!AppContext.isNetworkConnected(this)) {
//                TuActivity.startThis(this, null, ThemeId + "");
//                return;
//            }
            if (zhibiaosShow.size() > 0
                    && (weidusHangShow.size() > 0 || weidusLieShow.size() > 0)) {
                try {
                    String zhibiaoText = zhibiaosShow.get(0).text;
                    if (!StringUtil.isBlank(zhibiaosShow.get(0).unit)) {
                        zhibiaoText = zhibiaoText + "(" + zhibiaosShow.get(0).unit + ")";
                    }
                    TuActivity.startThis(this, getChatJson(), ThemeId + "",subjectname, zhibiaoText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "至少选择一个指标及一个行标签或列标签", Toast.LENGTH_SHORT).show();
            }
            break;
        default:
            break;
        }
    }

    public void saveSelectData(String name) throws JSONException {
        LoadingDialog.createLoadingDialog(this);
        sendSaveFenxiRequest(isUpdata, creatSaveJson(isUpdata),name);
    }

    private void sendSaveFenxiRequest(boolean flag, String json,String name) {
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        if (flag) {

            rv.functionPath = APIContext.fenxiupdata;
        } else {
            rv.functionPath = APIContext.fenxisave;
        }
        rv.parser = new SaveSelectDataParser();
        rv.Type = APIContext.POST;
        saveSelectDataUUID = UUID.randomUUID().toString();
        rv.uuId = saveSelectDataUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pageInfo", json);
        if (flag) {
            map.put("id", id + "");
        } else {
            map.put("pageName", name);
        }
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    public String creatSaveJson(boolean isUpdata) throws JSONException {
        JSONObject objALL = new JSONObject();
        JSONObject obj = new JSONObject();
        JSONArray zhibiaoArray = new JSONArray();
        for (int i = 0; i < zhibiaosShow.size(); i++) {
            JSONObject zhibiaoObj = new JSONObject();
            ZhibiaoBean zhibiaoBean = zhibiaosShow.get(i);
            zhibiaoObj.put("tid", zhibiaoBean.tid);
            zhibiaoObj.put("unit", zhibiaoBean.unit);
            zhibiaoObj.put("rate", zhibiaoBean.rate);
            zhibiaoObj.put("alias", zhibiaoBean.alias);
            zhibiaoObj.put("fmt", zhibiaoBean.fmt);
            zhibiaoObj.put("aggre", zhibiaoBean.aggre);
            zhibiaoObj.put("col_name", zhibiaoBean.col_name);
            zhibiaoObj.put("kpi_name", zhibiaoBean.text);
            zhibiaoObj.put("kpi_id", zhibiaoBean.col_id);
            zhibiaoArray.put(zhibiaoObj);
        }
        obj.put("kpiJson", zhibiaoArray);

        JSONObject objTable = new JSONObject();
        JSONArray arrayHang = new JSONArray();
        for (int i = 0; i < weidusHangShow.size(); i++) {
            JSONObject weidusHangObj = new JSONObject();
            WeiduBean weiduhangObj = weidusHangShow.get(i);
            weidusHangObj.put("tid", weiduhangObj.tid);
            weidusHangObj.put("valType", weiduhangObj.valType);
            weidusHangObj.put("iscas", weiduhangObj.iscas);
            weidusHangObj.put("grouptype", weiduhangObj.grouptype);
            weidusHangObj.put("dim_name", weiduhangObj.dim_name);
            weidusHangObj.put("dimord", weiduhangObj.dimord);
            weidusHangObj.put("tableName", weiduhangObj.tableName);
            weidusHangObj.put("tableColName", weiduhangObj.tableColName);
            weidusHangObj.put("tableColKey", weiduhangObj.tableColKey);
            weidusHangObj.put("tname", weiduhangObj.tname);
            weidusHangObj.put("alias", weiduhangObj.alias);
            if (vat_hang != null && !vat_hang.equals("")) {
                weidusHangObj.put("vals", vat_hang);
            }
            if (shaixuan_time_hang_end != null
                    && !shaixuan_time_hang_end.equals("")) {
                if (shaixuan_time_hang_type.equals("month")) {
                    weidusHangObj.put("startmt", shaixuan_time_hang_start);
                    weidusHangObj.put("endmt", shaixuan_time_hang_end);
                    weidusHangObj.put("filtertype", 1);
                } else if (shaixuan_time_hang_type.equals("day")) {
                    weidusHangObj.put("startdt", shaixuan_time_hang_start);
                    weidusHangObj.put("enddt", shaixuan_time_hang_end);
                    weidusHangObj.put("filtertype", 1);
                }
            }
            weidusHangObj.put("id", weiduhangObj.col_id);
            weidusHangObj.put("dimdesc", weiduhangObj.text);
            weidusHangObj.put("colname", weiduhangObj.col_name);
            weidusHangObj.put("type", weiduhangObj.dim_type);
            arrayHang.put(weidusHangObj);
        }
        objTable.put("rows", arrayHang);

        JSONArray arrayLie = new JSONArray();
        for (int i = 0; i < weidusLieShow.size(); i++) {
            JSONObject weidusLieObj = new JSONObject();
            WeiduBean weiduLieObj = weidusLieShow.get(i);
            weidusLieObj.put("tid", weiduLieObj.tid);
            weidusLieObj.put("valType", weiduLieObj.valType);
            weidusLieObj.put("iscas", weiduLieObj.iscas);
            weidusLieObj.put("grouptype", weiduLieObj.grouptype);
            weidusLieObj.put("dim_name", weiduLieObj.dim_name);
            weidusLieObj.put("dimord", weiduLieObj.dimord);
            weidusLieObj.put("tableName", weiduLieObj.tableName);
            weidusLieObj.put("tableColName", weiduLieObj.tableColName);
            weidusLieObj.put("tableColKey", weiduLieObj.tableColKey);
            weidusLieObj.put("tname", weiduLieObj.tname);
            weidusLieObj.put("name", weiduLieObj.dim_name);
            weidusLieObj.put("id", weiduLieObj.col_id);
            weidusLieObj.put("alias", weiduLieObj.alias);
            if (vat_lie != null && !vat_lie.equals("")) {
                weidusLieObj.put("vals", vat_lie);
            }
            if (shaixuan_time_lie_end != null
                    && !shaixuan_time_lie_end.equals("")) {
                if (shaixuan_time_lie_type.equals("month")) {
                    weidusLieObj.put("startmt", shaixuan_time_lie_start);
                    weidusLieObj.put("endmt", shaixuan_time_lie_end);
                    weidusLieObj.put("filtertype", 1);
                } else if (shaixuan_time_lie_type.equals("day")) {
                    weidusLieObj.put("startdt", shaixuan_time_lie_start);
                    weidusLieObj.put("enddt", shaixuan_time_lie_end);
                    weidusLieObj.put("filtertype", 1);
                }
            }
            weidusLieObj.put("dimdesc", weiduLieObj.text);
            weidusLieObj.put("colname", weiduLieObj.col_name);
            weidusLieObj.put("type", weiduLieObj.dim_type);
            arrayLie.put(weidusLieObj);
        }
        JSONObject tmpJsonObject = new JSONObject();
        tmpJsonObject.put("id", "kpi");
        tmpJsonObject.put("type", "kpiOther");
        arrayLie.put(tmpJsonObject);
        objTable.put("cols", arrayLie);
        obj.put("tableJson", objTable);
        objALL.put("table", obj);
        JSONArray WeiduArray = new JSONArray();
        for (int i = 0; i < weidusShow.size(); i++) {
            JSONObject weidusObj = new JSONObject();
            WeiduBean weiduLieObj = weidusShow.get(i);
            weidusObj.put("id", weiduLieObj.col_id);
            weidusObj.put("name", weiduLieObj.text);
            weidusObj.put("type", weiduLieObj.dim_type);
            weidusObj.put("colname", weiduLieObj.col_name);
            weidusObj.put("tname", weiduLieObj.tname);
            weidusObj.put("tid", weiduLieObj.tid);
            weidusObj.put("valType", weiduLieObj.valType);
            weidusObj.put("tableName", weiduLieObj.tableName);
            weidusObj.put("tableColKey", weiduLieObj.tableColKey);
            weidusObj.put("tableColName", weiduLieObj.tableColName);
            weidusObj.put("dimord", weiduLieObj.dimord);
            weidusObj.put("grouptype", weiduLieObj.grouptype);
            weidusObj.put("alias", weiduLieObj.alias);
            WeiduArray.put(weidusObj);
        }

        objALL.put("params", WeiduArray);
        objALL.put("subjectid", subjectid);
        objALL.put("subjectname", subjectname);
        objALL.put("tid", ThemeId);
        if (isUpdata)
            objALL.put("id", id);
        return objALL.toString();
    }

    public void setListViewData(int flag) {
        if (flag == 0) {
            weidusShow.clear();
            for (int i = 0; i < weidus.size(); i++) {
                WeiduBean weiduBean = weidus.get(i);
                if (weiduBean.isChecked)
                    weidusShow.add(weiduBean);
            }
            weiduShowAdapter.notifyDataSetChanged();
        } else if (flag == 1) {
            weidusHangShow.clear();
            for (int i = 0; i < weidus.size(); i++) {
                WeiduBean weiduBean = weidus.get(i);
                if (weiduBean.isChecked)
                    weidusHangShow.add(weiduBean);
            }
            weiduShowHangAdapter.notifyDataSetChanged();
        } else if (flag == 2) {
            weidusLieShow.clear();
            for (int i = 0; i < weidus.size(); i++) {
                WeiduBean weiduBean = weidus.get(i);
                if (weiduBean.isChecked)
                    weidusLieShow.add(weiduBean);
            }
            weiduShowLieAdapter.notifyDataSetChanged();
        } else if (flag == 3) {
            zhibiaosShow.clear();
            for (int i = 0; i < zhibiaos.size(); i++) {
                ZhibiaoBean weiduBean = zhibiaos.get(i);
                if (weiduBean.isChecked)
                    zhibiaosShow.add(weiduBean);
            }
            zhibiaoShowAdapter.notifyDataSetChanged();
        }
    }

    public String getConditionJson() throws JSONException {
        JSONObject objALL = new JSONObject();
        JSONObject obj = new JSONObject();
        JSONArray zhibiaoArray = new JSONArray();
        for (int i = 0; i < zhibiaosShow.size(); i++) {
            JSONObject zhibiaoObj = new JSONObject();
            ZhibiaoBean zhibiaoBean = zhibiaosShow.get(i);
            zhibiaoObj.put("tid", zhibiaoBean.tid);
            zhibiaoObj.put("unit", zhibiaoBean.unit);
            zhibiaoObj.put("rate", zhibiaoBean.rate);
            zhibiaoObj.put("alias", zhibiaoBean.alias);
            zhibiaoObj.put("fmt", zhibiaoBean.fmt);
            zhibiaoObj.put("col_name", zhibiaoBean.col_name);
            zhibiaoObj.put("aggre", zhibiaoBean.aggre);
            zhibiaoObj.put("kpi_name", zhibiaoBean.text);
            zhibiaoObj.put("kpi_id", zhibiaoBean.col_id);
            zhibiaoObj.put("alias", zhibiaoBean.alias);
            zhibiaoArray.put(zhibiaoObj);
        }
        obj.put("kpiJson", zhibiaoArray);

        JSONObject objTable = new JSONObject();
        JSONArray arrayHang = new JSONArray();
        for (int i = 0; i < weidusHangShow.size(); i++) {
            JSONObject weidusHangObj = new JSONObject();
            WeiduBean weiduhangObj = weidusHangShow.get(i);
            weidusHangObj.put("tid", weiduhangObj.tid);
            weidusHangObj.put("valType", weiduhangObj.valType);
            weidusHangObj.put("iscas", weiduhangObj.iscas);
            weidusHangObj.put("grouptype", weiduhangObj.grouptype);
            weidusHangObj.put("dim_name", weiduhangObj.dim_name);
            weidusHangObj.put("dimord", weiduhangObj.dimord);
            weidusHangObj.put("tableName", weiduhangObj.tableName);
            weidusHangObj.put("tableColName", weiduhangObj.tableColName);
            weidusHangObj.put("tableColKey", weiduhangObj.tableColKey);
            weidusHangObj.put("tname", weiduhangObj.tname);
            if (vat_hang != null && !vat_hang.equals("")) {
                weidusHangObj.put("vals", vat_hang);
            }
            if (shaixuan_time_hang_end != null
                    && !shaixuan_time_hang_end.equals("")) {
                if (shaixuan_time_hang_type.equals("month")) {
                    weidusHangObj.put("startmt", shaixuan_time_hang_start);
                    weidusHangObj.put("endmt", shaixuan_time_hang_end);
                    weidusHangObj.put("filtertype", 1);
                } else if (shaixuan_time_hang_type.equals("day")) {
                    weidusHangObj.put("startdt", shaixuan_time_hang_start);
                    weidusHangObj.put("enddt", shaixuan_time_hang_end);
                    weidusHangObj.put("filtertype", 1);
                }
            }
            weidusHangObj.put("id", weiduhangObj.col_id);
            weidusHangObj.put("dimdesc", weiduhangObj.text);
            weidusHangObj.put("colname", weiduhangObj.col_name);
            weidusHangObj.put("type", weiduhangObj.dim_type);

            weidusHangObj.put("dateformat", weiduhangObj.dateformat);
            weidusHangObj.put("alias", weiduhangObj.alias);

            arrayHang.put(weidusHangObj);
        }
        objTable.put("rows", arrayHang);

        JSONArray arrayLie = new JSONArray();
        for (int i = 0; i < weidusLieShow.size(); i++) {
            JSONObject weidusLieObj = new JSONObject();
            WeiduBean weiduLieObj = weidusLieShow.get(i);
            weidusLieObj.put("tid", weiduLieObj.tid);
            weidusLieObj.put("valType", weiduLieObj.valType);
            weidusLieObj.put("iscas", weiduLieObj.iscas);
            weidusLieObj.put("grouptype", weiduLieObj.grouptype);
            weidusLieObj.put("dim_name", weiduLieObj.dim_name);
            weidusLieObj.put("dimord", weiduLieObj.dimord);
            weidusLieObj.put("tableName", weiduLieObj.tableName);
            weidusLieObj.put("tableColName", weiduLieObj.tableColName);
            weidusLieObj.put("tableColKey", weiduLieObj.tableColKey);
            weidusLieObj.put("tname", weiduLieObj.tname);
            weidusLieObj.put("id", weiduLieObj.col_id);
            if (vat_lie != null && !vat_lie.equals("")) {
                weidusLieObj.put("vals", vat_lie);
            }
            if (shaixuan_time_lie_end != null
                    && !shaixuan_time_lie_end.equals("")) {
                if (shaixuan_time_lie_type.equals("month")) {
                    weidusLieObj.put("startmt", shaixuan_time_lie_start);
                    weidusLieObj.put("endmt", shaixuan_time_lie_end);
                    weidusLieObj.put("filtertype", 1);
                } else if (shaixuan_time_lie_type.equals("day")) {
                    weidusLieObj.put("startdt", shaixuan_time_lie_start);
                    weidusLieObj.put("enddt", shaixuan_time_lie_end);
                    weidusLieObj.put("filtertype", 1);
                }
            }
            weidusLieObj.put("dimdesc", weiduLieObj.text);
            weidusLieObj.put("colname", weiduLieObj.col_name);
            weidusLieObj.put("type", weiduLieObj.dim_type);

            weidusLieObj.put("dateformat", weiduLieObj.dateformat);
            weidusLieObj.put("alias", weiduLieObj.alias);

            arrayLie.put(weidusLieObj);
        }
        JSONObject weidusLieObjWei = new JSONObject();
        weidusLieObjWei.put("id", "kpi");
        weidusLieObjWei.put("type", "kpiOther");
        arrayLie.put(weidusLieObjWei);
        objTable.put("cols", arrayLie);

        obj.put("tableJson", objTable);

        objALL.put("table", obj);

        JSONArray WeiduArray = new JSONArray();
        for (int i = 0; i < weidusShow.size(); i++) {
            JSONObject weidusObj = new JSONObject();
            WeiduBean weiduLieObj = weidusShow.get(i);
            weidusObj.put("id", weiduLieObj.col_id);
            weidusObj.put("name", weiduLieObj.text);
            weidusObj.put("type", weiduLieObj.dim_type);
            weidusObj.put("colname", weiduLieObj.col_name);
            weidusObj.put("tname", weiduLieObj.tname);
            weidusObj.put("tid", weiduLieObj.tid);
            weidusObj.put("valType", weiduLieObj.valType);
            weidusObj.put("tableName", weiduLieObj.tableName);
            weidusObj.put("tableColKey", weiduLieObj.tableColKey);
            weidusObj.put("tableColName", weiduLieObj.tableColName);
            weidusObj.put("dimord", weiduLieObj.dimord);
            weidusObj.put("grouptype", weiduLieObj.grouptype);

            weidusObj.put("dateformat", weiduLieObj.dateformat);
            weidusObj.put("alias", weiduLieObj.alias);

            WeiduArray.put(weidusObj);
        }
        objALL.put("params", WeiduArray);
        return objALL.toString();
    }

    public String getChatJson() throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray zhibiaoArray = new JSONArray();
        for (int i = 0; i < zhibiaosShow.size(); i++) {
            JSONObject zhibiaoObj = new JSONObject();
            ZhibiaoBean zhibiaoBean = zhibiaosShow.get(i);
            zhibiaoObj.put("tid", zhibiaoBean.tid);
            zhibiaoObj.put("unit", zhibiaoBean.unit);
            zhibiaoObj.put("rate", zhibiaoBean.rate);
            zhibiaoObj.put("alias", zhibiaoBean.alias);
            zhibiaoObj.put("fmt", zhibiaoBean.fmt);
            zhibiaoObj.put("aggre", zhibiaoBean.aggre);
            zhibiaoObj.put("col_name", zhibiaoBean.col_name);
            zhibiaoObj.put("aggre", zhibiaoBean.aggre);
            zhibiaoObj.put("kpi_name", zhibiaoBean.text);
            zhibiaoObj.put("kpi_id", zhibiaoBean.col_id);
            zhibiaoArray.put(zhibiaoObj);
        }
        obj.put("kpiJson", zhibiaoArray);

        JSONObject objTable = new JSONObject();
        JSONObject weidusHangObj = new JSONObject();
        if (weidusHangShow.size() > 0) {
            WeiduBean weiduhangObj = weidusHangShow.get(0);
            weidusHangObj.put("tid", weiduhangObj.tid);
            weidusHangObj.put("valType", weiduhangObj.valType);
            weidusHangObj.put("iscas", weiduhangObj.iscas);
            weidusHangObj.put("grouptype", weiduhangObj.grouptype);
            weidusHangObj.put("dim_name", weiduhangObj.dim_name);
            weidusHangObj.put("dimord", weiduhangObj.dimord);
            weidusHangObj.put("tableName", weiduhangObj.tableName);
            weidusHangObj.put("tableColName", weiduhangObj.tableColName);
            weidusHangObj.put("tableColKey", weiduhangObj.tableColKey);
            weidusHangObj.put("tname", weiduhangObj.tname);
            weidusHangObj.put("id", weiduhangObj.col_id);
            if (vat_hang != null && !vat_hang.equals("")) {
                weidusHangObj.put("vals", vat_hang);
            }
            if (shaixuan_time_hang_end != null
                    && !shaixuan_time_hang_end.equals("")) {
                if (shaixuan_time_hang_type.equals("month")) {
                    weidusHangObj.put("startmt", shaixuan_time_hang_start);
                    weidusHangObj.put("endmt", shaixuan_time_hang_end);
                    weidusHangObj.put("filtertype", 1);
                } else if (shaixuan_time_hang_type.equals("day")) {
                    weidusHangObj.put("startdt", shaixuan_time_hang_start);
                    weidusHangObj.put("enddt", shaixuan_time_hang_end);
                    weidusHangObj.put("filtertype", 1);
                }
            }
            weidusHangObj.put("dimdesc", weiduhangObj.text);
            weidusHangObj.put("colname", weiduhangObj.col_name);
            weidusHangObj.put("type", weiduhangObj.dim_type);

            weidusHangObj.put("dateformat", weiduhangObj.dateformat);
            weidusHangObj.put("alias", weiduhangObj.alias);
        }
        objTable.put("xcol", weidusHangObj);
        objTable.put("type", "line");
        JSONObject weidusLieObj = new JSONObject();
        if (weidusLieShow.size() > 0) {
            WeiduBean weiduLieObj = weidusLieShow.get(0);
            weidusLieObj.put("tid", weiduLieObj.tid);
            weidusLieObj.put("valType", weiduLieObj.valType);
            weidusLieObj.put("iscas", weiduLieObj.iscas);
            weidusLieObj.put("grouptype", weiduLieObj.grouptype);
            weidusLieObj.put("dim_name", weiduLieObj.dim_name);
            weidusLieObj.put("dimord", weiduLieObj.dimord);
            weidusLieObj.put("tableName", weiduLieObj.tableName);
            weidusLieObj.put("tableColName", weiduLieObj.tableColName);
            weidusLieObj.put("tableColKey", weiduLieObj.tableColKey);
            weidusLieObj.put("tname", weiduLieObj.tname);
            weidusLieObj.put("id", weiduLieObj.col_id);
            if (vat_lie != null && !vat_lie.equals("")) {
                weidusLieObj.put("vals", vat_lie);
            }
            if (shaixuan_time_lie_end != null
                    && !shaixuan_time_lie_end.equals("")) {
                if (shaixuan_time_lie_type.equals("month")) {
                    weidusLieObj.put("startmt", shaixuan_time_lie_start);
                    weidusLieObj.put("endmt", shaixuan_time_lie_end);
                    weidusLieObj.put("filtertype", 1);
                } else if (shaixuan_time_lie_type.equals("day")) {
                    weidusLieObj.put("startdt", shaixuan_time_lie_start);
                    weidusLieObj.put("enddt", shaixuan_time_lie_end);
                    weidusLieObj.put("filtertype", 1);
                }
            }
            weidusLieObj.put("dimdesc", weiduLieObj.text);
            weidusLieObj.put("colname", weiduLieObj.col_name);
            weidusLieObj.put("type", weiduLieObj.dim_type);

            weidusLieObj.put("dateformat", weiduLieObj.dateformat);
            weidusLieObj.put("alias", weiduLieObj.alias);
        }
        objTable.put("scol", weidusLieObj);
        JSONArray WeiduArray = new JSONArray();
        for (int i = 0; i < weidusShow.size(); i++) {
            JSONObject weidusObj = new JSONObject();
            WeiduBean weiduLieObj01 = weidusShow.get(i);
            weidusObj.put("id", weiduLieObj01.col_id);
            weidusObj.put("name", weiduLieObj01.text);
            weidusObj.put("type", weiduLieObj01.dim_type);
            weidusObj.put("colname", weiduLieObj01.col_name);
            weidusObj.put("tname", weiduLieObj01.tname);
            weidusObj.put("tid", weiduLieObj01.tid);
            weidusObj.put("valType", weiduLieObj01.valType);
            weidusObj.put("tableName", weiduLieObj01.tableName);
            weidusObj.put("tableColKey", weiduLieObj01.tableColKey);
            weidusObj.put("tableColName", weiduLieObj01.tableColName);
            weidusObj.put("dimord", weiduLieObj01.dimord);
            weidusObj.put("grouptype", weiduLieObj01.grouptype);

            weidusObj.put("dateformat", weiduLieObj01.dateformat);
            weidusObj.put("alias", weiduLieObj01.alias);

            WeiduArray.put(weidusObj);
        }
        objTable.put("params", WeiduArray);
        obj.put("chartJson", objTable);
        return obj.toString();
    }

    public void onItemClick(int type, int position) {
        switch (type) {
        case 1:
            shaixuan_isHand = true;
            WeiduBean wb = weidusHangShow.get(position);
            head_text = wb.text;
            sendShaixuan(wb.col_id, wb.tid);
            break;
        case 2:
            shaixuan_isHand = false;
            WeiduBean wb1 = weidusLieShow.get(position);
            head_text = wb1.text;
            sendShaixuan(wb1.col_id, wb1.tid);
            break;
        }

    }

    private String head_text;
    private boolean shaixuan_isHand;
    private String shaixuan = "";
    private String vat_hang, vat_lie;
    private String shaixuan_time_hang_start;
    private String shaixuan_time_hang_end;
    private String shaixuan_time_hang_type;
    private String shaixuan_time_lie_start;
    private String shaixuan_time_lie_end;
    private String shaixuan_time_lie_type;

    private void sendShaixuan(int dimId, int tid) {
        LoadingDialog.createLoadingDialog(this);
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.shaixuan;
        rv.parser = new ShaixuanParser();
        rv.Type = APIContext.GET;
        shaixuan = UUID.randomUUID().toString();
        rv.uuId = shaixuan;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("dimId", dimId + "");
        map.put("tid", tid + "");
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    public void setShaixuan(String value) {
        if (shaixuan_isHand) {
            vat_hang = value;
        } else {
            vat_lie = value;
        }
    }

    public void setShaixuanTimeHang(String start, String end, String type) {
        shaixuan_time_hang_start = start;
        shaixuan_time_hang_end = end;
        shaixuan_time_hang_type = type;
    }

    public void setShaixuanTimeLie(String start, String end, String type) {
        shaixuan_time_lie_start = start;
        shaixuan_time_lie_end = end;
        shaixuan_time_lie_type = type;
    }

    public void deleteHang() {
        shaixuan_time_hang_start = null;
        shaixuan_time_hang_end = null;
        shaixuan_time_hang_type = null;
        vat_hang = null;
    }

    public void deleteLie() {
        shaixuan_time_lie_start = null;
        shaixuan_time_lie_end = null;
        shaixuan_time_lie_type = null;
        vat_lie = null;
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this, R.style.customProgressDialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_exit_view);
        final EditText et = (EditText) dialog.findViewById(R.id.tv_message);
        dialog.findViewById(R.id.btn_exit_ok).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name=et.getText().toString();
                        try {
                            saveSelectData(name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.btn_exit_no).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onClick(findViewById(R.id.back));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
