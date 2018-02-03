package com.ruisi.bi.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ruisi.bi.app.adapter.PushItemAdapter;
import com.ruisi.bi.app.bean.PushListItem;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.parser.PushListItemParser;
import com.ruisi.bi.app.view.LoadingDialog;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by berlython on 16/5/13.
 */
public class PushSettingListActivity extends Activity implements SlideAndDragListView.OnListItemClickListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnSlideListener,ServerCallbackInterface{

    private String reqUUID;
    private RequestType requestType;

    private SlideAndDragListView<PushListItem> lvPushItem;
    private List<PushListItem> data;
    private PushItemAdapter pushITemAdapter;



    private enum RequestType {
        GET_PUSH_LIST,
        DEL_PUSH,
        START_PUSH,
        STOP_PUSH;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.activity_push_setting_list);

        lvPushItem = (SlideAndDragListView) findViewById(R.id.lv_push_setting);
        data = new ArrayList<>();
        pushITemAdapter = new PushItemAdapter(this, data);
        lvPushItem.setOnListItemClickListener(this);
        lvPushItem.setOnMenuItemClickListener(this);
        lvPushItem.setOnSlideListener(this);

        List<Menu> menuList = new ArrayList<>();

        Menu menu1 = new Menu(true, false, 0);//第1个参数表示在拖拽的时候 item 的背景是否透明，第2个参数表示滑动item是否能滑的过头，像弹簧那样(true表示过头，就像Gif中显示的那样；false表示不过头，就像Android QQ中的那样)
        menu1.addItem(new MenuItem.Builder()
                .setWidth(180)//单个菜单button的宽度
                .setBackground(new ColorDrawable(Color.RED))//设置菜单的背景
                .setText("删除")//set text string
                .setTextColor(Color.WHITE)//set text color
                .setTextSize(20)
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());
        menu1.addItem(new MenuItem.Builder()
                .setWidth(180)
                .setBackground(new ColorDrawable(Color.LTGRAY))
                .setText("禁用")
                .setTextColor(Color.WHITE)
                .setTextSize(20)
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());

        Menu menu2 = new Menu(true, false, 1);//第1个参数表示在拖拽的时候 item 的背景是否透明，第2个参数表示滑动item是否能滑的过头，像弹簧那样(true表示过头，就像Gif中显示的那样；false表示不过头，就像Android QQ中的那样)
        menu2.addItem(new MenuItem.Builder()
                .setWidth(180)//单个菜单button的宽度
                .setBackground(new ColorDrawable(Color.RED))//设置菜单的背景
                .setText("删除")//set text string
                .setTextColor(Color.WHITE)//set text color
                .setTextSize(20)
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());
        menu2.addItem(new MenuItem.Builder()
                .setWidth(180)
                .setBackground(new ColorDrawable(Color.GREEN))
                .setText("启用")
                .setTextColor(Color.WHITE)
                .setTextSize(20)
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());

        menuList.add(menu1);
        menuList.add(menu2);
        lvPushItem.setMenu(menuList);

        lvPushItem.setAdapter(pushITemAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPushList();
    }

    private void getPushList() {
        LoadingDialog.createLoadingDialog(this);
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.getPushList;
        rv.parser = new PushListItemParser();
        rv.Type = APIContext.GET;
        reqUUID = UUID.randomUUID().toString();
        rv.uuId = reqUUID;
        requestType = RequestType.GET_PUSH_LIST;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    private void delPush(Integer tid){
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.delPush;
        rv.Type = APIContext.GET;
        rv.parser = new BaseParser() {
            @Override
            public <T> T parse(String jsonStr) throws JSONException {
                return (T)jsonStr;
            }
        };
        reqUUID = UUID.randomUUID().toString();
        rv.uuId = reqUUID;
        requestType = RequestType.DEL_PUSH;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", tid.toString());
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    private void startOrStopPush(Integer type, Integer id){

        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;


        if (type == 1) {
            requestType = RequestType.STOP_PUSH;
            rv.functionPath = APIContext.stopPush;
        } else {
            requestType = RequestType.START_PUSH;
            rv.functionPath = APIContext.startPush;
        }

        rv.Type = APIContext.GET;
        rv.parser = new BaseParser() {
            @Override
            public <T> T parse(String jsonStr) throws JSONException {
                return (T)jsonStr;
            }
        };
        reqUUID = UUID.randomUUID().toString();
        rv.uuId = reqUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", id.toString());
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);

    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (requestType == RequestType.GET_PUSH_LIST) {
            if (uuid.equals(reqUUID)) {
                LoadingDialog.dimmissLoading();
                data.clear();
                data.addAll((Collection<? extends PushListItem>) object);
                pushITemAdapter.notifyDataSetChanged();
            }
        } else if (requestType == RequestType.DEL_PUSH){
            if (uuid.equals(reqUUID)) {
//                Toast.makeText(this, "删除推送配置成功", Toast.LENGTH_SHORT).show();
                getPushList();
            }
        } else if (requestType == RequestType.START_PUSH) {
            if (uuid.equals(reqUUID)) {
//                Toast.makeText(this, "启用推送配置成功", Toast.LENGTH_SHORT).show();
                getPushList();
            }
        } else if (requestType == RequestType.STOP_PUSH) {
            if (uuid.equals(reqUUID)) {
//                Toast.makeText(this, "禁用推送配置成功", Toast.LENGTH_SHORT).show();
                getPushList();
            }
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (requestType == RequestType.GET_PUSH_LIST) {
            if (uuid.equals(reqUUID)) {
                LoadingDialog.dimmissLoading();
                Toast.makeText(this, "获取推送配置列表失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestType == RequestType.DEL_PUSH){
            if (uuid.equals(reqUUID)) {
//                Toast.makeText(this, "删除推送配置失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestType == RequestType.START_PUSH) {
            if (uuid.equals(reqUUID)) {
//                Toast.makeText(this, "启用推送配置失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestType == RequestType.STOP_PUSH) {
            if (uuid.equals(reqUUID)) {
//                Toast.makeText(this, "禁用推送配置失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onListItemClick(View v, int position) {
        PushListItem pushListItem = data.get(position);
        Integer pushId = pushListItem.pushId;
        Intent intent = new Intent(PushSettingListActivity.this, PushCreateActivity.class);
        intent.putExtra("pushId", pushId);
        startActivity(intent);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        if (buttonPosition == 1) {
            PushListItem item =  data.get(itemPosition);
            startOrStopPush(item.state, item.pushId);
            item.state = item.state == 0 ? 1 : 0;
            pushITemAdapter.notifyDataSetChanged();
        } else if (buttonPosition == 0) {
            Integer id = data.get(itemPosition).pushId;
            showDialogs(id);
        }
        return 0;
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_add_push:
//                Toast.makeText(this, "add click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PushSettingListActivity.this, PushCreateActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void showDialogs(final Integer id) {
        final Dialog dialog = new Dialog(this, R.style.customProgressDialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_delete_view);
        dialog.findViewById(R.id.btn_exit_ok).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delPush(id);
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.btn_exit_no).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
    }
}
