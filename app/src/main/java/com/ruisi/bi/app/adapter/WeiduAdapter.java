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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.WeiduBean;

public class WeiduAdapter extends BaseAdapter {
    private Context context;
    private List<WeiduBean> weidus;
    private LayoutInflater inflater;
    private int type;

    public WeiduAdapter(Context context, List<WeiduBean> weidus) {
        this.inflater = LayoutInflater.from(context);
        this.weidus = weidus;
    }
public void setType(int type){
    this.type=type;
}
    @Override
    public int getCount() {
        return weidus.size();
    }

    @Override
    public Object getItem(int position) {
        return weidus.get(position);
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
            holder.noClick = (LinearLayout) convertView.findViewById(R.id.noClick);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final WeiduBean weiduBean=weidus.get(position);
        holder.tv_name.setText(weiduBean.text);
        holder.noClick.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
            }
        });
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
            }
        });
        holder.cb.setChecked(weiduBean.isChecked);
        if (weiduBean.canClicked && type != 0)
            holder.noClick.setVisibility(View.VISIBLE);
        return convertView;
    }
    private void setChecked(int position){
        weidus.get(position).isChecked=!weidus.get(position).isChecked;
        for (int i = 0; i < weidus.size(); i++) {
            if(i!=position){
                weidus.get(i).isChecked=false;
            }
        }
        notifyDataSetChanged();
    }

    private final class ViewHolder {
        TextView tv_name;
        CheckBox cb;
        LinearLayout noClick;
    }

}
