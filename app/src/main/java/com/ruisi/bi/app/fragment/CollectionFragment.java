package com.ruisi.bi.app.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ruisi.bi.app.LoginActivity;
import com.ruisi.bi.app.MenuActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.Webviewactivity;
import com.ruisi.bi.app.adapter.CollectionAdapter;
import com.ruisi.bi.app.bean.CollectionBean;
import com.ruisi.bi.app.bean.MenuBean;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.parser.CollectionParser;
import com.ruisi.bi.app.parser.MenuParser;
import com.ruisi.bi.app.util.OffLineFenxi;
import com.ruisi.bi.app.util.TaskManager;
import com.ruisi.bi.app.view.EmptyView;
import com.ruisi.bi.app.view.LoadingDialog;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;



/**
 * Created by berlython on 16/5/3.
 */
public class CollectionFragment extends Fragment implements ServerCallbackInterface, SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener {

    private final String TAG = "CollectionFragment";

    private SlideAndDragListView lvCollection;

    private List<CollectionBean> dataList = new ArrayList<>();
    private CollectionAdapter adapter;

    private String collectUUID;

    private RequestType currentType;

    private enum RequestType{
        GET_LIST, DEL_COLLECTION;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MenuActivity)getActivity()).setTitleAndIconByType(MenuActivity.TitleType.COLLECTION);
        lvCollection = (SlideAndDragListView) getView().findViewById(R.id.lv_collection);
        lvCollection.setOnListItemClickListener(this);
        lvCollection.setOnMenuItemClickListener(this);

        Menu menu = new Menu(true, false, 0);//第1个参数表示在拖拽的时候 item 的背景是否透明，第2个参数表示滑动item是否能滑的过头，像弹簧那样(true表示过头，就像Gif中显示的那样；false表示不过头，就像Android QQ中的那样)
        menu.addItem(new com.yydcdut.sdlv.MenuItem.Builder()
                .setWidth(180)//单个菜单button的宽度
                .setBackground(new ColorDrawable(Color.RED))//设置菜单的背景
                .setText("删除")//set text string
                .setTextColor(Color.WHITE)//set text color
                .setTextSize(20)
                .setDirection(com.yydcdut.sdlv.MenuItem.DIRECTION_RIGHT)
                .build());
        //set in sdlv
        lvCollection.setMenu(menu);

        adapter = new CollectionAdapter(getActivity(), R.layout.collection_item, dataList);
        lvCollection.setAdapter(adapter);

        EmptyView ev = (EmptyView) getView().findViewById(R.id.ev_empty);
        ev.setMessage("暂无收藏");
        lvCollection.setEmptyView(ev);


    }

    @Override
    public void onResume() {
        super.onResume();
        sendRequest();
    }

    private void sendRequest() {
        LoadingDialog.createLoadingDialog(getActivity());
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = getActivity();
        rv.functionPath = APIContext.getCollection;
        currentType = RequestType.GET_LIST;
        rv.parser = new CollectionParser();
        rv.Type = APIContext.GET;
        collectUUID = UUID.randomUUID().toString();
        rv.uuId = collectUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    private void sendDeleteRequest(String rid) {
        LoadingDialog.createLoadingDialog(getActivity());
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = getActivity();
        rv.functionPath = APIContext.delCollection;
        currentType = RequestType.DEL_COLLECTION;
        rv.parser = new BaseParser() {
            @Override
            public <T> T parse(String jsonStr) throws JSONException {
                return (T)jsonStr;
            }
        };
        rv.Type = APIContext.GET;
        collectUUID = UUID.randomUUID().toString();
        rv.uuId = collectUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", UserMsg.getToken());
        map.put("rid", rid);
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }


    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (collectUUID.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            if (RequestType.GET_LIST == currentType) {
                if (object instanceof ReLoginBean) {
                    Toast.makeText(getActivity(), "用户未登录！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                    return;
                }
                dataList.clear();
                dataList.addAll((Collection<? extends CollectionBean>) object);
                adapter.notifyDataSetChanged();
            } else {
                sendRequest();
            }
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (collectUUID.equals(uuid)) {
            Toast.makeText(getActivity(), "获取收藏数据失败", Toast.LENGTH_SHORT).show();
            Log.d(TAG, errorMessage.getErrormessage());
        }
    }

    @Override
    public void onListItemClick(View v, int position) {
        CollectionBean collection = dataList.get(position);
        Intent intent = new Intent(getActivity(), Webviewactivity.class);
        intent.putExtra("rid", collection.rid);
        intent.putExtra("title", collection.title);
        intent.putExtra("url", collection.url);
        intent.putExtra("iscollect", 1);
        startActivity(intent);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        CollectionBean bean = dataList.get(itemPosition);
        showDialog(bean.rid);
        return 0;
    }
    public void showDialog(final String rid) {
        lvCollection.closeSlidedItem();
        final Dialog dialog = new Dialog(getActivity(), R.style.customProgressDialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_delete_view);
        dialog.findViewById(R.id.btn_exit_ok).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDeleteRequest(rid);
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
