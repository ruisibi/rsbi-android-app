package com.ruisi.bi.app.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ruisi.bi.app.AnalysisActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.adapter.TableAdapter;
import com.ruisi.bi.app.adapter.TableAdapter.TableRow;
import com.ruisi.bi.app.adapter.ThemeAdapter;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.FormParser;

public class FormFragment extends Fragment implements ServerCallbackInterface {
	private ListView ListView01;
	private ThemeAdapter themeAdapter;
	private String formUUID;
	private AnalysisActivity analysisActivity;
	private ArrayList<TableRow> TableRows;
	private TableAdapter adapter;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		analysisActivity = (AnalysisActivity) activity;
		((ProgressBar)activity.findViewById(R.id.progress_bar)).setProgress(100);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.form_fragment_layout, null);
		ListView01 = (ListView) v
				.findViewById(R.id.ListView01);
		TableRows=new ArrayList<>();
		adapter=new TableAdapter(getActivity(), TableRows);
		ListView01.setAdapter(adapter);
		// themes=new ArrayList<ThemeBean>();
		// themeAdapter=new ThemeAdapter(getActivity(), themes);
		// ThemeFragment_listView.setAdapter(themeAdapter);
		sendRequest();
		return v;
	}

	private void sendRequest() {
		ServerEngine serverEngine = new ServerEngine(this);
		RequestVo rv = new RequestVo();
		rv.context = this.getActivity();
		rv.functionPath = APIContext.form;
		// rv.modulePath = APIContext.Login;
		rv.parser = new FormParser();
		rv.Type = APIContext.POST;
		formUUID = UUID.randomUUID().toString();
		rv.uuId = formUUID;
		rv.isSaveToLocation = false;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("pageInfo", analysisActivity.formStr);
		 map.put("token", UserMsg.getToken());
		rv.requestDataMap = map;
		serverEngine.addTaskWithConnection(rv);
	}

	@Override
	public <T> void succeedReceiveData(T object, String uuid) {
		if (formUUID.equals(uuid)) {
			TableRows.clear();
			TableRows.addAll((Collection<? extends TableRow>) object);
			adapter.notifyDataSetChanged();
			Toast.makeText(getActivity(), "�ɹ�", 2000).show();
			// themes.clear();
			// themes.addAll((Collection<? extends ThemeBean>) object);
			// themeAdapter.notifyDataSetChanged();
			// for(int i = 0; i < themes.size(); i++){
			// ThemeFragment_listView.expandGroup(i);
			// }
		}
	}

	@Override
	public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
		if (formUUID.equals(uuid)) {
			Toast.makeText(this.getActivity(), errorMessage.getErrorDes(), 1000).show();
		}
	}

}
