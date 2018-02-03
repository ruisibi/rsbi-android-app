package com.ruisi.bi.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.SaveDataListctivity;
import com.ruisi.bi.app.bean.SaveBean;

public class GetSaveDataAdapter extends BaseAdapter{
	private ArrayList<SaveBean> beans;
	private Context context;
	private LayoutInflater inflater;
	public GetSaveDataAdapter(Context context,ArrayList<SaveBean> beans) {
		this.beans=beans;
		this.context=context;
		inflater=LayoutInflater.from(context);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.savedata_list_activty_item, null);
			vh=new ViewHolder();
			vh.text = (TextView) convertView
					.findViewById(R.id.OptionAdapter_text);
//			vh.bt=(ImageView) convertView
//					.findViewById(R.id.OptionAdapter_delete);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.text.setText(beans.get(position).name);
//		vh.bt.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				((SaveDataListctivity)context).showDialogs(beans.get(position).id);
//			}
//		});
		return convertView;
	}

	static class ViewHolder {
		TextView text;
		ImageView bt;
	}
}
