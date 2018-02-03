package com.ruisi.bi.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.adapter.GetSaveDataAdapter;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.SaveBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.GetSaveDataParser;
import com.ruisi.bi.app.parser.SaveSelectDataParser;
import com.ruisi.bi.app.view.EmptyView;
import com.ruisi.bi.app.view.LoadingDialog;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;


public class SaveDataListctivity extends Activity implements
		SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnSlideListener, ServerCallbackInterface {
	private String uudi = "";
    private String deletUUid = "";
	private SlideAndDragListView<SaveBean> baobiao_mulu_lv;
	private ArrayList<SaveBean> beans;
	private GetSaveDataAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppContext.setStatuColor(this);
		setContentView(R.layout.savedata_list_activty);
		LoadingDialog.createLoadingDialog(this);
		sendRequest();
		baobiao_mulu_lv = (SlideAndDragListView) findViewById(R.id.baobiao_mulu_lv);
		baobiao_mulu_lv.setOnListItemClickListener(this);
        baobiao_mulu_lv.setOnMenuItemClickListener(this);
		beans=new ArrayList<SaveBean>();
		adapter=new GetSaveDataAdapter(this, beans);

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
        baobiao_mulu_lv.setMenu(menu);

        baobiao_mulu_lv.setAdapter(adapter);
		baobiao_mulu_lv.setOnMenuItemClickListener(this);

		EmptyView ev = (EmptyView) findViewById(R.id.ev_empty);
		ev.setMessage("暂无数据");
		baobiao_mulu_lv.setEmptyView(ev);


	}

	private void sendRequest() {
		ServerEngine serverEngine = new ServerEngine(this);
		RequestVo rv = new RequestVo();
		rv.context = this;
		rv.functionPath = APIContext.getfenxiupdatalist;
		rv.parser = new GetSaveDataParser();
		rv.Type = APIContext.GET;
		uudi = UUID.randomUUID().toString();
		rv.uuId = uudi;
		rv.isSaveToLocation = true;

		Map<String, String> map = new HashMap<String, String>();
		map.put("token", UserMsg.getToken());
		rv.requestDataMap = map;
		serverEngine.addTaskWithConnection(rv);
	}
	private void sendDeletRequest(int id) {
		LoadingDialog.createLoadingDialog(this);
		ServerEngine serverEngine = new ServerEngine(this);
		RequestVo rv = new RequestVo();
		rv.context = this;
		rv.functionPath = APIContext.deletefenxiupdata;
		rv.parser = new SaveSelectDataParser();
		rv.Type = APIContext.GET;
		deletUUid = UUID.randomUUID().toString();
		rv.uuId = deletUUid;
		rv.isSaveToLocation = true;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id",id+"");
		map.put("token", UserMsg.getToken());
		rv.requestDataMap = map;
		serverEngine.addTaskWithConnection(rv);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	@Override
	public <T> void succeedReceiveData(T object, String uuid) {
		if (uudi.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			beans.clear();
			beans.addAll((Collection<? extends SaveBean>) object);
			adapter.notifyDataSetChanged();
		}
		if (deletUUid.equals(uuid)) {
			sendRequest();
		}
	}

	@Override
	public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
		if (uudi.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			Toast.makeText(this, errorMessage.getErrorDes(), Toast.LENGTH_SHORT).show();
		}
		if (deletUUid.equals(uuid)) {
			LoadingDialog.dimmissLoading();
			Toast.makeText(this, errorMessage.getErrorDes(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onListItemClick(View view, int position) {
		SaveBean sb=beans.get(position);
		Intent intent = new Intent(this, ConditionAcitivity.class);
		intent.putExtra("ThemeId", -1);
		intent.putExtra("id", sb.id);
		intent.putExtra("subjectname", sb.name);
		APIContext.isFromSave=true;
		startActivity(intent);
	}
	private void showDialogs(final int id) {
        baobiao_mulu_lv.closeSlidedItem();
		final Dialog dialog = new Dialog(this, R.style.customProgressDialog);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.layout_delete_view);
		dialog.findViewById(R.id.btn_exit_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
                        sendDeletRequest(id);
						dialog.dismiss();
					}
				});
		dialog.findViewById(R.id.btn_exit_no).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	}

	@Override
	public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        showDialogs(beans.get(itemPosition).id);
		return 0;
	}

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {

    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

    }
}
