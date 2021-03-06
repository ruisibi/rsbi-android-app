package com.ruisi.bi.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.bean.BaobiaoItemBean;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaoBiaoItemParser;
import com.ruisi.bi.app.view.LoadingDialog;

public class BaoBiaoListActivity extends Activity implements
        OnItemClickListener, ServerCallbackInterface {
    private ListView baobiao_mulu_lv;
    private ArrayList<BaobiaoItemBean> data;
    private BaobiaoItemAdapter adapter;
    private String baobiaolistUUID;
    private int id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.baobiao_mulu_layout);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        ((TextView) findViewById(R.id.title)).setText(name);
        id = intent.getIntExtra("id", 0);
        baobiao_mulu_lv = (ListView) findViewById(R.id.baobiao_mulu_lv);
        baobiao_mulu_lv.setOnItemClickListener(this);
        data = new ArrayList<>();
        adapter = new BaobiaoItemAdapter(data);
        baobiao_mulu_lv.setAdapter(adapter);
//        sendRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendRequest();
    }

    private void sendRequest() {
        LoadingDialog.createLoadingDialog(this);
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.baobiaoItem;
        rv.parser = new BaoBiaoItemParser();
        rv.Type = APIContext.GET;
        baobiaolistUUID = UUID.randomUUID().toString();
        rv.uuId = baobiaolistUUID;
        rv.isSaveToLocation = true;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", UserMsg.getToken());
        map.put("cataId", id + "");
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (baobiaolistUUID.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            if (object instanceof ReLoginBean) {
                  Toast.makeText(this, "用户未登录！", Toast.LENGTH_SHORT).show();
                  startActivity(new Intent(this, LoginActivity.class));
                 finish();
                  return;
            }
            ArrayList<BaobiaoItemBean> d=(ArrayList<BaobiaoItemBean>) object;
            if (d.size()>0) {
                data.clear();
                data.addAll((Collection<? extends BaobiaoItemBean>) object);
                adapter.notifyDataSetChanged();
            }else {
                //Toast.makeText(this, "本目录下无报表!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (baobiaolistUUID.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            Toast.makeText(this, errorMessage.getErrorDes(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Intent intent = new Intent(this, Webviewactivity.class);
        intent.putExtra("rid", data.get(position).rid);
        intent.putExtra("url", data.get(position).url);
        intent.putExtra("title", data.get(position).title);
        intent.putExtra("iscollect", data.get(position).iscollect);
        startActivity(intent);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.back:
            finish();
            break;
        }
    }

    private class BaobiaoItemAdapter extends BaseAdapter {
        private ArrayList<BaobiaoItemBean> beans;

        public BaobiaoItemAdapter(ArrayList<BaobiaoItemBean> beans) {
            this.beans = beans;
        }

        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(BaoBiaoListActivity.this)
                    .inflate(R.layout.baobiao_mulu_layout_item, null);
            TextView name = (TextView) convertView
                    .findViewById(R.id.MainActivity_name);
            name.setText(beans.get(position).title);
            return convertView;
        }

    }
}
