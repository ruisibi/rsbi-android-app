package com.ruisi.bi.app;

import java.util.HashMap;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushManager;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.UserBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.LoginParser;
import com.ruisi.bi.app.view.DropEditText;
import com.ruisi.bi.app.view.LoadingDialog;

public class LoginActivity extends Activity implements OnClickListener,
        ServerCallbackInterface {
    private EditText et_username, et_pwd;
    private DropEditText drop_edit;
    private Button bt_commit;
    private String LoginUUID;
    private JSONArray array;

    private RequestType currentType;

    private enum RequestType {
        LOGIN, UPD_CHANNEL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.atc_login_layout);
        // 透明状态栏
        getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        initView();
        initData();
        setLisener();
    }

    private void initView() {
        drop_edit = (DropEditText) findViewById(R.id.drop_edit);
        et_username = (EditText) findViewById(R.id.LoginActivity_username);
        et_pwd = (EditText) findViewById(R.id.LoginActivity_pwd);
        bt_commit = (Button) findViewById(R.id.LoginActivity_commit);
    }

    private void initData() {
        if (UserMsg.getIPJson() == null) {
            array = new JSONArray();
//			array.put("http://112.124.13.251:8081/bi/");
            array.put(APIContext.HOST);
        } else {
            try {
                array = new JSONArray(UserMsg.getIPJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        drop_edit.setAdapter(new IpAdapter());
        try {
            drop_edit.setDefaultText((String) array.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLisener() {
        bt_commit.setOnClickListener(this);
        findViewById(R.id.getCount).setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.getCount:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.ruisitech.com/appsy.html");
                intent.setData(content_url);
                startActivity(intent);

                break;
            case R.id.LoginActivity_commit:
                LoadingDialog.createLoadingDialog(this);
                String ip = drop_edit.getText().toString();
                if (!ip.substring(ip.length() - 1, ip.length()).equals("/")) {
                    ip = ip + "/";
                }
                APIContext.HOST = ip;
                //把host写入存储
                UserMsg.saveHost(ip);
                sendData();
                break;

            default:
                break;
        }
    }

    private void sendData() {
        this.currentType = RequestType.LOGIN;
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        rv.functionPath = APIContext.Login;
        // rv.modulePath = APIContext.Login;
        rv.parser = new LoginParser();
        rv.Type = APIContext.GET;
        LoginUUID = UUID.randomUUID().toString();
        rv.uuId = LoginUUID;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userName", et_username.getText().toString());
        map.put("password", et_pwd.getText().toString());
        map.put("channel_id", UserMsg.getChannelId());
        map.put("dev", "az");
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (LoginUUID.equals(uuid)) {
            if (currentType == RequestType.LOGIN) {
                LoadingDialog.dimmissLoading();
                // if (object instanceof ReLoginBean) {
                // Toast.makeText(this, "用户未登录！", 1000).show();
                // return;
                // }
                // String ip=drop_edit.getText().toString();
                // if (!ip.substring(ip.length()-2, ip.length()-1).equals("/")) {
                // ip=ip+"/";
                // }
                if (APIContext.HOST != null && !APIContext.HOST.equals("")) {
                    boolean isSave = true;
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            if (((String) array.get(i)).equals(APIContext.HOST)) {
                                isSave = false;
                                break;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (isSave) {
                        array.put(APIContext.HOST);
                        UserMsg.saveIPJson(array.toString());
                        Log.e("LLL", "saveIPJson--->");
                    }

                }

                UserBean userBean = (UserBean) object;
                // userBean.name=et_username.getText().toString();
                // userBean.pwd=et_pwd.getText().toString();
                if (userBean.sysUser == null || userBean.sysUser.equals("")) {
                    UserMsg.saveRigester(userBean);
                    startActivity(new Intent(this, MenuActivity.class));
                    this.finish();
                } else {
                    Toast.makeText(this, userBean.sysUser, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

        @Override
        public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid){
            if (LoginUUID.equals(uuid)) {
                if (currentType == RequestType.LOGIN) {
                    Toast.makeText(this, "服务器无法连接", Toast.LENGTH_SHORT).show();
                    LoadingDialog.dimmissLoading();
                }
            }
        }

        class IpAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return array.length();
            }

            @Override
            public Object getItem(int position) {
                try {
                    return array.get(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = new TextView(LoginActivity.this);
                try {
                    tv.setText((String) array.get(position));
                    tv.setTextColor(0xFF000000);
                    tv.setTextSize(16);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return tv;
            }
        }
    }
