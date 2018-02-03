package com.ruisi.bi.app;

import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.MyCookieJar;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaoBiaoItemParser;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.parser.CollectionAddParser;
import com.ruisi.bi.app.view.LoadingDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class Webviewactivity extends Activity implements View.OnClickListener, ServerCallbackInterface{
    private final String TAG = "Webviewactivity";

    private WebView webView;
    private ProgressBar progressbar;
    private ImageView ivCollection;
    private String name;
    private String webURL;
    private String rid;
    private Integer iscollect;

    private String uuid;
    private RequestType currentRequestType;

    private final int NO_COLLECTED = 0;
    private final int COLLECTED = 1;

    private enum RequestType{
        COLLECT,CANCEL_COLLECT;
    }


    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.webview_activity_layout);
        Intent intent = getIntent();
        name = intent.getStringExtra("title");
        webURL = intent.getStringExtra("url");
        rid = intent.getStringExtra("rid");
        iscollect = intent.getIntExtra("iscollect", 0);

        ((TextView) findViewById(R.id.title)).setText(name);

        ivCollection = (ImageView) findViewById(R.id.btn_collection);
        if (iscollect == COLLECTED) {
            ivCollection.setImageDrawable(getResources().getDrawable(R.drawable.btn_collection_cancel));
        }
        ivCollection.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webView);
        progressbar = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new WebView.LayoutParams(
                WebView.LayoutParams.MATCH_PARENT, 10, 0, 0));
        webView.addView(progressbar);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);// 支持js
        setting.setDefaultTextEncodingName("UTF-8");// 设置字符编码
        webView.addJavascriptInterface(this, "AndroidFunction");
        setting.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                } else {
                    if (progressbar.getVisibility() == View.GONE)
                        progressbar.setVisibility(View.VISIBLE);
                    progressbar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
        MyCookieJar.syncCookes(this, webURL);
        webView.loadUrl(webURL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_collection:
                LoadingDialog.createLoadingDialog(this);
                ivCollection.setEnabled(false);
                if (iscollect == NO_COLLECTED) {
                    sendRequest(RequestType.COLLECT);
                    currentRequestType = RequestType.COLLECT;
                } else if (iscollect == COLLECTED){
                    sendRequest(RequestType.CANCEL_COLLECT);
                    currentRequestType = RequestType.CANCEL_COLLECT;
                }
                break;
        }
    }

    private void sendRequest(RequestType type){
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        if (type == RequestType.COLLECT) {
            rv.context = this;
            rv.functionPath = APIContext.addCollection;
            rv.parser = new CollectionAddParser();
            rv.Type = APIContext.POST;
            uuid = UUID.randomUUID().toString();
            rv.uuId = uuid;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("token", UserMsg.getToken());
            map.put("rid", "" + rid);
            rv.requestDataMap = map;
        } else if (type == RequestType.CANCEL_COLLECT){
            rv.context = this;
            rv.functionPath = APIContext.delCollection;
            rv.Type = APIContext.POST;
            rv.parser = new BaseParser() {
                @Override
                public <T> T parse(String jsonStr) throws JSONException {
                    return (T)jsonStr;
                }
            };
            uuid = UUID.randomUUID().toString();
            rv.uuId = uuid;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("token", UserMsg.getToken());
            map.put("rid", "" + rid);
            rv.requestDataMap = map;
        }
        serverEngine.addTaskWithConnection(rv);
    }


    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        LoadingDialog.dimmissLoading();
        if (uuid.equals(this.uuid)){
            if (currentRequestType == RequestType.COLLECT) {
                JSONObject obj = (JSONObject) object;
                try {
                    boolean result = obj.getBoolean("result");
                    if (result) {
                        iscollect = COLLECTED;
                        ivCollection.setImageDrawable(getResources().getDrawable(R.drawable.btn_collection_cancel));
                        Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                }
            }else if (currentRequestType == RequestType.CANCEL_COLLECT){
                try {
                    JSONObject obj = new JSONObject((String) object);
                    int result = obj.getInt("result");
                    if (result == 1) {
                        Toast.makeText(this, "取消成功", Toast.LENGTH_SHORT).show();
                        iscollect = NO_COLLECTED;
                        ivCollection.setImageDrawable(getResources().getDrawable(R.drawable.btn_collection));
                    } else {
                        Toast.makeText(this, "取消失败", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    Toast.makeText(this, "取消失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
        ivCollection.setEnabled(true);
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        LoadingDialog.dimmissLoading();
        ivCollection.setEnabled(true);
        if (uuid.equals(this.uuid)) {
            if (currentRequestType == RequestType.COLLECT) {
                Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
            } else if (currentRequestType == RequestType.CANCEL_COLLECT) {
                Toast.makeText(this, "取消失败", Toast.LENGTH_SHORT).show();
            }

            //Log.d(TAG, errorMessage.getErrormessage());
        }
    }
}
