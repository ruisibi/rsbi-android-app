package com.ruisi.bi.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.ruisi.bi.app.PushConditionActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.adapter.PushSubjectAdapter;
import com.ruisi.bi.app.bean.PushItem;
import com.ruisi.bi.app.util.DateUtils;

import org.jsoup.helper.DataUtil;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by berlython on 16/6/26.
 */
public class PushTimeFragment extends Fragment implements View.OnClickListener, TimePickerView.OnTimeSelectListener, OptionsPickerView.OnOptionsSelectListener, View.OnFocusChangeListener {

    private EditText edDay;

    private EditText edHour;

    private EditText edMinute;

    private boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_push_time, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PushSubjectAdapter.SubjectType subjectType = ((PushConditionActivity) getActivity()).getSubjectType();
        PushItem pushItem = ((PushConditionActivity) getActivity()).getPushItem();
        if (subjectType == PushSubjectAdapter.SubjectType.MONTH) {
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.ll_month);
            linearLayout.setVisibility(View.VISIBLE);
            edDay = (EditText) getView().findViewById(R.id.push_time_day);
            edDay.setInputType(InputType.TYPE_NULL);
            edDay.setOnFocusChangeListener(this);
            edDay.setOnClickListener(this);
            if (pushItem!= null && pushItem.job != null && !StringUtil.isBlank(pushItem.job.day)) {
                edDay.setText(pushItem.job.day);
            }
            TextView tvDayTitle = (TextView) getView().findViewById(R.id.push_time_day_title);
            tvDayTitle.setText("          ");

            if (pushItem!= null && pushItem.job != null && !StringUtil.isBlank(pushItem.job.day)) {
                edDay.setText(pushItem.job.day);
            }
        }

        edHour = (EditText) getView().findViewById(R.id.push_time_hour);
        edHour.setInputType(InputType.TYPE_NULL);
        edHour.setOnFocusChangeListener(this);
        edHour.setOnClickListener(this);

        edMinute = (EditText) getView().findViewById(R.id.push_time_minute);
        edMinute.setInputType(InputType.TYPE_NULL);
        edMinute.setOnFocusChangeListener(this);
        edMinute.setOnClickListener(this);

        if (pushItem!= null && pushItem.job != null && !StringUtil.isBlank(pushItem.job.hour)) {
            edHour.setText(pushItem.job.hour);
        }
        if (pushItem!= null && pushItem.job != null && !StringUtil.isBlank(pushItem.job.hour)) {
            edMinute.setText(pushItem.job.minute);
        };



    }

    @Override
    public void onClick(View v) {
        showPickers(v);
    }

    private void showPickers(View v){
        if (v == edHour || v == edMinute) {
            TimePickerView timePicker = new TimePickerView(getActivity(), TimePickerView.Type.HOURS_MINS);
            timePicker.setCyclic(false);
            timePicker.setCancelable(false);
            timePicker.setOnTimeSelectListener(this);
            timePicker.setTitle("请选择推送时间");
            timePicker.show();
            String hour = edHour.getText().toString();
            String minute = edMinute.getText().toString();

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
            if (!StringUtil.isBlank(hour)) {
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
                timePicker.setTime(calendar.getTime());
            } else {
                timePicker.setTime(DateUtils.changeTimeZone(calendar.getTime(), TimeZone.getTimeZone("Asia/Shanghai"), TimeZone.getTimeZone("GMT")));
            }



        } else if (v == edDay){
            OptionsPickerView<String> optionsPickerView = new OptionsPickerView<>(getActivity());

            ArrayList<String> list = new ArrayList<>();
            for (int i = 1; i < 32; i++) {
                list.add("" + i);
            }

            optionsPickerView.setTitle("请选择推送日期");
            optionsPickerView.setPicker(list);
            optionsPickerView.setOnoptionsSelectListener(this);
            optionsPickerView.setCancelable(false);
            optionsPickerView.show();
            String day = edDay.getText().toString();
            if (!StringUtil.isBlank(day)) {
                int option = Integer.parseInt(day) - 1;
                optionsPickerView.setSelectOptions(option);
            } else {
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtils.changeTimeZone(date, TimeZone.getTimeZone("Asia/Shanghai"), TimeZone.getTimeZone("GMT")));
                Integer iday = calendar.get(Calendar.DAY_OF_MONTH);
                optionsPickerView.setSelectOptions(iday);
            }
        }
    }

    @Override
    public void onTimeSelect(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        edMinute.setText("" + minute);
        edHour.setText("" + hour);
    }


    @Override
    public void onOptionsSelect(int options1, int option2, int options3) {
        edDay.setText("" + (options1 + 1));
    }

    public boolean check() {
        if (edDay != null) {
            if (StringUtil.isBlank(edDay.getText().toString())) {
                return false;
            }
        }
        if (StringUtil.isBlank(edHour.getText().toString()) || StringUtil.isBlank(edMinute.getText().toString())){
            return false;
        }

        return true;
    }

    public void setTime(PushItem pushItem) {
        PushItem.Job job = pushItem.new Job();
        if (edDay != null) {
            job.day = edDay.getText().toString();
        }
        job.hour = edHour.getText().toString();
        job.minute = edMinute.getText().toString();

        pushItem.job = job;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (isFirst) {
                isFirst = false;
            } else {
                showPickers(v);
            }

        }
    }
}
