package com.ruisi.bi.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.ZhibiaoBean;

public class ZhibiaoAdapter extends BaseAdapter {
    private Context context;
    private List<ZhibiaoBean> zhibiaos;
    private LayoutInflater inflater;

    public ZhibiaoAdapter(Context context, List<ZhibiaoBean> weidus) {
        this.inflater = LayoutInflater.from(context);
        this.zhibiaos = weidus;
    }

    @Override
    public int getCount() {
        return zhibiaos.size();
    }

    @Override
    public Object getItem(int position) {
        return zhibiaos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.theme_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.cb = (CheckBox) convertView
                    .findViewById(R.id.cb_contacts);
            holder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_contacts_item_name);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ZhibiaoBean weiduBean=zhibiaos.get(position);
        holder.tv_name.setText(weiduBean.text);
        holder.cb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setChecked(position);
//                weiduBean.isChecked = !weiduBean.isChecked;
//                notifyDataSetChanged();
            }
        });
        holder.tv_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setChecked(position);
//                weiduBean.isChecked = !weiduBean.isChecked;
//                notifyDataSetChanged();
            }
        });
        holder.cb.setChecked(weiduBean.isChecked);
        return convertView;
    }
    private void setChecked(int position){
        zhibiaos.get(position).isChecked=!zhibiaos.get(position).isChecked;
        for (int i = 0; i < zhibiaos.size(); i++) {
            if(i!=position){
                zhibiaos.get(i).isChecked=false;
            }
        }
        notifyDataSetChanged();
    }

    private final class ViewHolder {
        TextView tv_name;
        CheckBox cb;
    }

}
