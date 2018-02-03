package com.ruisi.bi.app.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.PushItem;
import com.ruisi.bi.app.bean.PushListItem;

import java.util.List;

public class PushItemAdapter extends BaseAdapter {

    private List<PushListItem> data;
    private Context context;

    public PushItemAdapter(Context context, List<PushListItem> data) {
        this.context = context;
        this.data = data;
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
        return data.get(position).pushId;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).state == 1) {
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;//menu type count
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.savedata_list_activty_item, null);
            vh=new ViewHolder();
            vh.text = (TextView) convertView
                    .findViewById(R.id.OptionAdapter_text);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        PushListItem item = data.get(position);
        String text = item.pushTitle;
        vh.text.setText(text);
        if (item.state == 0) {
            vh.text.setTextColor(Color.LTGRAY);
        } else {
            vh.text.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView text;
    }
}
