package com.ruisi.bi.app.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.ruisi.bi.app.AnalysisActivity;
import com.ruisi.bi.app.LoginActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.adapter.ThemeAdapter;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.ThemeBean;
import com.ruisi.bi.app.bean.ThemeChildBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.ThemeParser;
import com.ruisi.bi.app.util.OffLineFenxi;
import com.ruisi.bi.app.util.OffLineFenxi.OffLineCallBack;
import com.ruisi.bi.app.util.TaskManager;
import com.ruisi.bi.app.view.LoadingDialog;

public class ThemeFragment extends Fragment implements ServerCallbackInterface,
		OffLineCallBack {
	private ExpandableListView ThemeFragment_listView;
	private ArrayList<ThemeBean> themes;
	private ThemeAdapter themeAdapter;
	private String themeUUID;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// ((ProgressBar)
		// activity.findViewById(R.id.progress_bar)).setProgress(0);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.thiem_fragment_layout, null);
		ThemeFragment_listView = (ExpandableListView) v
				.findViewById(R.id.ThemeFragment_listView);
		themes = new ArrayList<ThemeBean>();
		themeAdapter = new ThemeAdapter(getActivity(), themes);
		ThemeFragment_listView.setAdapter(themeAdapter);
		LoadingDialog.createLoadingDialog(this.getActivity());
		sendRequest();
		return v;
	}

	private void sendRequest() {
		ServerEngine serverEngine = new ServerEngine(this);
		RequestVo rv = new RequestVo();
		rv.context = this.getActivity();
		rv.functionPath = APIContext.Theme;
		rv.parser = new ThemeParser();
		rv.Type = APIContext.GET;
		themeUUID = UUID.randomUUID().toString();
		rv.uuId = themeUUID;
		rv.isSaveToLocation = false;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", UserMsg.getToken());
		rv.requestDataMap = map;
		serverEngine.addTaskWithConnection(rv);
	}

	@Override
	public <T> void succeedReceiveData(T object, String uuid) {
		if (themeUUID.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			if (object instanceof ReLoginBean) {
	    		  Toast.makeText(this.getActivity(), "用户未登录！", 1000).show();
	    		  startActivity(new Intent(this.getActivity(), LoginActivity.class));
	    		  this.getActivity().finish();
	    		  return;
			}
			themes.clear();
			themes.addAll((Collection<? extends ThemeBean>) object);
			themeAdapter.notifyDataSetChanged();
			for (int i = 0; i < themes.size(); i++) {
				ThemeFragment_listView.expandGroup(i);
			}
			UserMsg.saveFenxi(((ArrayList<ThemeBean>) object).toString());
			if (APIContext.ThemeId!=0) {
					for (int i = 0; i < themes.size(); i++) {
						ArrayList<ThemeChildBean> childs = themes.get(i).childs;
						for (int j = 0; j < childs.size(); j++) {
							if (APIContext.ThemeId==childs.get(j).tid) {
								childs.get(j).isChecked=true;
							} 
						}
					}
					themeAdapter.notifyDataSetChanged();
			}
		}
	}
	public void resatData(){
		for (int i = 0; i < themes.size(); i++) {
			ArrayList<ThemeChildBean> childs = themes.get(i).childs;
			for (int j = 0; j < childs.size(); j++) {
				if (APIContext.ThemeId==childs.get(j).tid) {
					childs.get(j).isChecked=true;
				} 
			}
		}
		themeAdapter.notifyDataSetChanged();
	}

	@Override
	public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
		if (themeUUID.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			if (this.getActivity()==null|| errorMessage==null||errorMessage.getErrorDes()==null) {
				return;
			}
			Toast.makeText(this.getActivity(), errorMessage.getErrorDes(), 1000)
					.show();
			String data = UserMsg.getFenxi();
			if (data != null) {
				TaskManager.getInstance().addTask(
						new OffLineFenxi(new ThemeParser(), data, this));
			}
		}
	}

	@Override
	public void callBack(Object obj) {
		LoadingDialog.dimmissLoading();
		themes.clear();
		themes.addAll((Collection<? extends ThemeBean>) obj);
		themeAdapter.notifyDataSetChanged();
		for (int i = 0; i < themes.size(); i++) {
			ThemeFragment_listView.expandGroup(i);
		}
	}

	@Override
	public void callBackFail(String obj) {

	}
}
