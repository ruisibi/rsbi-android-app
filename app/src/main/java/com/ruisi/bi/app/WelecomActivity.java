package com.ruisi.bi.app;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.MyCookieJar;

import okhttp3.Cookie;

public class WelecomActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppContext.setStatuColor(this);
		setContentView(R.layout.welecome_layout);
		new Thread(new Runnable() {
			@Override
			public void run() {
				UserMsg.savaAppStatus(true);
			}
		}).start();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				List<Cookie> cookies = UserMsg.getCookies();
				String token = UserMsg.getToken();
				//为了兼容以前版本，同时需要token和 cookie验证
				if ((cookies == null || cookies.size() == 0) && (token == null || token.length() == 0) ) {
					startActivity(new Intent(WelecomActivity.this, LoginActivity.class));
				} else {
					MyCookieJar.setCookies(cookies);
					startActivity(new Intent(WelecomActivity.this, MenuActivity.class));
				}
				//更新host
				if(UserMsg.getHost() != null){
					APIContext.HOST = UserMsg.getHost();
				}
				WelecomActivity.this.finish();
			}
		}, 3000);
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "UjbESafGszIyuF8ZAcozPkdN");
	}
}
