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
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.adapter.MenuAdapter;
import com.ruisi.bi.app.adapter.PushSubjectAdapter;
import com.ruisi.bi.app.bean.PushSubject;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.MenuParser;
import com.ruisi.bi.app.parser.PushSubjectParser;
import com.ruisi.bi.app.util.OffLineFenxi;
import com.ruisi.bi.app.util.TaskManager;
import com.ruisi.bi.app.view.LoadingDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by berlython on 16/5/27.
 */
public class PushCreateMonthFragment extends Fragment implements AdapterView.OnItemClickListener, ServerCallbackInterface {

    private ListView listView;
    private List<PushSubject> data;
    private PushSubjectAdapter monthAdapter;
    private Integer selectId;
    private String uuid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_push_create, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = (ListView) this.getView().findViewById(R.id.lv_push_create);
        listView.setOnItemClickListener(this);
        data = new ArrayList<>();
        monthAdapter = new PushSubjectAdapter(getActivity(), data, selectId, PushSubjectAdapter.SubjectType.MONTH);
        listView.setAdapter(monthAdapter);
        LoadingDialog.createLoadingDialog(getActivity());
        sendRequest();

    }

    public void setSelectId(Integer id){
        selectId = id;
        if (monthAdapter != null) {
            monthAdapter.setSelectedId(id);
            monthAdapter.notifyDataSetChanged();
        }

    }

    private void sendRequest(){
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = getActivity();
        rv.functionPath = APIContext.getPushSubjectList;
        rv.parser = new PushSubjectParser();
        rv.Type = APIContext.GET;
        uuid = UUID.randomUUID().toString();
        rv.uuId = uuid;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", UserMsg.getToken());
        map.put("subjectType", "month");
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (uuid.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            if (object instanceof ReLoginBean) {
                Toast.makeText(getActivity(), "用户未登录！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                return;
            }
            data.clear();
            data.addAll((Collection<? extends PushSubject>) object);
            monthAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (uuid.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            Toast.makeText(getActivity(), errorMessage.getErrorDes(), Toast.LENGTH_SHORT).show();
        }
    }
}
