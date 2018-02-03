package com.ruisi.bi.app;

import java.util.ArrayList;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.fragment.ConditionFragment;
import com.ruisi.bi.app.fragment.FormFragment;
import com.ruisi.bi.app.fragment.ThemeFragment;

@SuppressLint("CommitTransaction")
public class 	AnalysisActivity extends FragmentActivity implements
		OnClickListener {
	private FragmentManager fm;
	private FragmentTransaction ft;
	
	private FrameLayout container;
	private ImageView back, next, pre;
	private ArrayList<Fragment> fragments;
	private int indexFragment;
	public int ThemeId,subjectid;
	public String subjectname;
	private ConditionFragment conditionFragment;
	public String formStr;
	private TextView next_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppContext.setStatuColor(this);
		setContentView(R.layout.act_analysis_layout);
		conditionFragment = new ConditionFragment();
		fragments = new ArrayList<>();
		fragments.add(new ThemeFragment());
		fragments.add(conditionFragment);
		fragments.add(new FormFragment());
		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		ft.replace(R.id.AnalysisActivity_container, fragments.get(0)).commit();

		next_tv = (TextView) findViewById(R.id.next);
		next = (ImageView) findViewById(R.id.AnalysisActivity_next);
		pre = (ImageView) findViewById(R.id.AnalysisActivity_pre);
		back = (ImageView) findViewById(R.id.back);
		container = (FrameLayout) findViewById(R.id.AnalysisActivity_container);
		next.setOnClickListener(this);
		pre.setOnClickListener(this);
		back.setOnClickListener(this);
		next_tv.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.next:
			if (APIContext.id!=0) {
				Intent intent = new Intent(this, ConditionAcitivity.class);
				intent.putExtra("ThemeId", -1);
				intent.putExtra("id", APIContext.id);
				startActivity(intent);
			}else {
				if (ThemeId == 0){
					Toast.makeText(this, "请选择！", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(this, ConditionAcitivity.class);
				intent.putExtra("ThemeId", ThemeId);
				intent.putExtra("subjectid", subjectid);
				intent.putExtra("subjectname", subjectname);
				startActivity(intent);
			}
			
			break;
		case R.id.AnalysisActivity_next:
			if (indexFragment < 2) {

				indexFragment++;
				if (indexFragment == 2) {
					try {
						formStr = conditionFragment.getConditionJson();
						System.out.println(formStr);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				ft = fm.beginTransaction();
				ft.replace(R.id.AnalysisActivity_container,
						fragments.get(indexFragment)).commit();
			}
			break;
		case R.id.AnalysisActivity_pre:
			if (indexFragment > 0) {
				indexFragment--;
				ft = fm.beginTransaction();
				ft.replace(R.id.AnalysisActivity_container,
						fragments.get(indexFragment)).commit();
			}
			break;
		case R.id.back:
			APIContext.id = 0;
			APIContext.ThemeId = 0;
			this.finish();
			break;

		default:
			break;
		}
	}
}
