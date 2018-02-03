package com.ruisi.bi.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.fragment.TuBingxingFragment;
import com.ruisi.bi.app.fragment.TuLeidaFragment;
import com.ruisi.bi.app.fragment.TuQuxianFragment;
import com.ruisi.bi.app.fragment.TuTiaoxingFragment;
import com.ruisi.bi.app.fragment.TuZhuxingFragment;
import com.ruisi.bi.app.view.SelectTuPopWindow;

import org.json.JSONException;
import org.json.JSONObject;

public class TuActivity extends FragmentActivity {
    public static String strJsons;
    public static String tags;
    private static String title;
    private static String zhibiao;
    private FrameLayout container;
    private TuQuxianFragment quxianFragment;
    private TuZhuxingFragment zhuxingFragment;
    private TuBingxingFragment bingxingFragment;
    private TuTiaoxingFragment tiaoxingFragment;
    private TuLeidaFragment leidaFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ImageView select;

    public static void startThis(Context context, String strJson, String tag, String titles, String zhibiaoTitle) {
        strJsons = strJson;
        tags = tag;
        title = titles;
        zhibiao = zhibiaoTitle;
        context.startActivity(new Intent(context, TuActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.tu_activity_layout);
//		((TextView) findViewById(R.id.title)).setText(title);
        container = (FrameLayout) findViewById(R.id.TuActivity_container);
        select = (ImageView) findViewById(R.id.select);

        initFragments();
    }

    private void initFragments() {
        fm = getSupportFragmentManager();
        quxianFragment = new TuQuxianFragment(strJsons, zhibiao);
        zhuxingFragment = new TuZhuxingFragment(getRequestJson("column"), zhibiao);
        bingxingFragment = new TuBingxingFragment(getRequestJson("pie"));
        tiaoxingFragment = new TuTiaoxingFragment(getRequestJson("bar"), zhibiao);
        leidaFragment = new TuLeidaFragment(getRequestJson("radar"));
        ft = fm.beginTransaction();
        ft.replace(R.id.TuActivity_container, quxianFragment).commit();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.select:
                SelectTuPopWindow.getMenuPopwindow(this, container);
                break;


            default:
                break;
        }
    }

    private String getRequestJson(String type) {
        if (strJsons == null) return null;

        JSONObject obj1 = null;
        try {
            obj1 = new JSONObject(strJsons);
            obj1.getJSONObject("chartJson").put("type", type);
            return obj1.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void onItemSelected(int position) {
        switch (position) {
            case 0:
                ft = fm.beginTransaction();
                ft.replace(R.id.TuActivity_container, quxianFragment).commit();
                break;
            case 1:
                ft = fm.beginTransaction();
                ft.replace(R.id.TuActivity_container, zhuxingFragment).commit();
                break;
            case 2:
                ft = fm.beginTransaction();
                ft.replace(R.id.TuActivity_container, bingxingFragment).commit();
                break;
            case 3:
                ft = fm.beginTransaction();
                ft.replace(R.id.TuActivity_container, tiaoxingFragment).commit();
                break;
//		case 4:
//			ft = fm.beginTransaction();
//			ft.replace(R.id.TuActivity_container, bingxingFragment).commit();
//			break;
            case 4:
                ft = fm.beginTransaction();
                ft.replace(R.id.TuActivity_container, leidaFragment).commit();
                break;
            default:
                break;
        }

    }

}
