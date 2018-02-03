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

import com.ruisi.bi.app.bean.BaobiaoMuluBean;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaobiaoMuluParser;
import com.ruisi.bi.app.view.LoadingDialog;

public class BaobiaoMuluAcitvity extends Activity implements
		OnItemClickListener, ServerCallbackInterface {
	private ListView baobiao_mulu_lv;
	private ArrayList<BaobiaoMuluBean> data;
	private BaobiaoMuluAdapter adapter;
	private String baobiaomuluUUID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppContext.setStatuColor(this);
		setContentView(R.layout.baobiao_mulu_layout);
		baobiao_mulu_lv = (ListView) findViewById(R.id.baobiao_mulu_lv);
		baobiao_mulu_lv.setOnItemClickListener(this);
		data = new ArrayList<>();
		adapter = new BaobiaoMuluAdapter(data);
		baobiao_mulu_lv.setAdapter(adapter);
//		sendRequest();
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
		rv.functionPath = APIContext.baobiaomulu;
		rv.parser = new BaobiaoMuluParser();
		rv.Type = APIContext.GET;
		baobiaomuluUUID = UUID.randomUUID().toString();
		rv.uuId = baobiaomuluUUID;
		rv.isSaveToLocation = true;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", UserMsg.getToken());
		rv.requestDataMap = map;
		serverEngine.addTaskWithConnection(rv);
	}

	@Override
	public <T> void succeedReceiveData(T object, String uuid) {
		if (baobiaomuluUUID.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			if (object instanceof ReLoginBean) {
	    		  Toast.makeText(this, "用户未登录！", 1000).show();
	    		  startActivity(new Intent(this, LoginActivity.class));
	    		 finish();
	    		  return;
			}
			data.clear();
			data.addAll((Collection<? extends BaobiaoMuluBean>) object);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
		if (baobiaomuluUUID.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			Toast.makeText(this, errorMessage.getErrorDes(), 1000).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, BaoBiaoListActivity.class);
		intent.putExtra("id", data.get(position).id);
		intent.putExtra("name", data.get(position).name);
		startActivity(intent);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	private class BaobiaoMuluAdapter extends BaseAdapter {
		private ArrayList<BaobiaoMuluBean> beans;

		public BaobiaoMuluAdapter(ArrayList<BaobiaoMuluBean> beans) {
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
			convertView = LayoutInflater.from(BaobiaoMuluAcitvity.this)
					.inflate(R.layout.baobiao_mulu_layout_item, null);
			TextView name = (TextView) convertView
					.findViewById(R.id.MainActivity_name);
			name.setText(beans.get(position).name);
			return convertView;
		}

	}

}
