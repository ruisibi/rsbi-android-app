package com.ruisi.bi.app;

import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.UserBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.UserMsgParser;
import com.ruisi.bi.app.view.LoadingDialog;

public class UserMsgActivity extends Activity implements
		ServerCallbackInterface, OnClickListener {
	private String uuids;
	private TextView login_name, user_name, company_name, zhanghao_statu,
			login_count, pre_login_time, zhanghaokaitong_time,
			zhanghaoend_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppContext.setStatuColor(this);
		setContentView(R.layout.user_msg_layout);
		sendData();
		login_name=(TextView) findViewById(R.id.login_name);
		user_name=(TextView) findViewById(R.id.user_name);
		company_name=(TextView) findViewById(R.id.company_name);
		zhanghao_statu=(TextView) findViewById(R.id.zhanghao_statu);
		login_count=(TextView) findViewById(R.id.login_count);
		pre_login_time=(TextView) findViewById(R.id.pre_login_time);
		//zhanghaokaitong_time=(TextView) findViewById(R.id.zhanghaokaitong_time);
		//zhanghaoend_time=(TextView) findViewById(R.id.zhanghaoend_time);
		findViewById(R.id.back).setOnClickListener(this);
	}

	private void sendData() {
		LoadingDialog.createLoadingDialog(this);
		ServerEngine serverEngine = new ServerEngine(this);
		RequestVo rv = new RequestVo();
		rv.context = this;
		rv.functionPath = APIContext.userMessage;
		rv.parser = new UserMsgParser();
		rv.Type = APIContext.GET;
		uuids = UUID.randomUUID().toString();
		rv.uuId = uuids;
		rv.isSaveToLocation = false;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", UserMsg.getToken());
		rv.requestDataMap = map;
		serverEngine.addTaskWithConnection(rv);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	@Override
	public <T> void succeedReceiveData(T object, String uuid) {
		if (uuids.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			if (object instanceof ReLoginBean) {
	    		  Toast.makeText(this, "用户未登录！", 1000).show();
	    		  startActivity(new Intent(this, LoginActivity.class));
	    		 finish();
	    		  return;
			}
			UserBean bean=(UserBean) object;
			login_name.setText(bean.staff_id);
			user_name.setText(bean.login_name);
			company_name.setText(bean.company);
			zhanghao_statu.setText(Integer.parseInt(bean.state)==1?"有效":"无效");
			login_count.setText(bean.log_cnt);
			pre_login_time.setText(bean.log_date);
			//zhanghaokaitong_time.setText(bean.date_start);
			//zhanghaoend_time.setText(bean.date_end);
		}
	}

	@Override
	public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
		if (uuids.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			Toast.makeText(this, errorMessage.getErrorDes(), 1000).show();
		}

	}
}
