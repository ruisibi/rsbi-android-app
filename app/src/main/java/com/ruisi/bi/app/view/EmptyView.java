package com.ruisi.bi.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruisi.bi.app.R;

/**
 * Created by berly on 2016/8/23.
 */
public class EmptyView extends RelativeLayout {

    private TextView tvMessage;

    public EmptyView(Context context,AttributeSet attrs ) {
        super(context, attrs);
        init();
    }

    public void setMessage(String text) {
        this.tvMessage.setText(text);
    }

    private void init() {
        inflate(getContext(), R.layout.view_empty, this);
        this.tvMessage = (TextView) findViewById(R.id.ve_message);
    }


}
