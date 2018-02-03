package com.ruisi.bi.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.ZhibiaoBean;

public class ZhibiaoShowAdapter extends BaseAdapter {
    private Context context;
    private List<ZhibiaoBean> zhibiaos;
    private LayoutInflater inflater;

    public ZhibiaoShowAdapter(Context context, List<ZhibiaoBean> zhibiaos) {
        this.inflater = LayoutInflater.from(context);
        this.zhibiaos = zhibiaos;
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
            convertView = inflater.inflate(R.layout.delete_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.deltet = (ImageView) convertView
                    .findViewById(R.id.delete_item_iv);
            holder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_contacts_item_name);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ZhibiaoBean weiduBean=zhibiaos.get(position);
        holder.tv_name.setText(weiduBean.text);
        holder.deltet.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                zhibiaos.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private final class ViewHolder {
        TextView tv_name;
        ImageView deltet;
    }

}
