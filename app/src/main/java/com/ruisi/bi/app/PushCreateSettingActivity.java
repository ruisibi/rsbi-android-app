package com.ruisi.bi.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.adapter.PushSubjectAdapter;
import com.ruisi.bi.app.bean.PushItem;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.ZhibiaoBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.BaseParser;
import com.ruisi.bi.app.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;



public class PushCreateSettingActivity extends Activity implements AdapterView.OnItemClickListener, ServerCallbackInterface {

    private final String TAG = "P_C_S_Activity";

    private ListView lvPushCreateSetting;
    private List<ContentItem> itemList;
    private List<WeiduBean> dimList;
    private List<ZhibiaoBean> kpiList;
    private PushCreateSettingAdapter pushCreateSettingAdapter;
    private Integer subjectId;
    private PushSubjectAdapter.SubjectType subjectType;
    private RequestType currentRequestType;
    private String uuid;

    private ImageView imgBack;
    private ImageButton btnSave;

    private String saveName;

    private PushItem pushItem;
    private Integer pushId;

    private final String BETWEEN = "between";

    private enum RequestType {
        PUSH_SAVE,
        PUSH_UPDATE;
    }

    private enum PopupType{
        DIM, KPI, CONDITION, TIME;
    }

    public PushItem getPushItem() {
        return pushItem;
    }

    public void setPushItem(PushItem pushItem) {
        this.pushItem = pushItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.setStatuColor(this);
        setContentView(R.layout.activity_push_create_setting);

        Intent intent = getIntent();
        this.subjectId = intent.getIntExtra("subjectId", -1);
        this.subjectType = intent.getIntExtra("subjectType", 0) == 1 ? PushSubjectAdapter.SubjectType.DAY : PushSubjectAdapter.SubjectType.MONTH;
        pushItem = (PushItem)intent.getSerializableExtra(PushCreateActivity.PUSH_ITEM_SER_KEY);
        pushId = intent.getIntExtra("pushId", -1);

        itemList = new ArrayList<>();
        if (pushItem == null) {
            pushItem = new PushItem();
            pushItem.tid = subjectId;
            pushItem.pushType = subjectType.equals(PushSubjectAdapter.SubjectType.DAY) ? "day" : "month";
        }
        setItemList(pushItem);


        imgBack = (ImageView) findViewById(R.id.back);
        btnSave = (ImageButton) findViewById(R.id.btn_save);

        lvPushCreateSetting = (ListView) findViewById(R.id.lv_push_create_setting);
        pushCreateSettingAdapter = new PushCreateSettingAdapter(this, itemList);
        lvPushCreateSetting.setAdapter(pushCreateSettingAdapter);

        lvPushCreateSetting.setOnItemClickListener(this);
    }

