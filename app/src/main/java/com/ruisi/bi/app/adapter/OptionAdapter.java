package com.ruisi.bi.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.WeiduOptionBean;

public class OptionAdapter extends BaseAdapter {
	private ArrayList<WeiduOptionBean> options;
	private LayoutInflater inflater;

	public OptionAdapter(Context context, ArrayList<WeiduOptionBean> options) {
		this.options = options;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return options.size();
	}

	@Override
	public Object getItem(int position) {
		return options.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.option_item, null);
			vh=new ViewHolder();
			vh.text = (TextView) convertView
					.findViewById(R.id.OptionAdapter_text);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.text.setText(options.get(position).text);
		return convertView;
	}

	static class ViewHolder {
		TextView text;
	}
}
