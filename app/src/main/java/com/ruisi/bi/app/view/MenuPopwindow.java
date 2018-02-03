package com.ruisi.bi.app.view;

import java.util.HashMap;
import java.util.UUID;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.LoginActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.UserMsgActivity;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.UserBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.MyCookieJar;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.parser.MenuParser;

public class MenuPopwindow implements ServerCallbackInterface {
	private String uuidssss = "";
	private Activity mcontext;

	public PopupWindow getMenuPopwindow(final Activity context, View vs) {
		mcontext = context;
		View mView = LayoutInflater.from(context).inflate(
				R.layout.setting_popwindow, null);
		ListView lv = (ListView) mView.findViewById(R.id.popwindow_lv);
		lv.setAdapter(new MenuAdapter(context));
		
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		final PopupWindow menuWindow = new PopupWindow(mView,
				metric.widthPixels * 2 / 5, LayoutParams.WRAP_CONTENT);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setFocusable(true);
		menuWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		menuWindow.setTouchable(true);
		menuWindow.setContentView(mView);
		int[] location = new int[2];
		vs.getLocationOnScreen(location);
		menuWindow.showAtLocation(vs, Gravity.TOP | Gravity.RIGHT, location[0],
				location[1]);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				menuWindow.dismiss();
				if (position == 0) {
					context.startActivity(new Intent(context,
							UserMsgActivity.class));
				} else if (position == 1) {
					sendRequest(context);
				}
			}
		});
		return menuWindow;
	}

	@SuppressLint("Recycle")
	private static int[] getImageRes(Context context, int ids) {
		TypedArray arrIcons = context.getResources().obtainTypedArray(ids);
		int[] icons = new int[arrIcons.length()];
		for (int i = 0; i < arrIcons.length(); i++) {
			icons[i] = arrIcons.getResourceId(i, 0);
		}
		return icons;
	}

	static class MenuAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private String[] names;
		private int[] icons;

		public MenuAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			names = context.getResources().getStringArray(R.array.menus_name);
			icons = getImageRes(context, R.array.menus_icon);
		}

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			return names[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = inflater.inflate(R.layout.menu_item, null);
//			((ImageView) v.findViewById(R.id.menu_item_icon))
//					.setImageResource(icons[position]);
			((TextView) v.findViewById(R.id.menu_item_name))
					.setText(names[position]);
			return v;
		}

	}

	private void sendRequest(Context context) {
		LoadingDialog.createLoadingDialog(context);
		ServerEngine serverEngine = new ServerEngine(this);
		RequestVo rv = new RequestVo();
		rv.context = context;
		rv.functionPath = APIContext.logout;
		rv.parser = new BaseParser() {

			@Override
			public <T> T parse(String jsonStr) throws JSONException {
				return (T) "ok";
			}

		};
		rv.Type = APIContext.GET;
		uuidssss = UUID.randomUUID().toString();
		rv.uuId = uuidssss;
		rv.isSaveToLocation = false;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", UserMsg.getToken());
		rv.requestDataMap = map;
		serverEngine.addTaskWithConnection(rv);
	}

	@Override
	public <T> void succeedReceiveData(T object, String uuid) {
		if (uuidssss.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			//清除cookie
			//从数据库中清除
			UserMsg.removeCookie();
			//从内存中清除
			MyCookieJar.clearCookie();
			mcontext.startActivity(new Intent(mcontext, LoginActivity.class));
			mcontext.finish();
			Toast.makeText(mcontext, "注销成功！", 1000).show();
		}

	}

	@Override
	public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
		if (uuidssss.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			Toast.makeText(mcontext, "注销失败！", 1000).show();
		}
	}
}
