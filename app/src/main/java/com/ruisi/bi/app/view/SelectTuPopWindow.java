package com.ruisi.bi.app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.TuActivity;

public class SelectTuPopWindow {
	public static PopupWindow getMenuPopwindow(final Activity context, View vs) {
		View mView = LayoutInflater.from(context).inflate(
				R.layout.setting_popwindow, null);
		ListView lv = (ListView) mView.findViewById(R.id.popwindow_lv);
		lv.setAdapter(new MenuAdapter(context));
		
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		final PopupWindow menuWindow = new PopupWindow(mView,
				metric.widthPixels*2/5, LayoutParams.WRAP_CONTENT);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setFocusable(true);
		menuWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		menuWindow.setTouchable(true);
		menuWindow.setContentView(mView);
		int[] location = new int[2];
		vs.getLocationOnScreen(location);
		menuWindow.showAtLocation(vs, Gravity.TOP | Gravity.RIGHT, location[0], location[1]);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((TuActivity)context).onItemSelected(position);
				menuWindow.dismiss();
			}
		});
		return menuWindow;
	}
	@SuppressLint("Recycle")
	private static int[] getImageRes(Context context,int ids) {
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
			inflater=LayoutInflater.from(context);
			names=context.getResources().getStringArray(R.array.select_tu);
			icons=getImageRes(context, R.array.menus_icon);
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
			View v=inflater.inflate(R.layout.menu_item, null);
//			((ImageView)v.findViewById(R.id.menu_item_icon)).setImageResource(icons[position]);		
			((TextView)v.findViewById(R.id.menu_item_name)).setText(names[position]);
			return v;
		}

	}
}
