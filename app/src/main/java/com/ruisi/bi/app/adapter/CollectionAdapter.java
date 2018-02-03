package com.ruisi.bi.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.CollectionBean;

import java.util.List;

/**
 * Created by berlython on 16/5/4.
 */
public class CollectionAdapter extends ArrayAdapter<CollectionBean> {

    private int resourceId;

    public CollectionAdapter(Context context, int resource, List<CollectionBean> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CollectionBean collection = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        } else {
            view = convertView;
        }
        TextView text = (TextView) view.findViewById(R.id.collection_item_id);
        text.setText(collection.title);
        return view;
    }
}
