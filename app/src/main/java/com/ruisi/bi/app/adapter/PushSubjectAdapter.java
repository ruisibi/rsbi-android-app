package com.ruisi.bi.app.adapter;

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

import org.jsoup.helper.StringUtil;

import java.util.List;

/**
 * Created by berlython on 16/5/27.
 */
public class PushSubjectAdapter extends BaseAdapter {

    private Context context;

    private List<PushSubject> data;

    private Integer selectedId;

    private SubjectType subjectType;

    public enum SubjectType{
        DAY, MONTH;
    }

    public PushSubjectAdapter(Context context, List<PushSubject> data, Integer selectedId, SubjectType subjectType){
        this.context = context;
        this.data = data;
        this.selectedId = selectedId;
        this.subjectType = subjectType;
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
        return data.get(position).tId;
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
        PushSubject ps = data.get(position);
        viewHolder.tv_name.setText(ps.tDesc);
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
        if (ps.tId == selectedId){
            viewHolder.cb_selected.setChecked(true);
        } else {
            viewHolder.cb_selected.setChecked(false);
        }

        return convertView;
    }

    private void update(Integer position){
        PushSubject ps = data.get(position);
        selectedId = ps.tId;

        if (this.context != null && this.context instanceof PushCreateActivity) {
            ((PushCreateActivity)this.context).setSelectedSubjectId(ps.tId, this.subjectType);
        }

        notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox cb_selected;
        TextView tv_name;
    }
}