    public void setItemList(PushItem pushItem) {
        itemList.clear();
        if (pushItem != null) {
            String dimName = "空";
            if (pushItem.dim != null && !StringUtil.isBlank(pushItem.dim.dimdesc)) {
                dimName = pushItem.dim.dimdesc;
            }
            itemList.add(new ContentItem("推送维度", dimName));

            PushItem.KpiJsonItem kpiJsonItem = null;
            String kpiName = "空";
            if (pushItem.kpiJson != null && pushItem.kpiJson.size() > 0) {
                kpiName = pushItem.kpiJson.get(0).kpi_name;
                kpiJsonItem = pushItem.kpiJson.get(0);
            }
            itemList.add(new ContentItem("推送度量", kpiName));

            String condition = "空";
            if (kpiJsonItem != null) {
                String opt = kpiJsonItem.opt;
                if (opt != null && "between".equals(opt)) {
                    condition = kpiJsonItem.kpi_name + " " + opt + " " + kpiJsonItem.val1 + kpiJsonItem.unit+ " and " + kpiJsonItem.val2 + kpiJsonItem.unit;
                } else if (opt != null && !"between".equals(opt)){
                    condition = kpiJsonItem.kpi_name + " " + opt + " " + kpiJsonItem.val1 + kpiJsonItem.unit;
                }
            }
            itemList.add(new ContentItem("筛选条件", condition));

            String time = "空";
            if (pushItem.job != null) {
                String day = pushItem.job.day;
                if (StringUtil.isBlank(day)) {
                    time = "每天 " + pushItem.job.hour + "点 " + pushItem.job.minute + "分";
                } else {
                    time = "每月 " + day + "号 " + pushItem.job.hour + "点 " + pushItem.job.minute + "分";
                }
            }

            itemList.add(new ContentItem("推送时间", time));
        } else {
            pushItem = new PushItem();
            pushItem.tid = subjectId;
            pushItem.pushType = subjectType == PushSubjectAdapter.SubjectType.MONTH ? "month":"day";
            itemList.add(new ContentItem("推送维度", "空"));
            itemList.add(new ContentItem("推送度量", "空"));
            itemList.add(new ContentItem("筛选条件", "空"));
            itemList.add(new ContentItem("推送时间", "空"));
        }
        if (pushCreateSettingAdapter != null) {
            pushCreateSettingAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        createPopup(position);
    }

    private void createPopup(int position) {
        Intent intent = new Intent(PushCreateSettingActivity.this, PushConditionActivity.class);

        intent.putExtra("subjectType", subjectType == PushSubjectAdapter.SubjectType.DAY ? 0 : 1);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PushCreateActivity.PUSH_ITEM_SER_KEY, pushItem);
        intent.putExtras(bundle);
        switch(position) {
            case 0:
                intent.putExtra("type", 0);
                startActivityForResult(intent, 0);
                break;
            case 1:
                intent.putExtra("type", 1);
                startActivityForResult(intent, 1);
                break;
            case 2: {
                if (pushItem.kpiJson == null || pushItem.kpiJson.size() == 0) {
                    Toast.makeText(PushCreateSettingActivity.this, "请先选择度量数据", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("type", 2);
                    startActivityForResult(intent, 2);
                }

                break;
            }
            case 3:
                intent.putExtra("type", 3);
                startActivityForResult(intent, 3);
                break;
        }

    }


    private void sendRequest(RequestType requestType) {
        ServerEngine serverEngine = new ServerEngine(this);
        RequestVo rv = new RequestVo();
        rv.context = this;
        this.uuid = UUID.randomUUID().toString();
        rv.uuId = this.uuid;
        HashMap<String, String> map = new HashMap<String, String>();
        LoadingDialog.createLoadingDialog(this);

        String jsonStr = null;
        try {
            jsonStr = createJson(pushItem);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(PushCreateSettingActivity.this, "保存推送失败", Toast.LENGTH_LONG).show();
            return;
        }

        rv.Type = APIContext.POST;
        rv.isSaveToLocation = false;
        rv.parser = new BaseParser() {
                @Override
                public <T> T parse(String jsonStr) throws JSONException {
                    return (T)jsonStr;
                }
            };

        if (requestType == RequestType.PUSH_SAVE) {
            map.put("pageName", saveName);
            this.currentRequestType = RequestType.PUSH_SAVE;
            rv.functionPath = APIContext.savePush;

        } else if (requestType == RequestType.PUSH_UPDATE || pushId != -1) {
            //TODO
            map.put("id", pushId.toString());
            this.currentRequestType = RequestType.PUSH_UPDATE;
            rv.functionPath = APIContext.updatePush;
        }
        map.put("pageInfo", jsonStr);
        map.put("token", UserMsg.getToken());

        rv.requestDataMap = map;
        serverEngine.addTaskWithConnection(rv);
    }

    private String createJson(PushItem iPushItem) throws JSONException {

        JSONObject obj = new JSONObject();

        obj.put("tid", iPushItem.tid);
        obj.put("pushType", iPushItem.pushType);

        JSONObject dim = null;
        if (iPushItem.dim != null) {
            dim = new JSONObject();
            dim.put("type", iPushItem.dim.type);
            dim.put("id", iPushItem.dim.id);
            dim.put("colname", iPushItem.dim.colname);
            dim.put("alias", iPushItem.dim.alias);
            dim.put("tname", iPushItem.dim.tname);
            dim.put("tableColKey", iPushItem.dim.tableColKey);
            dim.put("tableColName", iPushItem.dim.tableColName);
            dim.put("dimdesc", iPushItem.dim.dimdesc);
            dim.put("dimord", iPushItem.dim.dimord);
            dim.put("grouptype", iPushItem.dim.grouptype);
            dim.put("iscas", iPushItem.dim.iscas);
            dim.put("valType", iPushItem.dim.valType);
            dim.put("tid", iPushItem.dim.tid);
            dim.put("dateformat", iPushItem.dim.dateformat);
        }
        obj.put("dim", dim);

        JSONArray kpiJson = null;
        if (iPushItem.kpiJson != null && iPushItem.kpiJson.size() > 0) {
            kpiJson = new JSONArray();
            JSONObject kpiObj = new JSONObject();
            PushItem.KpiJsonItem item = iPushItem.kpiJson.get(0);
            kpiObj.put("kpi_id", item.kpi_id);
            kpiObj.put("kpi_name", item.kpi_name);
            kpiObj.put("col_name", item.col_name);
            kpiObj.put("aggre", item.aggre);
            kpiObj.put("fmt", item.fmt);
            kpiObj.put("alias", item.alias);
            kpiObj.put("tid", item.tid);
            kpiObj.put("unit", item.unit);
            kpiObj.put("rate", item.rate);
            kpiObj.put("opt", item.opt);
            kpiObj.put("val1", item.val1);
            kpiObj.put("val2", item.val2);
            kpiJson.put(kpiObj);
        }

        obj.put("kpiJson", kpiJson);

        JSONObject job = null;
        if (iPushItem.job != null) {
            job = new JSONObject();
            job.put("day", iPushItem.job.day);
            job.put("hour", iPushItem.job.hour);
            job.put("minute", iPushItem.job.minute);
        }
        obj.put("job", job);


        return obj.toString().replaceAll("null", "");
    }

    public void onClick(View v){
        if (v == imgBack) {
            finish();
        } else if (v == btnSave) {
            if (pushItem.kpiJson != null && pushItem.kpiJson.size() != 0  && pushItem.job != null && !StringUtil.isBlank(pushItem.job.hour) && !StringUtil.isBlank(pushItem.job.minute)){
                if (pushItem.dim != null && pushItem.dim.id == null) {
                    pushItem.dim = null;
                }

                if (pushId != null && pushId.compareTo(-1) != 0) {
                    sendRequest(RequestType.PUSH_UPDATE);
                } else {

                    final Dialog dialog = new Dialog(this, R.style.customProgressDialog);

                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(R.layout.layout_exit_view);
                    final EditText et = (EditText) dialog.findViewById(R.id.tv_message);
                    dialog.findViewById(R.id.btn_exit_ok).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveName = et.getText().toString();

                                    sendRequest(RequestType.PUSH_SAVE);
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
                    dialog.show();
                }
            } else {
                if (pushItem.kpiJson == null || pushItem.kpiJson.size() == 0) {
                        Toast.makeText(PushCreateSettingActivity.this, "请选择推送度量", Toast.LENGTH_SHORT).show();
                }
                if (pushItem.job == null) {
                    Toast.makeText(PushCreateSettingActivity.this, "请选择推送时间", Toast.LENGTH_SHORT).show();
                }

            }

        }

    }

    @Override
    public <T> void succeedReceiveData(T object, String uuid) {
        if (this.uuid.equals(uuid)){
            if (RequestType.PUSH_SAVE == currentRequestType) {
                LoadingDialog.dimmissLoading();
                Toast.makeText(PushCreateSettingActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PushCreateSettingActivity.this, PushSettingListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (RequestType.PUSH_UPDATE == currentRequestType) {
                LoadingDialog.dimmissLoading();
                Toast.makeText(PushCreateSettingActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PushCreateSettingActivity.this, PushSettingListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    @Override
    public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
        if (this.uuid.equals(uuid)){
            if (RequestType.PUSH_SAVE == currentRequestType) {
                LoadingDialog.dimmissLoading();
                Toast.makeText(this, "保存推送设置失败: " + errorMessage.getErrormessage(), Toast.LENGTH_SHORT).show();
            } else if (RequestType.PUSH_UPDATE == currentRequestType) {
                LoadingDialog.dimmissLoading();
                Toast.makeText(this, "更新推送设置失败: " + errorMessage.getErrormessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PushItem pushItem = (PushItem) data.getSerializableExtra(PushCreateActivity.PUSH_ITEM_SER_KEY);
            if (requestCode == 0) {
                String text = data.getStringExtra("text");
                itemList.get(0).content = pushItem.dim == null ? "空" : text;
            } else if (requestCode == 1) {
                itemList.get(1).content = pushItem.kpiJson.size() == 0 ? "空" : pushItem.kpiJson.get(0).kpi_name;
            } else if (requestCode == 2) {

                PushItem.KpiJsonItem item = pushItem.kpiJson.get(0);
                if (item.opt != null && item.val1 != null) {
                    itemList.get(2).content = item.kpi_name + " " + item.opt + " " + item.val1 + item.unit +
                            (StringUtil.isBlank(item.val2) ? "" : (" and" + item.val2 + " " + item.unit));
                } else {
                    itemList.get(2).content = "空";
                }

            } else if (requestCode == 3) {
                PushItem.Job job = pushItem.job;
                if (job == null || StringUtil.isBlank(job.hour)) {
                    itemList.get(3).content = "空";
                } else {
                    if (PushCreateSettingActivity.this.subjectType == PushSubjectAdapter.SubjectType.DAY) {

                        itemList.get(3).content = "每天 " + job.hour + "点 " + job.minute + "分";
                    } else {
                        itemList.get(3).content = "每月 " + job.day + "号 " + job.hour + "点 " + job.minute + "分";
                    }

                }
            }

            this.pushItem = pushItem;


            pushCreateSettingAdapter.notifyDataSetChanged();
        }
    }
}

class PushCreateSettingAdapter extends BaseAdapter {

    private Context context;
    private List<ContentItem> data;

    public PushCreateSettingAdapter(Context context, List<ContentItem> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_push_create_setting, parent, false);
            vh = new ViewHolder();
            vh.titleView = (TextView) convertView
                    .findViewById(R.id.tv_push_create_setting_title);
            vh.contentView = (TextView) convertView.findViewById(R.id.tv_push_create_setting_content);
            vh.imgCross = (ImageView) convertView.findViewById(R.id.img_cross);
            final ViewHolder finalVh = vh;
            final Context finalContext = context;
            vh.imgCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalVh.contentView.setText("空");
                    PushCreateSettingActivity activity = (PushCreateSettingActivity) finalContext;
                    PushItem pushItem = activity.getPushItem();
                    if (position == 0) {
                        pushItem.dim = null;
                    } else if (position == 1) {
                        pushItem.kpiJson = null;
                    } else if(position == 2) {
                        pushItem.kpiJson.get(0).opt = null;
                        pushItem.kpiJson.get(0).val1 = null;
                        pushItem.kpiJson.get(0).val2 = null;
                    } else {
                        pushItem.job = null;
                    }
                    activity.setItemList(pushItem);

                }
            });
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        ContentItem ci = data.get(position);
        vh.titleView.setText(ci.title);
        vh.contentView.setText(ci.content);
        if (!"空".equals(ci.content)) {
            vh.imgCross.setVisibility(View.VISIBLE);
        } else {
            vh.imgCross.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder{
        TextView titleView;
        TextView contentView;
        ImageView imgCross;
    }

}

class ContentItem {
    public String title;
    public String content;

    public ContentItem(){};
    public ContentItem(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
