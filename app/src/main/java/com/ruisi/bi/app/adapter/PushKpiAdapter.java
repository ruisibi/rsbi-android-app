package com.ruisi.bi.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.ZhibiaoBean;
import com.ruisi.bi.app.fragment.PushKpiFragment;

import java.util.List;

/**
 * Created by berlython on 16/7/3.
 */
public class PushKpiAdapter extends BaseAdapter{

    private Context context;

    private List<ZhibiaoBean> data;

    private Integer selectedId;

    private PushKpiFragment fragment;

    public PushKpiAdapter(Context context, List<ZhibiaoBean> data, Integer selectedId, PushKpiFragment fragment){
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
        ZhibiaoBean kpi = data.get(position);
        viewHolder.tv_name.setText(kpi.text);
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
            if (kpi.id == selectedId) {
                viewHolder.cb_selected.setChecked(true);
            } else {
                viewHolder.cb_selected.setChecked(false);
            }
        }

        return convertView;
    }

    private void update(Integer position){
        ZhibiaoBean zhibiaoBean = data.get(position);
        selectedId = zhibiaoBean.id;
        fragment.setSelectedData(position);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox cb_selected;
        TextView tv_name;
    }
}
