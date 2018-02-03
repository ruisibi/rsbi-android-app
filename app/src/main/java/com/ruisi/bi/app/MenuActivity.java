package com.ruisi.bi.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.viewbadger.BadgeView;
import com.ruisi.bi.app.bean.Message;
import com.ruisi.bi.app.bean.PushListItem;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.fragment.CollectionFragment;
import com.ruisi.bi.app.fragment.MenuListFragment;
import com.ruisi.bi.app.fragment.MessageFragment;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.parser.MessageParser;
import com.ruisi.bi.app.view.LoadingDialog;
import com.ruisi.bi.app.view.MenuPopwindow;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MenuActivity extends FragmentActivity implements OnClickListener, ServerCallbackInterface {
    private ImageView setting_iv;

    private TextView tv_title;
    private FragmentTabHost tabHost;
    private String uuid;
    private BadgeView badgeView;
    private TextView tvClear;

    private final String TAB_TAG_FUNCTION = "tag_function";
    private final String TAB_TAG_COLLECTION = "tag_collection";
    private final String TAB_TAG_MESSAGE = "tag_message";

    public enum TitleType{
        MENU, MESSAGE, COLLECTION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppContext.setStatuColor(this);
        tv_title = (TextView) findViewById(R.id.title);
        setting_iv = (ImageView) findViewById(R.id.setting);
        setting_iv.setOnClickListener(this);
        tvClear = (TextView) findViewById(R.id.tv_clear);
        tvClear.setOnClickListener(this);


        int pushFlag = getIntent().getIntExtra("pushFlag", -1);

        tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);

        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        tabHost.getTabWidget().setBackgroundColor(Color.WHITE);
        tabHost.getTabWidget().setDividerDrawable(getResources().getDrawable(R.color.white));
        //1
        tabHost.addTab(tabHost.newTabSpec(TAB_TAG_FUNCTION)
                        .setIndicator("功能", getResources().getDrawable(R.drawable.sel_menu)),
                MenuListFragment.class,
                null);

        tabHost.addTab(tabHost.newTabSpec(TAB_TAG_MESSAGE)
                        .setIndicator("消息", getResources().getDrawable(R.drawable.sel_message)),
                MessageFragment.class,
                null);
        //2
        tabHost.addTab(tabHost.newTabSpec(TAB_TAG_COLLECTION)
                        .setIndicator("收藏", getResources().getDrawable(R.drawable.sel_collection)),
                CollectionFragment.class,
                null);

        tabHost.getTabWidget().getChildTabViewAt(0).setBackgroundColor(Color.WHITE);
        tabHost.getTabWidget().getChildTabViewAt(1).setBackgroundColor(Color.WHITE);
        tabHost.getTabWidget().getChildTabViewAt(2).setBackgroundColor(Color.WHITE);

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.GRAY);
        }

        TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tv.setTextColor(Color.rgb(29, 174, 241));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {

                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tv.setTextColor(Color.LTGRAY);
                }
                TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.rgb(29, 174, 241));


            }
        });

        tabHost.getTabWidget().setStripEnabled(false);

        if (pushFlag == 1) {
            tabHost.setCurrentTabByTag(TAB_TAG_MESSAGE);
        }
    }

    public void sendRequest() {
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        uuid = UUID.randomUUID().toString();
        rv.uuId = uuid;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        rv.functionPath = APIContext.getMsgList;
        rv.parser = new MessageParser();
        rv.Type = APIContext.GET;
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sendRequest();
    }


    @Override
    public void onClick(View v) {
        if (v == setting_iv) {
            ((MenuListFragment)getSupportFragmentManager().findFragmentByTag(TAB_TAG_FUNCTION)).showSettingView();
        } else if(v == tvClear){
            ((MessageFragment)getSupportFragmentManager().findFragmentByTag(TAB_TAG_MESSAGE)).clear();
        }
    }

    public void setTitleAndIconByType(TitleType type){
        if (type == TitleType.MENU) {
            tv_title.setText("菜单");
            setting_iv.setVisibility(View.VISIBLE);
            tvClear.setVisibility(View.INVISIBLE);
        } else if (type == TitleType.MESSAGE) {
            tv_title.setText("消息");
            setting_iv.setVisibility(View.INVISIBLE);
            tvClear.setVisibility(View.VISIBLE);
        } else if (type == TitleType.COLLECTION) {
            tv_title.setText("收藏");
            setting_iv.setVisibility(View.INVISIBLE);
            tvClear.setVisibility(View.INVISIBLE);
        }
    }

    public void setBadgeViewCount(Integer count){
        if (badgeView != null) {
            if (count != 0){
                if (count > 99) {
                    badgeView.setText("99+");
                } else {
                    badgeView.setText("" + count);
                }
                badgeView.show();
                ShortcutBadger.applyCount(getApplicationContext(), 1); //for 1.1.4+
            } else {
                badgeView.hide();
                ShortcutBadger.applyCount(getApplicationContext(), 0); //for 1.1.4+
            }

        } else {
            if (count != 0) {
                ImageView peopleIndicator = (ImageView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.icon);
                badgeView = new BadgeView(this, peopleIndicator);
                if (count > 99) {
                    badgeView.setText("99+");
                    ShortcutBadger.applyCount(getApplicationContext(), 1); //for 1.1.4+
                } else {
                    badgeView.setText("" + count);
                    ShortcutBadger.applyCount(getApplicationContext(), 0); //for 1.1.4+
                }
                badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                badgeView.setTextSize(10);
                badgeView.show();
            }
        }
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (uuid.equals(this.uuid)) {

            if (object instanceof ReLoginBean)
            {
                Toast.makeText(this,"用户未登录", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MenuActivity.this,LoginActivity.class));
                this.finish();
                return;
            }
            List<Message> items = (List<Message>)object;
            int count = 0;
            for (Message item : items) {
                if (item.state.equals("0")) {
                    count++;
                }
            }
            setBadgeViewCount(count);

        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {


    }
}
