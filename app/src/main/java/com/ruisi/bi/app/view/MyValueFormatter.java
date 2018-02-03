package com.ruisi.bi.app.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.ruisi.bi.app.ConditionAcitivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.WeiduOptionBean;
import com.ruisi.bi.app.view.ShaixuanPopwindow.ShaixuanAdapter;

import java.text.DecimalFormat;

public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    
    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }
    
    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + " $";
    }
    public static void ValueSelectedPopwindow(Context context,View v,float x,float y,String top,String bottom){
    	View mView = LayoutInflater.from(context).inflate(
				R.layout.value_popowindow_layout, null);
		((TextView) mView.findViewById(R.id.top_value)).setText(top);
		((TextView) mView.findViewById(R.id.bottom_value)).setText(bottom);
		final PopupWindow menuWindow = new PopupWindow(mView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setFocusable(true);
		menuWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		menuWindow.setTouchable(true);
		menuWindow.setContentView(mView);
		// int[] location = new int[2];
		// vs.getLocationOnScreen(location);
		menuWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, (int)x-100, (int)y+50);
    }
}
