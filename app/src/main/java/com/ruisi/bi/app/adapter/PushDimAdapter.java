package com.ruisi.bi.app.adapter;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ruisi.bi.app.PushCreateActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.PushSubject;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.fragment.PushDimFragment;

import java.util.Calendar;
import java.util.List;

/**
 * Created by berlython on 16/7/3.
 */
public class PushDimAdapter extends BaseAdapter{

    private Context context;

    private List<WeiduBean> data;

    private Integer selectedId;

    private PushDimFragment fragment;

    public PushDimAdapter(Context context, List<WeiduBean> data, Integer selectedId, PushDimFragment fragment){
        this.context = context;
        this.data = data;
        this.selectedId = selectedId;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).id;
    }

    public void setSelectedId(Integer selectedId){
        this.selectedId = selectedId;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.theme_item, null);
            viewHolder = new ViewHolder();
            viewHolder.cb_selected = (CheckBox) convertView
                    .findViewById(R.id.cb_contacts);
            viewHolder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_contacts_item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WeiduBean dim = data.get(position);
        viewHolder.tv_name.setText(dim.text);
        final int p = position;
        viewHolder.cb_selected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                update(p);
            }
        });
        viewHolder.tv_name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                update(p);
            }
        });
        if (selectedId != null) {
            if (dim.id == selectedId) {
                viewHolder.cb_selected.setChecked(true);
            } else {
                viewHolder.cb_selected.setChecked(false);
            }
        }

        return convertView;
    }

    private void update(Integer position){
        WeiduBean weiduBean = data.get(position);
        selectedId = weiduBean.id;
        fragment.setSelectedData(position);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox cb_selected;
        TextView tv_name;
    }
}
