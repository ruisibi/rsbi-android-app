package com.ruisi.bi.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.ruisi.bi.app.AnalysisActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.ThemeBean;
import com.ruisi.bi.app.bean.ThemeChildBean;
import com.ruisi.bi.app.common.UserMsg;

public class ThemeAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<ThemeBean> groupsList;
	private LayoutInflater inflater;

	public ThemeAdapter(Context context, List<ThemeBean> groupsList) {
		this.context = context;
		this.groupsList = groupsList;
		this.inflater = LayoutInflater.from(context);
	}

	class ViewHolder {
		CheckBox cb_contact;
		TextView tv_name;
	}

	@Override
	public int getGroupCount() {
		return groupsList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO
		return groupsList.get(groupPosition).childs.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupsList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groupsList.get(groupPosition).childs.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.theme_item_head, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.ThemeAdapter_list_head);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_name.setText(groupsList.get(groupPosition).text);
		viewHolder.tv_name.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return true;
			}
		});
		return convertView;
	}

	public interface OnItemCheckboxChangeListener {
		public void onItemCheckboxChange(boolean flag, int groupPosition,
				int childPosition);
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.theme_item, null);
			viewHolder = new ViewHolder();
			viewHolder.cb_contact = (CheckBox) convertView
					.findViewById(R.id.cb_contacts);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_contacts_item_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final ThemeChildBean mContacts = groupsList.get(groupPosition).childs
				.get(childPosition);
		viewHolder.tv_name.setText(mContacts.text);
		viewHolder.cb_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				updata(groupPosition, childPosition);
			}
		});
		viewHolder.tv_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				updata(groupPosition, childPosition);
			}
		});
		viewHolder.cb_contact.setChecked(mContacts.isChecked);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private void updata(int groupPosition, int childPosition) {
		for (int i = 0; i < groupsList.size(); i++) {
			ArrayList<ThemeChildBean> childs = groupsList.get(i).childs;
			for (int j = 0; j < childs.size(); j++) {
				if (i == groupPosition && j == childPosition) {
					childs.get(j).isChecked = true;
					((AnalysisActivity)context).ThemeId=childs.get(j).tid;
					((AnalysisActivity)context).subjectid=childs.get(j).id;
					((AnalysisActivity)context).subjectname=childs.get(j).text;
				} else {
					childs.get(j).isChecked = false;
				}
			}
		}
		notifyDataSetChanged();
	}

}
