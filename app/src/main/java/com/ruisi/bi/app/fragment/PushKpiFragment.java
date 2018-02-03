package com.ruisi.bi.app.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ruisi.bi.app.LoginActivity;
import com.ruisi.bi.app.PushConditionActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.adapter.PushDimAdapter;
import com.ruisi.bi.app.adapter.PushKpiAdapter;
import com.ruisi.bi.app.bean.PushItem;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.ZhibiaoBean;
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
public class PushKpiFragment extends Fragment implements AdapterView.OnItemClickListener, ServerCallbackInterface {

    private ListView lvPushKpi;
    private List<ZhibiaoBean> kpiList;
    private PushKpiAdapter adapter;

    private String uuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_push_kpi, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        kpiList = new ArrayList<>();
        lvPushKpi = (ListView) getView().findViewById(R.id.lv_push_kpi);
        lvPushKpi.setOnItemClickListener(this);
        PushItem pi = ((PushConditionActivity)getActivity()).getPushItem();
        adapter = new PushKpiAdapter(getActivity(), kpiList, (pi.kpiJson != null) && (pi.kpiJson.size() != 0)? pi.kpiJson.get(0).kpi_id: null, this);
        lvPushKpi.setAdapter(adapter);

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
        rv.functionPath = APIContext.Zhibiao;
        rv.parser = new ZhibiaoParser();
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

    public void setSelectedData(Integer position) {
        PushItem pushItem = ((PushConditionActivity)getActivity()).getPushItem();
        if (pushItem.kpiJson == null) {
            pushItem.kpiJson = new ArrayList<>();
        }
        ZhibiaoBean nowSelected = kpiList.get(position);

        PushItem.KpiJsonItem kpiJsonItem = pushItem.new KpiJsonItem();
        kpiJsonItem.kpi_id = nowSelected.id;
        kpiJsonItem.kpi_name = nowSelected.text;
        kpiJsonItem.col_name = nowSelected.col_name;
        kpiJsonItem.aggre = nowSelected.aggre;
        kpiJsonItem.fmt = nowSelected.fmt;
        kpiJsonItem.alias = nowSelected.alias;
        kpiJsonItem.tid = nowSelected.tid;
        kpiJsonItem.unit = nowSelected.unit;
        kpiJsonItem.rate = nowSelected.rate;
        if (pushItem.kpiJson.size() == 0) {
            pushItem.kpiJson.add(kpiJsonItem);
        } else {
            pushItem.kpiJson.set(0, kpiJsonItem);
        }


        ((PushConditionActivity) getActivity()).setPushItem(pushItem, null);
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
            kpiList.clear();
            kpiList.addAll((Collection<? extends ZhibiaoBean>) object);
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
