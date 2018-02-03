package com.ruisi.bi.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.adapter.PushSubjectAdapter;
import com.ruisi.bi.app.bean.PushItem;
import com.ruisi.bi.app.bean.PushListItem;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.fragment.PushCreateDayFragment;
import com.ruisi.bi.app.fragment.PushCreateMonthFragment;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.parser.PushItemParser;
import com.ruisi.bi.app.view.LoadingDialog;

import org.json.JSONException;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import info.hoang8f.android.segmented.SegmentedGroup;


/**
 * Created by berlython on 16/5/14.
 */
public class PushCreateActivity extends Activity implements ServerCallbackInterface, View.OnClickListener {

    public static final String PUSH_ITEM_SER_KEY = "com.ruisi.bi.app.bean.pushItem.ser";

    private Integer pushId;
    private Integer subjectId;
    private PushSubjectAdapter.SubjectType subjectType;
    private String reqUUID;
    private PushItem pushItem;
    private PushCreateDayFragment dayFragment;
    private PushCreateMonthFragment monthFragment;
    private RadioButton rbDay;
    private RadioButton rbMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.activity_push_create);
//        LoadingDialog.createLoadingDialog(this);

        rbDay = (RadioButton)findViewById(R.id.segBtnDay);
        rbMonth = (RadioButton)findViewById(R.id.segBtnMonth);
        rbDay.setOnClickListener(this);
        rbMonth.setOnClickListener(this);

        rbDay.setChecked(true);
        dayFragment = new PushCreateDayFragment();
        monthFragment = new PushCreateMonthFragment();
        setFragment(0);
        this.pushId = getIntent().getIntExtra("pushId", -1);

        if (pushId != -1){
            sendRequest();
        }

    }

    public void setFragment(int type) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (type == 0) {
            transaction.replace(R.id.realtabcontent, dayFragment);
        } else {
            transaction.replace(R.id.realtabcontent, monthFragment);
        }
        transaction.commit();

    }

    private void sendRequest(){

        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.getPush;
        rv.parser = new PushItemParser();
        rv.Type = APIContext.GET;
        reqUUID = UUID.randomUUID().toString();
        rv.uuId = reqUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", pushId.toString());
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        LoadingDialog.dimmissLoading();
        if (uuid.equals(reqUUID)) {
            pushItem = (PushItem)object;
            this.subjectId = pushItem.tid;
            if ("month".equals(pushItem.pushType)) {
                setFragment(1);
                rbMonth.setChecked(true);
                this.subjectType = PushSubjectAdapter.SubjectType.MONTH;
                monthFragment.setSelectId(pushItem.tid);

            } else if ("day".equals(pushItem.pushType)){
                setFragment(0);
                rbDay.setChecked(true);
                this.subjectType = PushSubjectAdapter.SubjectType.DAY;
                dayFragment.setSelectId(pushItem.tid);
            }
            goToNext();
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (uuid.equals(reqUUID)) {
            Toast.makeText(this, "获取推送配置信息失败", Toast.LENGTH_SHORT).show();
//            LoadingDialog.dimmissLoading();
            finish();
        }
    }

    public void setSelectedSubjectId(Integer subjectId, PushSubjectAdapter.SubjectType subjectType) {
        this.subjectId = subjectId;
        this.subjectType = subjectType;

    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_next_step:
                goToNext();
                break;
            case R.id.segBtnDay:
                setFragment(0);
                break;
            case R.id.segBtnMonth:
                setFragment(1);
                break;
        }
    }

    public Integer getSubjectId (){
        return this.subjectId;
    }

    private void goToNext(){
        if (subjectId != null) {
            Intent intent = new Intent(PushCreateActivity.this, PushCreateSettingActivity.class);
            intent.putExtra("subjectId", subjectId);
            intent.putExtra("subjectType", subjectType == PushSubjectAdapter.SubjectType.DAY ? 1 : 0);
            if (pushItem != null && subjectId == pushItem.tid) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PUSH_ITEM_SER_KEY, pushItem);
                intent.putExtras(bundle);
            }
            if (pushId != null && pushId != -1) {
                intent.putExtra("pushId", pushId);
            }
            startActivity(intent);
        } else {
            Toast.makeText(this, "请选择一个推送主题", Toast.LENGTH_SHORT).show();
        }
    }


}
