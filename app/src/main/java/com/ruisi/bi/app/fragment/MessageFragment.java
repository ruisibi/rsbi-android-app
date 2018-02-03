package com.ruisi.bi.app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ruisi.bi.app.LoginActivity;
import com.ruisi.bi.app.MenuActivity;
import com.ruisi.bi.app.MessageDetailActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.WelecomActivity;
import com.ruisi.bi.app.adapter.Message2Adapter;
import com.ruisi.bi.app.bean.Message;
import com.ruisi.bi.app.bean.MessageObj;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.parser.MenuParser;
import com.ruisi.bi.app.parser.Message2Parsar;
import com.ruisi.bi.app.parser.MessageParser;
import com.ruisi.bi.app.util.NotificationsUtils;
import com.ruisi.bi.app.util.OffLineFenxi;
import com.ruisi.bi.app.util.TaskManager;
import com.ruisi.bi.app.view.EmptyView;
import com.ruisi.bi.app.view.LoadingDialog;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONException;
import org.jsoup.helper.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lib.homhomlib.design.SlidingLayout;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MessageFragment extends Fragment implements  ServerCallbackInterface, SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener, SlidingLayout.SlidingListener {

    private final static String lancherActivityClassName = WelecomActivity.class.getName();

    private SlideAndDragListView lvMessage;
    private Message2Adapter messageAdapter;
    private MessageObj datas;
    private String uuid;
    private RequestType currentRequestType;
    private Integer unreadCount;
    private Integer currentPage;
    private SlidingLayout slidingLayout;
    private SlidingLayout empty_slidingLayout;

    public final static String MESSAGE_OBJECT_KEY_SER = "com.ruisi.app.bean.message.ser";

    @Override
    public void onSlidingOffset(View view, float delta) {

    }

    @Override
    public void onSlidingStateChange(View view, int state) {
        if (state == 1 && datas != null) {

            float delta = slidingLayout.getSlidingDistance();
            float delta2 = empty_slidingLayout.getSlidingDistance();
            Log.d("onSlidingStateChange", "onSlidingStateChange: " + delta);
            if ((datas.rows.size() != 0 && delta > 0) || (datas.rows.size() == 0 && delta2 > 0)) {
                currentPage = 0;
                sendRequest(RequestType.GET_LIST, null, currentPage);
            } else if ((datas.rows.size() != 0 && delta < 0) || (datas.rows.size() == 0 && delta2 < 0)){
                if (datas.hasNext) {
                    currentPage++;
                    sendRequest(RequestType.GET_LIST, null, currentPage);
                } else {
                    Toast.makeText(getActivity(), "没有更多的数据", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onSlidingChangePointer(View view, int pointerId) {

    }


    private enum RequestType {
        GET_LIST, MSG2READ, DEL_MSG, DEL_ALL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MenuActivity)getActivity()).setTitleAndIconByType(MenuActivity.TitleType.MESSAGE);
        lvMessage = (SlideAndDragListView) this.getView().findViewById(R.id.lv_message);
        lvMessage.setOnListItemClickListener(this);
        lvMessage.setOnMenuItemClickListener(this);
        lvMessage.setOnMenuItemClickListener(this);
        datas = new MessageObj();
        datas.rows = new ArrayList<>();
        messageAdapter = new Message2Adapter(getActivity(), datas);
        Menu menu = new Menu(true, false, 0);//第1个参数表示在拖拽的时候 item 的背景是否透明，第2个参数表示滑动item是否能滑的过头，像弹簧那样(true表示过头，就像Gif中显示的那样；false表示不过头，就像Android QQ中的那样)
        menu.addItem(new MenuItem.Builder()
                .setWidth(180)//单个菜单button的宽度
                .setBackground(new ColorDrawable(Color.RED))//设置菜单的背景
                .setText("删除")//set text string
                .setTextColor(Color.WHITE)//set text color
                .setTextSize(20)
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());
        //set in sdlv
        lvMessage.setMenu(menu);
        lvMessage.setAdapter(messageAdapter);

        slidingLayout = (lib.homhomlib.design.SlidingLayout) this.getView().findViewById(R.id.slidingLayout);
        slidingLayout.setSlidingListener(this);

        empty_slidingLayout = (SlidingLayout) this.getView().findViewById(R.id.empty_slidingLayout);
        EmptyView ev = (EmptyView) getView().findViewById(R.id.ev_empty);
        if (ev != null) {
            ev.setMessage("暂无消息");
            lvMessage.setEmptyView(empty_slidingLayout);
            empty_slidingLayout.setSlidingListener(this);
        }


        currentPage = 0;
        sendRequest(RequestType.GET_LIST, null, currentPage);

        boolean result = NotificationsUtils.isNotificationEnabled(getActivity());
        if (!result) {
            showNotificationDialog();
        }
    }

    public void clear(){
        showDialogs(null);
    }

    private void sendRequest(RequestType type, String id, Integer page) {
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = getActivity();
        uuid = UUID.randomUUID().toString();
        rv.uuId = uuid;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        currentRequestType = type;
        if (RequestType.GET_LIST == type) {
            LoadingDialog.createLoadingDialog(getActivity());
            rv.functionPath = APIContext.getMsgList2;
            rv.parser = new Message2Parsar();
            rv.Type = APIContext.GET;
            map.put("token", UserMsg.getToken());
            map.put("page", page.toString());
        }
        if (RequestType.MSG2READ == type) {
            rv.functionPath = APIContext.msg2Read;
            rv.Type = APIContext.GET;
            rv.parser = new BaseParser() {
                @Override
                public <T> T parse(String jsonStr) throws JSONException {
                    return (T)jsonStr;
                }
            };
            map.put("token", UserMsg.getToken());
            map.put("id", id);
        }
        if (RequestType.DEL_MSG == type) {
            rv.functionPath = APIContext.delMsg;
            rv.Type = APIContext.GET;
            rv.parser = new BaseParser() {
                @Override
                public <T> T parse(String jsonStr) throws JSONException {
                    return (T)jsonStr;
                }
            };
            map.put("token", UserMsg.getToken());
            map.put("id", id);
        }
        if (RequestType.DEL_ALL == type) {
            rv.functionPath = APIContext.delAllMsg;
            rv.Type = APIContext.GET;
            rv.parser = new BaseParser() {
                @Override
                public <T> T parse(String jsonStr) throws JSONException {
                    return (T)jsonStr;
                }
            };
            map.put("token", UserMsg.getToken());
        }
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }


    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (this.uuid.equals(uuid)) {
            if (currentRequestType == RequestType.GET_LIST) {
                LoadingDialog.dimmissLoading();
                if (object instanceof ReLoginBean) {
                    Toast.makeText(getActivity(), "用户未登录！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                    return;
                }
                MessageObj message = (MessageObj)object;
                if (currentPage == 0) {
                    datas.rows.clear();
                }
                datas.hasNext = message.hasNext;
                datas.rows.addAll(message.rows);
                int count = 0;
                for (Message msg : message.rows) {
                    if (msg.state.equals("0")) {
                        count++;
                    }
                }
                unreadCount = count;
                ((MenuActivity)getActivity()).setBadgeViewCount(unreadCount);
                messageAdapter.notifyDataSetChanged();
            } else if (currentRequestType == RequestType.DEL_MSG || currentRequestType == RequestType.DEL_ALL) {
                currentPage = 0;
                sendRequest(RequestType.GET_LIST, null, currentPage);
            }
        }

    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (this.uuid.equals(uuid)) {
            if (currentRequestType == RequestType.GET_LIST) {
                LoadingDialog.dimmissLoading();
                if (currentPage != 0) {
                    currentPage--;
                }
                Toast.makeText(getActivity(), errorMessage.getErrorDes(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onListItemClick(View v, int position) {
        Message message = datas.rows.get(position);
        if (message.state.equals("0")) {
            sendRequest(RequestType.MSG2READ, message.id.toString(), null);
            message.state = "1";
            messageAdapter.notifyDataSetChanged();
            unreadCount--;
            ((MenuActivity)getActivity()).setBadgeViewCount(unreadCount);
        }
        Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MESSAGE_OBJECT_KEY_SER, message);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        Message message = datas.rows.get(0);
        showDialogs(message.id);
        return 0;
    }

    private void showNotificationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("消息通知已关闭");
        builder.setMessage("请进入“设置-应用-睿思云”设置启用通知");
        builder.setCancelable(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void showDialogs(final Integer id) {
        lvMessage.closeSlidedItem();
        final Dialog dialog = new Dialog(getActivity(), R.style.customProgressDialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_delete_view);
        dialog.findViewById(R.id.btn_exit_ok).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (id != null) {
                            sendRequest(RequestType.DEL_MSG, String.valueOf(id), null);
                            dialog.dismiss();
                        } else {
                            sendRequest(RequestType.DEL_ALL, null, null);
                            dialog.dismiss();
                        }
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
