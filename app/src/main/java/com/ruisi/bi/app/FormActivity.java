package com.ruisi.bi.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.adapter.TableAdapter;
import com.ruisi.bi.app.adapter.TableAdapter.TableRow;
import com.ruisi.bi.app.bean.FormBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.WeiduOptionBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.FormParser;
import com.ruisi.bi.app.util.OffLineFenxi;
import com.ruisi.bi.app.util.OffLineFenxi.OffLineCallBack;
import com.ruisi.bi.app.util.TaskManager;
import com.ruisi.bi.app.view.LoadingDialog;
import com.ruisi.bi.app.view.ShaixuanPopwindow;

public class FormActivity extends Activity implements ServerCallbackInterface, 
        OnItemSelectedListener, OffLineCallBack {
    private ListView ListView01;
    private String formUUID;
    private ArrayList<TableRow> TableRows;
    private TableAdapter adapter;
    private static String strJsons;
    private Spinner spinner01, spinner02;
    // private OptionAdapter oAdapter01, oAdapter02;
    private ArrayList<WeiduOptionBean> options01, options02;
    private TextView option_name;
    private String shaixuan01, shaixuan02;
    private int index01, index02;
    private boolean isFirst = true;
    private String DateMin, DateMax;
    private String StartDay, EndDay;
    private static String tags;
    private String[] sile01, sile02;
    private static String title;
    private ArrayAdapter<String> adapterSpinner01, adapterSpinner02;

    public static void startThis(Context context, String strJson, String tag,
            String titles) {
        strJsons = strJson;
        tags = tag;
        title = titles;
        context.startActivity(new Intent(context, FormActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.form_fragment_layout);
        spinner01 = (Spinner) findViewById(R.id.FormActivity_spinner01);
        spinner02 = (Spinner) findViewById(R.id.FormActivity_spinner02);
        spinner02.setOnItemSelectedListener(this);
        spinner01.setOnItemSelectedListener(this);
        options01 = new ArrayList<>();
        options02 = new ArrayList<>();
        // oAdapter01 = new OptionAdapter(this, options01);
        // oAdapter02 = new OptionAdapter(this, options02);
        // spinner01.setAdapter(oAdapter01);
        // spinner02.setAdapter(oAdapter02);
        option_name = (TextView) findViewById(R.id.FormActivity_option_name);
        // ((TextView) findViewById(R.id.title)).setText(title);
        ListView01 = (ListView) findViewById(R.id.ListView01);
        TableRows = new ArrayList<>();
        adapter = new TableAdapter(this, TableRows);
        ListView01.setAdapter(adapter);
        if (AppContext.isNetworkConnected(this)) {
            sendRequest();
        } else {
            String data = UserMsg.getForm(tags);
            if (data != null) {
                TaskManager.getInstance().addTask(
                        new OffLineFenxi(new FormParser(), data, this));
            }
        }
    }

    private void sendRequest() {
        LoadingDialog.createLoadingDialog(this);
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.form;
        rv.parser = new FormParser();
        rv.Type = APIContext.POST;
        formUUID = UUID.randomUUID().toString();
        rv.uuId = formUUID;
        rv.isSaveToLocation = true;
        rv.tag01 = tags;
        Map<String, String> map = new HashMap<String, String>();
        map.put("pageInfo", strJsons);
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (formUUID.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            final FormBean formBean = (FormBean) object;
            TableRows.clear();
            TableRows.addAll(formBean.TableRows);
            adapter.notifyDataSetChanged();
            if (formBean.options.size() == 1) {
                findViewById(R.id.select_layout).setVisibility(View.VISIBLE);
                if (adapterSpinner01!=null) {
                    return;
                }
                sile01 = new String[formBean.options.get(0).options.size()];
                for (int i = 0; i < formBean.options.get(0).options.size(); i++) {
                    sile01[i] = formBean.options.get(0).options.get(i).text;
                }
                adapterSpinner01 = new ArrayAdapter<String>(this,
                        R.layout.myspinner, sile01) {
                    @Override
                    public View getDropDownView(final int position,
                            View convertView, ViewGroup parent) {
                        final View view = FormActivity.this.getLayoutInflater()
                                .inflate(R.layout.spinner_item_layout, null);
                        TextView label = (TextView) view
                                .findViewById(R.id.spinner_item_label);
                        CheckBox check = (CheckBox) view
                                .findViewById(R.id.spinner_item_checked_image);
                        label.setText(sile01[position]);
                        if (shaixuan01.equals(formBean.options.get(0).options.get(position).value)) {
                            check.setChecked(true);
                        }
                        return view;
                    }
                };
                spinner01.setAdapter(adapterSpinner01);
                options01.clear();
                spinner01.setVisibility(View.VISIBLE);
                option_name.setVisibility(View.VISIBLE);
                options01.addAll(formBean.options.get(0).options);
                // oAdapter01.notifyDataSetChanged();
                spinner01.setSelection(index01, true);
                option_name.setText(formBean.options.get(0).name);
            }
            if (formBean.options.size() >= 2) {
                findViewById(R.id.select_layout).setVisibility(View.VISIBLE);
                if (formBean.options.get(0).type.equals("dateSelect")) {
                    findViewById(R.id.date_layout).setVisibility(View.VISIBLE);
                    if (formBean.options.get(0).max == null) {
                        DateMin = formBean.options.get(0).min;
                    } else {
                        DateMin = formBean.options.get(0).min;
                    }
                    if (formBean.options.get(1).max == null) {
                        DateMax = formBean.options.get(1).min;
                    } else {
                        DateMax = formBean.options.get(1).max;
                    }
                    return;
                }
                
                if (adapterSpinner02!=null) {
                    return;
                }
                options01.clear();
                options02.clear();
                spinner01.setVisibility(View.VISIBLE);
                spinner02.setVisibility(View.VISIBLE);
                option_name.setVisibility(View.VISIBLE);
                option_name.setText(formBean.options.get(0).name);
                options01.addAll(formBean.options.get(0).options);
                options02.addAll(formBean.options.get(1).options);
                spinner01.setSelection(index01, true);
                spinner02.setSelection(index02, true);
                sile01 = new String[formBean.options.get(0).options.size()];
                for (int i = 0; i < formBean.options.get(0).options.size(); i++) {
                    sile01[i] = formBean.options.get(0).options.get(i).text;
                }
                sile02 = new String[formBean.options.get(1).options.size()];
                for (int i = 0; i < formBean.options.get(1).options.size(); i++) {
                    sile02[i] = formBean.options.get(1).options.get(i).text;
                }
                adapterSpinner01 = new ArrayAdapter<String>(this,
                        R.layout.myspinner, sile01) {
                    @Override
                    public View getDropDownView(int position, View convertView,
                            ViewGroup parent) {
                        View view = FormActivity.this.getLayoutInflater()
                                .inflate(R.layout.spinner_item_layout, null);
                        TextView label = (TextView) view
                                .findViewById(R.id.spinner_item_label);
                        // ImageView check = (ImageView) view
                        // .findViewById(R.id.spinner_item_checked_image);
                        CheckBox check = (CheckBox) view
                                .findViewById(R.id.spinner_item_checked_image);
                        label.setText(sile01[position]);
                        if (shaixuan01.equals(formBean.options.get(0).options.get(position).value)) {
                            check.setChecked(true);
                        }

                        return view;
                    }
                };
                adapterSpinner02 = new ArrayAdapter<String>(this,
                        R.layout.myspinner, sile02) {
                    @Override
                    public View getDropDownView(int position, View convertView,
                            ViewGroup parent) {
                        View view = FormActivity.this.getLayoutInflater()
                                .inflate(R.layout.spinner_item_layout, null);
                        TextView label = (TextView) view
                                .findViewById(R.id.spinner_item_label);
                        // ImageView check = (ImageView) view
                        // .findViewById(R.id.spinner_item_checked_image);
                        label.setText(sile02[position]);
                        
                        
                        CheckBox check = (CheckBox) view
                                .findViewById(R.id.spinner_item_checked_image);
                        if (shaixuan02.equals(formBean.options.get(1).options.get(position).value)) {
                            check.setChecked(true);
                        }
                        return view;
                    }
                };
                spinner01.setAdapter(adapterSpinner01);
                spinner02.setAdapter(adapterSpinner02);
            }
            isFirst = false;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.back:
            finish();
            break;
        case R.id.FormActivity_check:
            setParams();
            break;
        case R.id.date_start_bt:
        case R.id.date_end_bt:
            if (DateMin == null || DateMax == null)
                return;
            ShaixuanPopwindow.getShaixuanDatPopwindow(this, DateMin, DateMax,
                    StartDay, EndDay, (TextView) findViewById(R.id.title), null);
            break;
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (formUUID.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            Toast.makeText(this, errorMessage.getErrorDes(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        switch (parent.getId()) {
        case R.id.FormActivity_spinner01:
            index01 = position;
            shaixuan01 = options01.get(position).value;
            break;
        case R.id.FormActivity_spinner02:
            index02 = position;
            shaixuan02 = options02.get(position).value;
            break;
        }
    }

    private void setParams() {
        if (StartDay != null && EndDay != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
            try {
                Date d01=sdf.parse(StartDay);
                Date d02=sdf.parse(EndDay);
                if (d02.getTime()<d01.getTime()) {
                    Toast.makeText(this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                JSONObject obj = new JSONObject(strJsons);
                JSONObject objP = obj.getJSONArray("params").getJSONObject(0);
                // objP.put("filtertype", 1);
                objP.put("st", StartDay);
                objP.put("end", EndDay);
                
                Log.e("LLL", "StartDay-->"+StartDay);
                Log.e("LLL", "EndDay-->"+EndDay);
                strJsons = obj.toString();
                sendRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        if (DateMin != null) {
            return;
        }
        if (spinner01.getVisibility() == View.VISIBLE
                && spinner02.getVisibility() == View.VISIBLE) {

            if (shaixuan01 == null) {
                Toast.makeText(this, "第一个未选择", Toast.LENGTH_SHORT).show();
                return;
            }
            if (shaixuan02 == null) {
                Toast.makeText(this, "第二个未选择", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isFirst) {
                return;
            }

            try {
                JSONObject obj = new JSONObject(strJsons);
                JSONObject objP = obj.getJSONArray("params").getJSONObject(0);
                objP.put("filtertype", 1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
                try {
                    Date d01=sdf.parse(shaixuan01);
                    Date d02=sdf.parse(shaixuan02);
                    if (d02.getTime()<d01.getTime()) {
                        Toast.makeText(this, "开始月份不能大于结束月份", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                objP.put("st", shaixuan01);
                objP.put("end", shaixuan02);
                strJsons = obj.toString();
                sendRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        if (spinner01.getVisibility() == View.VISIBLE) {
            try {
                JSONObject obj = new JSONObject(strJsons);
                obj.getJSONArray("params").getJSONObject(0)
                        .put("vals", shaixuan01);
                strJsons = obj.toString();
                sendRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setSelectDay(String t, String e) {
        ((Button) findViewById(R.id.date_start_bt)).setText(t);
        ((Button) findViewById(R.id.date_end_bt)).setText(e);
        StartDay = t;
        EndDay = e;
    }

    @Override
    public void callBack(Object obj) {
        LoadingDialog.dimmissLoading();
        FormBean formBean = (FormBean) obj;
        TableRows.clear();
        TableRows.addAll(formBean.TableRows);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void callBackFail(String obj) {

    }
}
