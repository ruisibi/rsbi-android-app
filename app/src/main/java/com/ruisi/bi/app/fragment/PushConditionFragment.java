package com.ruisi.bi.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.PushConditionActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.PushItem;

import org.jsoup.helper.StringUtil;
import org.w3c.dom.Text;

/**
 * Created by berlython on 16/6/26.
 */
public class PushConditionFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView tvTitle;
    private Spinner spCompare;
    private EditText etParam1;
    private EditText etParam2;

    private TextView tvAnd;
    private TextView tvParam2Unit;

    private ArrayAdapter<CharSequence> spinnerAdapter;

    private final String BETWEEN = "between";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_push_condition, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PushItem pushItem = ((PushConditionActivity)getActivity()).getPushItem();

        String unitStr = pushItem.kpiJson.get(0).unit;
        unitStr = StringUtil.isBlank(unitStr) || unitStr.equals("null")? "" : unitStr;

        tvTitle = (TextView) getView().findViewById(R.id.dialog_push_title);
        tvTitle.setText(pushItem.kpiJson.get(0).kpi_name);

        spCompare = (Spinner) getView().findViewById(R.id.dialog_push_compare);
        etParam1 = (EditText) getView().findViewById(R.id.dialog_push_param1);
        TextView tvParam1 = (TextView) getView().findViewById(R.id.dialog_push_param1_unit);
        tvParam1.setText(unitStr);
        if (pushItem.kpiJson.get(0).val1 != null) {
            etParam1.setText(pushItem.kpiJson.get(0).val1);
        }

        tvAnd = (TextView) getView().findViewById(R.id.dialog_push_and);
        tvAnd.setVisibility(TextView.INVISIBLE);

        etParam2 = (EditText) getView().findViewById(R.id.dialog_push_param2);
        etParam2.setVisibility(EditText.INVISIBLE);
        if (pushItem.kpiJson.get(0).val2 != null) {
            etParam2.setText(pushItem.kpiJson.get(0).val2);
        }

        tvParam2Unit = (TextView) getView().findViewById(R.id.dialog_push_param2_unit);
        tvParam2Unit.setVisibility(TextView.INVISIBLE);
        tvParam2Unit.setText(unitStr);

        spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.dialog_spinner_resource, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCompare.setAdapter(spinnerAdapter);
        spCompare.setOnItemSelectedListener(this);

        String opt = pushItem.kpiJson.get(0).opt;
        if (!StringUtil.isBlank(opt)) {
            int count = spinnerAdapter.getCount();
            for (int i = 0; i < count; i++) {
                CharSequence item = spinnerAdapter.getItem(i);
                if (opt.equals(item.toString())) {
                    spCompare.setSelection(i, false);
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CharSequence selected = spinnerAdapter.getItem(position);
        if (BETWEEN.equals(selected)) {
            tvAnd.setVisibility(TextView.VISIBLE);
            etParam2.setVisibility(View.VISIBLE);
            tvParam2Unit.setVisibility(View.VISIBLE);
        } else {
            tvAnd.setVisibility(TextView.INVISIBLE);
            etParam2.setVisibility(View.INVISIBLE);
            tvParam2Unit.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        tvAnd.setVisibility(TextView.INVISIBLE);
        etParam2.setVisibility(View.INVISIBLE);
        tvParam2Unit.setVisibility(View.INVISIBLE);
    }

    public boolean check() {
        boolean result = true;

        Object selectedItem = spCompare.getSelectedItem();
        if (selectedItem == null) {
            Toast.makeText(getActivity(), "请填写所有参数", Toast.LENGTH_SHORT).show();
            result = false;
        } else {

            CharSequence param1Text = etParam1.getText();
            if (param1Text == null || param1Text.length() == 0) {
                Toast.makeText(getActivity(), "请填写所有参数", Toast.LENGTH_SHORT).show();
                result = false;
            }
            if (BETWEEN.equals(selectedItem.toString())) {
                CharSequence param2Text = etParam2.getText();
                if (param1Text == null || param1Text.length() == 0) {
                    Toast.makeText(getActivity(), "请填写所有参数", Toast.LENGTH_SHORT).show();
                    result = false;
                }
            }
        }

        return result;
    }

    public void setData(PushItem pushItem) {
        Object selectedItem = spCompare.getSelectedItem();
        pushItem.kpiJson.get(0).opt = selectedItem.toString();
        pushItem.kpiJson.get(0).val1 = etParam1.getText().toString();
        pushItem.kpiJson.get(0).val2 = etParam2.getText().toString();
    }
}
