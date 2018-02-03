package com.ruisi.bi.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ruisi.bi.app.adapter.PushSubjectAdapter;
import com.ruisi.bi.app.bean.PushItem;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.fragment.PushConditionFragment;
import com.ruisi.bi.app.fragment.PushDimFragment;
import com.ruisi.bi.app.fragment.PushKpiFragment;
import com.ruisi.bi.app.fragment.PushTimeFragment;

import javax.security.auth.Subject;

/**
 * Created by berlython on 16/6/26.
 */
public class PushConditionActivity extends Activity {

    private FragmentType fragmentType;

    private TextView tvTitle;
    private Button btnSure;

    private PushItem pushItem;
    private PushSubjectAdapter.SubjectType subjectType;
    private String text;
    private Fragment fragment;


    private enum FragmentType{
        PUSH_DIM, PUSH_KPI, PUSH_CONDITION, PUSH_TIME;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.activity_push_condition);

        tvTitle = (TextView) findViewById(R.id.title);
        btnSure = (Button) findViewById(R.id.btn_sure);

        Intent intent = getIntent();
        pushItem = (PushItem)intent.getSerializableExtra(PushCreateActivity.PUSH_ITEM_SER_KEY);
        Integer type = intent.getIntExtra("type", 0);
        Integer subject = intent.getIntExtra("subjectType", 0);

        if (subject == 0) {
            subjectType = PushSubjectAdapter.SubjectType.DAY;
        } else {
            subjectType = PushSubjectAdapter.SubjectType.MONTH;
        }

        switch (type) {
            case 0:
                fragmentType = FragmentType.PUSH_DIM;
                break;
            case 1:
                fragmentType = FragmentType.PUSH_KPI;
                break;
            case 2:
                fragmentType = FragmentType.PUSH_CONDITION;
                break;
            case 3:
                fragmentType = FragmentType.PUSH_TIME;
                break;
        }

        setFragment();
    }

    private void setFragment(){
        switch (fragmentType) {
            case PUSH_DIM:
                fragment = new PushDimFragment();
                break;
            case PUSH_KPI:
                fragment = new PushKpiFragment();
                break;
            case PUSH_CONDITION:
                fragment = new PushConditionFragment();
                break;
            case PUSH_TIME:
                fragment = new PushTimeFragment();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.commit();
    }

    public void setPushItem(PushItem pushItem, String text) {
        this.pushItem = pushItem;
        this.text = text;
    }

    public PushItem getPushItem() {
        return pushItem;
    }

    public PushSubjectAdapter.SubjectType getSubjectType() {
        return this.subjectType;
    }

    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back:
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.btn_sure:
                //TODO

                if (fragmentType == FragmentType.PUSH_DIM || fragmentType == FragmentType.PUSH_KPI) {
                    if (fragmentType == FragmentType.PUSH_DIM) {
                        intent.putExtra("text", this.text);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PushCreateActivity.PUSH_ITEM_SER_KEY, pushItem);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                if (fragmentType == FragmentType.PUSH_CONDITION) {
                    if (((PushConditionFragment)fragment).check()){
                        ((PushConditionFragment)fragment).setData(pushItem);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(PushCreateActivity.PUSH_ITEM_SER_KEY, pushItem);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                if (fragmentType == FragmentType.PUSH_TIME) {
                    if (((PushTimeFragment)fragment).check()){
                        ((PushTimeFragment) fragment).setTime(pushItem);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(PushCreateActivity.PUSH_ITEM_SER_KEY, pushItem);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }


                break;
        }
    }




}
