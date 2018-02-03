package com.ruisi.bi.app.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ruisi.bi.app.ConditionAcitivity;
import com.ruisi.bi.app.R;

public class MyPopwindow {
    public static PopupWindow getSaveNotePoPwindow(final Context context,
            View v,  BaseAdapter adapter, final int flag) {
        View mView = LayoutInflater.from(context).inflate(
                R.layout.select_popwindow_layout, null);
        ListView lv = (ListView) mView.findViewById(R.id.MyPopwindow_listview);
        lv.setAdapter(adapter);
        TextView title=(TextView) mView.findViewById(R.id.MyPopwindow_title);
        if(flag==0){
            title.setText("选择维度");
        }else if(flag==1){
            title.setText("选择维度");
        }else if(flag==2){
            title.setText("选择维度");
        }else if(flag==3){
            title.setText("选择指标");
        }
        
        final PopupWindow menuWindow = new PopupWindow(mView,
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        menuWindow.setOutsideTouchable(true);
        menuWindow.setFocusable(true);
        menuWindow.setBackgroundDrawable(new ColorDrawable(00000));
        menuWindow.setTouchable(true);
        menuWindow.setContentView(mView);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        menuWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, 0, 0);
        mView.findViewById(R.id.cancle).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        menuWindow.dismiss();
                    }
                });

        mView.findViewById(R.id.commit).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((ConditionAcitivity)context).setListViewData(flag);
                        menuWindow.dismiss();
                    }
                });
        mView.findViewById(R.id.MyPopwindow_back).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        menuWindow.dismiss();
                    }
                });

        
        return menuWindow;
    }
}
