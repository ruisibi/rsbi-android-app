package com.ruisi.bi.app.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.AnalysisActivity;
import com.ruisi.bi.app.BaobiaoMuluAcitvity;
import com.ruisi.bi.app.DataInputActivity;
import com.ruisi.bi.app.LoginActivity;
import com.ruisi.bi.app.MenuActivity;
import com.ruisi.bi.app.PushSettingListActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.SaveDataListctivity;
import com.ruisi.bi.app.SystemHelperActivity;
import com.ruisi.bi.app.adapter.MenuAdapter;
import com.ruisi.bi.app.bean.MenuBean;
import com.ruisi.bi.app.bean.ReLoginBean;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.MenuParser;
import com.ruisi.bi.app.util.OffLineFenxi;
import com.ruisi.bi.app.util.TaskManager;
import com.ruisi.bi.app.view.LoadingDialog;
import com.ruisi.bi.app.view.MenuPopwindow;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MenuListFragment extends Fragment implements View.OnClickListener, ServerCallbackInterface,
        AdapterView.OnItemClickListener, OffLineFenxi.OffLineCallBack {

    private ListView lv;
    private MenuAdapter mainAdapter;
    private List<MenuBean> datas;
    private String MenuUUID;
    private ServerCheckTask sct;

    private TextView networkWarn_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MenuActivity)getActivity()).setTitleAndIconByType(MenuActivity.TitleType.MENU);
        lv = (ListView) this.getView().findViewById(R.id.MainActivity_listview);

        networkWarn_tv = (TextView) getView().findViewById(R.id.MainActivity_network_warning);
        networkWarn_tv.setOnClickListener(this);

        lv.setOnItemClickListener(this);
        datas = new ArrayList<>();
        mainAdapter = new MenuAdapter(getActivity(), datas);
        lv.setAdapter(mainAdapter);
        LoadingDialog.createLoadingDialog(getActivity());
        sendRequest();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sct == null) {
            sct = new ServerCheckTask();
            sct.execute();
        } else if (sct.isCancelled()){
            sct.execute();
        }
    }

    private void sendRequest() {
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = getActivity();
        rv.functionPath = APIContext.Menu2;
        rv.parser = new MenuParser();
        rv.Type = APIContext.GET;
        MenuUUID = UUID.randomUUID().toString();
        rv.uuId = MenuUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", UserMsg.getToken());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    public void onClick(View v) {
        if (v == networkWarn_tv) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            if (!sct.isCancelled()){
                sct.cancel(true);
            }
            getActivity().finish();
        }
    }

    @Override
    public void callBack(Object obj) {
        LoadingDialog.dimmissLoading();
        datas.clear();
        datas.addAll((Collection<? extends MenuBean>) obj);
        mainAdapter.notifyDataSetChanged();
    }

    @Override
    public void callBackFail(String obj) {
        Toast.makeText(getActivity(), obj, Toast.LENGTH_SHORT).show();
    }

    public void showSettingView(){
        MenuPopwindow mm=new MenuPopwindow();
        mm.getMenuPopwindow(getActivity(), lv);
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (MenuUUID.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            if (object instanceof ReLoginBean) {
                Toast.makeText(getActivity(), "用户未登录！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                return;
            }
            datas.clear();
            datas.addAll((Collection<? extends MenuBean>) object);
            mainAdapter.notifyDataSetChanged();
            UserMsg.saveTheme(((ArrayList<MenuBean>) object).toString());

            if (networkWarn_tv.getVisibility() == View.VISIBLE) {
                networkWarn_tv.setVisibility(View.GONE);
                networkWarn_tv.setText(null);
            }
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (MenuUUID.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            if (getActivity() != null) {
                Toast.makeText(getActivity(), errorMessage.getErrorDes(), Toast.LENGTH_SHORT).show();
            }
            String data = UserMsg.getThemes();
            TaskManager.getInstance().addTask(
                    new OffLineFenxi(new MenuParser(), data, this));

            if (networkWarn_tv.getVisibility() != View.VISIBLE) {
                networkWarn_tv.setVisibility(View.VISIBLE);
                networkWarn_tv.setText("网络异常,请切换登录服务器");
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
        MenuBean menu = datas.get(pos);
        int mid = menu.id;
        if (mid == 0) {
            Intent intent = new Intent(getActivity(), DataInputActivity.class);
            intent.putExtra("url", menu.uri);
            startActivity(intent);
        } else if (mid == 1) {
            startActivity(new Intent(getActivity(), AnalysisActivity.class));
       } else if (mid == 2) {
            startActivity(new Intent(getActivity(), SaveDataListctivity.class));
        } else if (mid == 3) {
            startActivity(new Intent(getActivity(), BaobiaoMuluAcitvity.class));
        } else if (mid == 4) {
            startActivity(new Intent(getActivity(), PushSettingListActivity.class));
        } else if(mid == 5){
            Intent intent = new Intent(getActivity(), SystemHelperActivity.class);
            intent.putExtra("url", menu.uri);
            startActivity(intent);
        }
    }

    private class ServerCheckTask extends AsyncTask<Void, Integer, Boolean> {

        private final String TAG = "MenuAc.$ServerCheckTask";

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                while (true) {
                    sendRequest4Test();
                    Thread.sleep(60000);
                }

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Integer result = values[0];
            switch (result) {
                case 1:
                    networkWarn_tv.setVisibility(View.GONE);
                    networkWarn_tv.setText(null);
                    break;
                case 0:
                default:
                    networkWarn_tv.setVisibility(View.VISIBLE);
                    networkWarn_tv.setText("网络异常,请切换登录服务器");
                    break;

            }

        }

        private void sendRequest4Test(){
            Log.d(TAG, "send test request");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(APIContext.HOST + APIContext.userMessage + "?token=" + UserMsg.getToken());
                HttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    publishProgress(1);
                }else {
                    publishProgress(0);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                publishProgress(0);
            }
        }

    }
}
