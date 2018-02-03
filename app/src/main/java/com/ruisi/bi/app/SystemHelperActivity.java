package com.ruisi.bi.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.net.MyCookieJar;

import org.jsoup.helper.StringUtil;

import java.util.List;

import okhttp3.Cookie;

/**
 * Created by hq on 2018/1/27.
 */

public class SystemHelperActivity extends Activity {

    private WebView webView;
    private ProgressBar progressbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.activity_system_helper);
        Intent intent = getIntent();
        webView = (WebView) findViewById(R.id.webView);

        String url = intent.getStringExtra("url");

        progressbar = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new WebView.LayoutParams(
                WebView.LayoutParams.MATCH_PARENT, 10, 0, 0));
        webView.addView(progressbar);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);// 支持js
        setting.setDefaultTextEncodingName("UTF-8");// 设置字符编码

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
        if (!StringUtil.isBlank(url)) {
            MyCookieJar.syncCookes(this, url);
            webView.loadUrl(url);
        } else {
            Toast.makeText(SystemHelperActivity.this, "获取链接失败", Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
