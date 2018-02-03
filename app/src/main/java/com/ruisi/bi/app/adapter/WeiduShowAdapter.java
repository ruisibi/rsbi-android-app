package com.ruisi.bi.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruisi.bi.app.ConditionAcitivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.WeiduBean;

public class WeiduShowAdapter extends BaseAdapter {
    private Context context;
    private List<WeiduBean> weidus;
    private LayoutInflater inflater;
    private int type;//0,为查询条件；     1,行；    2,列；

    public WeiduShowAdapter(Context context, List<WeiduBean> weidus,int type) {
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.weidus = weidus;
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
            convertView = inflater.inflate(R.layout.delete_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.deltet = (ImageView) convertView
                    .findViewById(R.id.delete_item_iv);
            holder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_contacts_item_name);
            holder.shaixuan=(ImageView) convertView.findViewById(R.id.delete_item_shaixuan_iv);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final WeiduBean weiduBean=weidus.get(position);
        holder.tv_name.setText(weiduBean.text);
        holder.shaixuan.setVisibility(type!=0?View.VISIBLE:View.INVISIBLE);
        holder.deltet.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if (type==1) {
                    ((ConditionAcitivity)context).deleteHang();
                }else if (type==2) {
                    ((ConditionAcitivity)context).deleteLie();
                }
                weidus.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.shaixuan.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ((ConditionAcitivity)context).onItemClick(type,position);
            }
        });
        return convertView;
    }
    


    private final class ViewHolder {
        TextView tv_name;
        ImageView deltet,shaixuan;
    }

}
