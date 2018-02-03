package com.ruisi.bi.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.bean.Message;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.fragment.MessageFragment;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.parser.MessageParser;
import com.ruisi.bi.app.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


/**
 * Created by berlython on 16/6/13.
 */
public class MessageDetailActivity extends Activity implements ServerCallbackInterface {

    private String uuid;
    private Integer msgId;

    private TextView tvTitle;
    private TextView tvTime;
    private ListView lvContent;

    private Message message;

    private JSONArray datas;
    private MessageContentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.activity_message_detail);

        tvTitle = (TextView) findViewById(R.id.tv_md_title);
        tvTime = (TextView) findViewById(R.id.tv_md_time);
        lvContent = (ListView) findViewById(R.id.lv_md_content);

        datas = new JSONArray();
        adapter = new MessageContentAdapter(this, datas);
        lvContent.setAdapter(adapter);
        message = (Message)getIntent().getSerializableExtra(MessageFragment.MESSAGE_OBJECT_KEY_SER);
        tvTitle.setText(message.title);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(message.crtdate);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String dateStr = sdf.format(date);
        tvTime.setText("推送时间 " + dateStr);

        msgId = message.id;
        sendRequest();
    }

    private void sendRequest() {
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        uuid = UUID.randomUUID().toString();
        rv.uuId = uuid;
        rv.isSaveToLocation = false;
        HashMap<String, String> map = new HashMap<String, String>();
        LoadingDialog.createLoadingDialog(this);
        rv.functionPath = APIContext.getMsg;
        rv.parser = new BaseParser() {
            @Override
            public <T> T parse(String jsonStr) throws JSONException {
                Log.d("MSGDETAIL", jsonStr);
                JSONArray jsonArray = null;
                if (!StringUtil.isBlank(jsonStr)){
                    jsonArray = new JSONArray(jsonStr);
                }
                return (T)jsonArray;
            }
        };
        rv.Type = APIContext.GET;
        map.put("token", UserMsg.getToken());
        map.put("id", msgId.toString());
        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (this.uuid.equals(uuid)) {
            LoadingDialog.dimmissLoading();
            if (object != null) {
                this.datas = (JSONArray) object;
                this.adapter.datas = this.datas;
                this.adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (this.uuid.equals(uuid)) {
            Toast.makeText(this, "获取推送信息失败", Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View v) {
        finish();
    }

    private class MessageContentAdapter extends BaseAdapter {

        private Context context;
        private JSONArray datas;

        public MessageContentAdapter(Context context, JSONArray datas) {
            this.context = context;
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return datas.getJSONArray(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message_detail, null);
                vh=new ViewHolder();
                vh.textView1 = (TextView)convertView.findViewById(R.id.tv_li_md_text1);
                vh.textView2 = (TextView)convertView.findViewById(R.id.tv_li_md_text2);
                vh.textView3 = (TextView)convertView.findViewById(R.id.tv_li_md_text3);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                vh.textView1.setBackgroundResource(R.drawable.li_message_detail_header_border);
                vh.textView2.setBackgroundResource(R.drawable.li_message_detail_header_border);
                vh.textView3.setBackgroundResource(R.drawable.li_message_detail_header_border);
            }

            JSONArray data = null;
            try {
                data = datas.getJSONArray(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (data != null) {
                try {
                    String tx = data.getString(0);
                    if (tx != null && !"null".equals(tx)) {
                        vh.textView1.setText(tx);
                    } else {
                        vh.textView1.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    String tx = data.getString(1);
                    if (tx != null && !"null".equals(tx)) {
                        vh.textView2.setText(tx);
                    } else {
                        vh.textView2.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    String tx = data.getString(2);
                    if (tx != null && !"null".equals(tx)) {
                        vh.textView3.setText(tx);
                    } else {
                        vh.textView3.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return convertView;
        }

        class ViewHolder {
            public TextView textView1;
            public TextView textView2;
            public TextView textView3;
        }
    }
}
