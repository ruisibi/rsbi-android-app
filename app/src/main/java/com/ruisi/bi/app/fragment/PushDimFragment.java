package com.ruisi.bi.app.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ruisi.bi.app.LoginActivity;
import com.ruisi.bi.app.PushConditionActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.adapter.PushDimAdapter;
import com.ruisi.bi.app.bean.MenuBean;
import com.ruisi.bi.app.bean.PushItem;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.WeiduParser;
import com.ruisi.bi.app.parser.ZhibiaoParser;
import com.ruisi.bi.app.view.LoadingDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by berlython on 16/6/26.
 */
public class PushDimFragment extends Fragment implements AdapterView.OnItemClickListener, ServerCallbackInterface {

    private ListView lvPushDim;
    private List<WeiduBean> dimList;
    private PushDimAdapter adapter;

    private String uuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_push_dim, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dimList = new ArrayList<>();
        lvPushDim = (ListView) this.getView().findViewById(R.id.lv_push_dim);
        lvPushDim.setOnItemClickListener(this);
        PushItem pi = ((PushConditionActivity)getActivity()).getPushItem();
        adapter = new PushDimAdapter(getActivity(), dimList, pi.dim != null? pi.dim.id: null, this);
        lvPushDim.setAdapter(adapter);

        sendRequest();
    }

    public void sendRequest() {
        Integer tid = ((PushConditionActivity)getActivity()).getPushItem().tid;
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = getActivity();
        this.uuid = UUID.randomUUID().toString();
        rv.uuId = this.uuid;
        HashMap<String, String> map = new HashMap<String, String>();
        rv.functionPath = APIContext.Weidu;
        rv.parser = new WeiduParser();
        rv.Type = APIContext.GET;
        rv.isSaveToLocation = false;
        map.put("tableid", "" + tid);
        map.put("filterDateDim", "y");
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setSelectedData(position);
    }

    public void setSelectedData(int position) {
        WeiduBean selectedDim = dimList.get(position);

        PushItem pushItem = ((PushConditionActivity)getActivity()).getPushItem();
        PushItem.Dim dim = pushItem.new Dim();
        dim.type = selectedDim.type;
        dim.id = selectedDim.id;
        dim.colname = selectedDim.col_name;
        dim.alias = selectedDim.alias;
        dim.tname = selectedDim.tname;
        dim.tableColKey = selectedDim.tableColKey;
        dim.tableColName = selectedDim.tableColName;
        dim.tableName = selectedDim.tableName;
        dim.dimdesc = selectedDim.text;
        dim.dimord = selectedDim.dimord;
        dim.grouptype = selectedDim.grouptype;
        dim.iscas = selectedDim.iscas;
        dim.valType = selectedDim.valType;
        dim.tid = selectedDim.tid;
        dim.dateformat = selectedDim.dateformat;

        pushItem.dim = dim;
        ((PushConditionActivity) getActivity()).setPushItem(pushItem, selectedDim.text);
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (this.uuid.equals(uuid)) {
            if (object instanceof ReLoginBean) {
                Toast.makeText(getActivity(), "用户未登录！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                return;
            }
            dimList.clear();
            dimList.addAll((Collection<? extends WeiduBean>) object);
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (this.uuid.equals(uuid)) {
            // Toast.makeText(getActivity(), "获取维度数据失败", Toast.LENGTH_SHORT).show();
        }
    }
}
