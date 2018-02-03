package com.ruisi.bi.app.common;

import android.content.Context;
import android.content.Intent;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.ruisi.bi.app.MenuActivity;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by berlython on 16/6/13.
 */
public class PushReceiver extends PushMessageReceiver {
    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        if (errorCode == 0) {
            UserMsg.saveChannelId(channelId);
        }

    }

    @Override
    public void onUnbind(Context context, int i, String s) {
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {
    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {
    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {
    }

    @Override
    public void onMessage(Context context, String s, String s1) {
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        Intent intent=new Intent(context, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("pushFlag", 1);
        context.startActivity(intent);
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
        ;
        ShortcutBadger.applyCount(context.getApplicationContext(), 1);
    }
}
