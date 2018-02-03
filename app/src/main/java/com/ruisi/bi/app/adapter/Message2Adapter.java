package com.ruisi.bi.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.Message;
import com.ruisi.bi.app.bean.MessageObj;

import java.util.List;

/**
 * Created by berlython on 16/6/8.
 */
public class Message2Adapter extends BaseAdapter {

    private Context context;
    private MessageObj data;

    public Message2Adapter(Context context, MessageObj data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.rows.size();
    }

    @Override
    public Object getItem(int position) {
        return data.rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.rows.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message, null);
            vh=new ViewHolder();
            vh.imgDayMonth = (ImageView) convertView
                    .findViewById(R.id.img_day_month);
            vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            vh.tvDataDate = (TextView) convertView.findViewById(R.id.tv_datadate);
            BadgeView badgeView = new BadgeView(context, vh.imgDayMonth);
            badgeView.setWidth(20);
            badgeView.setHeight(20);
            vh.badgeView = badgeView;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Message message = data.rows.get(position);
        if ("day".equals(message.pushType)) {
            vh.imgDayMonth.setImageResource(R.mipmap.appri);
        } else {
            vh.imgDayMonth.setImageResource(R.mipmap.appyue);
        }
        if ("0".equals(message.state)) {
            vh.badgeView.show();
        } else {
            vh.badgeView.hide();
        }
        vh.tvTitle.setText(message.title);
        vh.tvDataDate.setText(message.datadate.replaceAll("-", "/"));
        return convertView;
    }

    private class ViewHolder {
        ImageView imgDayMonth;
        TextView tvTitle;
        TextView tvDataDate;
        BadgeView badgeView;
    }
}
