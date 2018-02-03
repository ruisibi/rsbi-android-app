package com.ruisi.bi.app.util;

import org.json.JSONException;

import com.ruisi.bi.app.parser.BaseParser;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class OffLineFenxi implements Runnable {
    private OffLineCallBack olcb;
    private BaseParser parser;
    private String data;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 1:
                olcb.callBack(msg.obj);
                break;
            case 2:
                olcb.callBackFail("离线解析失败！！！");
                break;

            default:
                break;
            }
            
        }

    };

    public OffLineFenxi( BaseParser parser, String data,
            OffLineCallBack cb) {
        this.olcb = cb;
        this.parser = parser;
        this.data = data;
    }

    @Override
    public void run() {
        Message msg = new Message();
        try {
            msg.obj = parser.parse(data);
            msg.what=1;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            msg.what=2;
        }
        handler.sendMessage(msg);
    }

    public interface OffLineCallBack {
        public void callBack(Object obj);
        public void callBackFail(String obj);
    }

}
