package com.ruisi.bi.app.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.TuActivity;
import com.ruisi.bi.app.adapter.OptionAdapter;
import com.ruisi.bi.app.bean.RequestVo;
import com.ruisi.bi.app.bean.WeiduBean;
import com.ruisi.bi.app.bean.WeiduOptionBean;
import com.ruisi.bi.app.common.APIContext;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.common.UserMsg;
import com.ruisi.bi.app.net.ServerCallbackInterface;
import com.ruisi.bi.app.net.ServerEngine;
import com.ruisi.bi.app.net.ServerErrorMessage;
import com.ruisi.bi.app.parser.TuLeidaParser;
import com.ruisi.bi.app.parser.TuZhuxingParser;
import com.ruisi.bi.app.util.OffLineFenxi;
import com.ruisi.bi.app.util.TaskManager;
import com.ruisi.bi.app.util.OffLineFenxi.OffLineCallBack;
import com.ruisi.bi.app.view.LoadingDialog;
import com.ruisi.bi.app.view.MyValueFormatter;
import com.ruisi.bi.app.view.ShaixuanPopwindow;

public class TuTiaoxingFragment extends Fragment implements
		ServerCallbackInterface, OnChartValueSelectedListener,
		OnItemSelectedListener, OnClickListener ,OffLineCallBack{
	private HorizontalBarChart mChart;;
	private String requestJson;
	private Typeface tf;
	private String zhuxingUUID;

	private Spinner spinner01, spinner02;
	private OptionAdapter oAdapter01, oAdapter02;
	private ArrayList<WeiduOptionBean> options01, options02;
	private TextView option_name;
	private String shaixuan01, shaixuan02;
	private int index01, index02;
	private String DateMin, DateMax;
	private String StartDay, EndDay;
	private String zhibiao;

	private Button check;
	private String[] sile01, sile02;
	private ArrayAdapter<String> adapterSpinner01, adapterSpinner02;

	@SuppressLint("ValidFragment")
	public TuTiaoxingFragment(){}
	@SuppressLint("ValidFragment")
	public TuTiaoxingFragment(String requestJson, String zhibiao) {
		this.requestJson = requestJson;
		this.zhibiao = zhibiao;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tu_tiaoxing_fragment, null);
		mChart = (HorizontalBarChart) v.findViewById(R.id.chart1);
		spinner01 = (Spinner) v.findViewById(R.id.zhuxing_spinner01);
		spinner02 = (Spinner) v.findViewById(R.id.zhuxing_spinner02);
		 v.findViewById(R.id.date_start_bt).setOnClickListener(this);
	        v.findViewById(R.id.date_end_bt).setOnClickListener(this);
		spinner02.setOnItemSelectedListener(this);
		spinner01.setOnItemSelectedListener(this);
		options01 = new ArrayList<>();
		options02 = new ArrayList<>();
//		oAdapter01 = new OptionAdapter(getActivity(), options01);
//		oAdapter02 = new OptionAdapter(getActivity(), options02);
//		spinner01.setAdapter(oAdapter01);
//		spinner02.setAdapter(oAdapter02);
		((TextView)v.findViewById(R.id.tv_zhibiao)).setText(zhibiao);
		option_name = (TextView) v.findViewById(R.id.zhuxing_option_name);
		check = (Button) v.findViewById(R.id.zhuxing_check);
		check.setOnClickListener(this);

		initLineChart();
		if (TuActivity.strJsons!=null) {
			sendRequest();
		}else {
			String data=UserMsg.getTuZhuxing(TuActivity.tags);
			if (data!=null) {
				TaskManager.getInstance().addTask(new OffLineFenxi( new TuZhuxingParser(), data, this));
			}
		}
		return v;
	}

	private void sendRequest() {
		LoadingDialog.createLoadingDialog(getActivity());
		ServerEngine serverEngine = new ServerEngine(this);
		RequestVo rv = new RequestVo();
		rv.context = this.getActivity();
		rv.functionPath = APIContext.tu;
		rv.parser = new TuZhuxingParser();
		rv.Type = APIContext.POST;
		zhuxingUUID = UUID.randomUUID().toString();
		rv.uuId = zhuxingUUID;
		rv.tag01=TuActivity.tags;
		rv.isSaveToLocation = true;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("pageInfo", requestJson);
		 map.put("token", UserMsg.getToken());
		rv.requestDataMap = map;
		serverEngine.addTaskWithConnection(rv);
	}

	private void initLineChart() {
		mChart.setOnChartValueSelectedListener(this);
		// mChart.setHighlightEnabled(false);

		mChart.setDrawBarShadow(false);

		mChart.setDrawValueAboveBar(true);

		mChart.setDescription("");

		// if more than 60 entries are displayed in the chart, no values will be
		// drawn
		mChart.setMaxVisibleValueCount(60);

		// scaling can now only be done on x- and y-axis separately
		mChart.setPinchZoom(false);

		// draw shadows for each bar that show the maximum value
		// mChart.setDrawBarShadow(true);

		// mChart.setDrawXLabels(false);

		mChart.setDrawGridBackground(false);

		// mChart.setDrawYLabels(false);

		tf = Typeface.createFromAsset(getActivity().getAssets(),
				"OpenSans-Regular.ttf");

		XAxis xl = mChart.getXAxis();
		xl.setPosition(XAxisPosition.BOTTOM);
		xl.setTypeface(tf);
		xl.setDrawAxisLine(true);
		xl.setDrawGridLines(true);
		xl.setGridLineWidth(0.3f);

		mChart.getAxisLeft().setEnabled(false);
		// YAxis yl = mChart.getAxisLeft();
		// yl.setTypeface(tf);
		// yl.setDrawAxisLine(true);
		// yl.setDrawGridLines(true);
		// yl.setGridLineWidth(0.3f);
		// yl.setInverted(true);

		YAxis yr = mChart.getAxisRight();
		yr.setTypeface(tf);
		yr.setDrawAxisLine(true);
		yr.setDrawGridLines(false);
		// yr.setInverted(true);
		mChart.setBackgroundColor(Color.WHITE);
	}

	@Override
	public void onNothingSelected() {

	}

	@Override
	public void onValueSelected(Entry arg0, int arg1, Highlight arg2) {

	}

	@Override
	public <T> void succeedReceiveData(T object, String uuid) {
		if (uuid.equals(zhuxingUUID)) {
			final ArrayList<Object> dataR = (ArrayList<Object>) object;
			LoadingDialog.dimmissLoading();
			BarData data = (BarData) dataR.get(1);
			data.setValueTextSize(10f);
			data.setValueTypeface(tf);
			mChart.setData(data);
			mChart.animateY(1000);
			Legend l = mChart.getLegend();
			l.setPosition(LegendPosition.RIGHT_OF_CHART);
			l.setFormSize(8f);
			l.setXEntrySpace(4f);
			if (((ArrayList<WeiduBean>) dataR.get(0)).size() == 1) {
				if (adapterSpinner01!=null) {
					return;
				}
				getActivity().findViewById(R.id.select_layout).setVisibility(
						View.VISIBLE);
				getActivity().findViewById(R.id.zhuxing_check).setVisibility(
						View.VISIBLE);
				getActivity().findViewById(R.id.zhuxing_option_name)
						.setVisibility(View.VISIBLE);
				sile01 = new String[((ArrayList<WeiduBean>) dataR.get(0)).get(0).options.size()];
				for (int i = 0; i < ((ArrayList<WeiduBean>) dataR.get(0)).get(0).options.size(); i++) {
					sile01[i] = ((ArrayList<WeiduBean>) dataR.get(0)).get(0).options.get(i).text;
				}
				adapterSpinner01 = new ArrayAdapter<String>(getActivity(),
						R.layout.myspinner, sile01){
							@Override
							public View getDropDownView(int position, View convertView,
									ViewGroup parent) {
								View view = getActivity().getLayoutInflater()
										.inflate(R.layout.spinner_item_layout, null);
								TextView label = (TextView) view
										.findViewById(R.id.spinner_item_label);
								// ImageView check = (ImageView) view
								// .findViewById(R.id.spinner_item_checked_image);
								label.setText(sile01[position]);
								CheckBox check = (CheckBox) view
										.findViewById(R.id.spinner_item_checked_image);
								if (shaixuan01.equals(((ArrayList<WeiduBean>) dataR.get(0)).get(0).options.get(position).value)) {
									check.setChecked(true);
								}

								return view;
							}
						};
						spinner01.setAdapter(adapterSpinner01);
				options01.clear();
				spinner01.setVisibility(View.VISIBLE);
				options01
						.addAll(((ArrayList<WeiduBean>) dataR.get(0)).get(0).options);
//				oAdapter01.notifyDataSetChanged();
				spinner01.setSelection(index01);
				option_name.setText(((ArrayList<WeiduBean>) dataR.get(0))
						.get(0).name);
			}
			if (((ArrayList<WeiduBean>) dataR.get(0)).size() >= 2) {
				getActivity().findViewById(R.id.select_layout).setVisibility(
						View.VISIBLE);
				if (((ArrayList<WeiduBean>) dataR.get(0)).get(0).type
						.equals("dateSelect")) {
					getActivity().findViewById(R.id.date_layout).setVisibility(
							View.VISIBLE);

					if (((ArrayList<WeiduBean>) dataR.get(0)).get(0).max == null) {
						DateMin = ((ArrayList<WeiduBean>) dataR.get(0)).get(0).min;
					} else {
						DateMin = ((ArrayList<WeiduBean>) dataR.get(0)).get(0).min;
					}
					if (((ArrayList<WeiduBean>) dataR.get(0)).get(1).max == null) {
						DateMax = ((ArrayList<WeiduBean>) dataR.get(0)).get(1).min;
					} else {
						DateMax = ((ArrayList<WeiduBean>) dataR.get(0)).get(1).max;
					}
					return;
				}
				getActivity().findViewById(R.id.zhuxing_check).setVisibility(
						View.VISIBLE);
				getActivity().findViewById(R.id.zhuxing_option_name)
						.setVisibility(View.VISIBLE);
				if (adapterSpinner02!=null) {
					return;
				}
				options01.clear();
				options02.clear();
				spinner01.setVisibility(View.VISIBLE);
				spinner02.setVisibility(View.VISIBLE);
				option_name.setText(((ArrayList<WeiduBean>) dataR.get(0))
						.get(0).name);
				options01
						.addAll(((ArrayList<WeiduBean>) dataR.get(0)).get(0).options);
				options02
						.addAll(((ArrayList<WeiduBean>) dataR.get(0)).get(1).options);
//				oAdapter01.notifyDataSetChanged();
//				oAdapter02.notifyDataSetChanged();
				sile01 = new String[((ArrayList<WeiduBean>) dataR.get(0)).get(0).options.size()];
				for (int i = 0; i < ((ArrayList<WeiduBean>) dataR.get(0)).get(0).options.size(); i++) {
					sile01[i] = ((ArrayList<WeiduBean>) dataR.get(0)).get(0).options.get(i).text;
				}
				sile02 = new String[((ArrayList<WeiduBean>) dataR.get(0)).get(1).options.size()];
				for (int i = 0; i < ((ArrayList<WeiduBean>) dataR.get(0)).get(1).options.size(); i++) {
					sile02[i] = ((ArrayList<WeiduBean>) dataR.get(0)).get(1).options.get(i).text;
				}
				adapterSpinner01 = new ArrayAdapter<String>(getActivity(),
						R.layout.myspinner, sile01) {
					@Override
					public View getDropDownView(int position, View convertView,
							ViewGroup parent) {
						View view = getActivity().getLayoutInflater()
								.inflate(R.layout.spinner_item_layout, null);
						TextView label = (TextView) view
								.findViewById(R.id.spinner_item_label);
						// ImageView check = (ImageView) view
						// .findViewById(R.id.spinner_item_checked_image);
						label.setText(sile01[position]);
						CheckBox check = (CheckBox) view
								.findViewById(R.id.spinner_item_checked_image);
						label.setText(sile01[position]);
						if (shaixuan01.equals(((ArrayList<WeiduBean>) dataR.get(0)).get(0).options.get(position).value)) {
							check.setChecked(true);
						}

						return view;
					}
				};
				adapterSpinner02 = new ArrayAdapter<String>(getActivity(),
						R.layout.myspinner, sile02) {
					@Override
					public View getDropDownView(int position, View convertView,
							ViewGroup parent) {
						View view = getActivity().getLayoutInflater()
								.inflate(R.layout.spinner_item_layout, null);
						TextView label = (TextView) view
								.findViewById(R.id.spinner_item_label);
						// ImageView check = (ImageView) view
						// .findViewById(R.id.spinner_item_checked_image);
						label.setText(sile02[position]);
						CheckBox check = (CheckBox) view
								.findViewById(R.id.spinner_item_checked_image);
						if (shaixuan02.equals(((ArrayList<WeiduBean>) dataR.get(0)).get(1).options.get(position).value)) {
							check.setChecked(true);
						}

						return view;
					}
				};
				// adapterSpinner01.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// adapterSpinner02.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner01.setAdapter(adapterSpinner01);
				spinner02.setAdapter(adapterSpinner02);
				spinner01.setSelection(index01);
				spinner02.setSelection(index02);
			}
			
		}

	}

	@Override
	public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid) {
		if (uuid.equals(zhuxingUUID)) {
			Toast.makeText(this.getActivity(), errorMessage.getErrorDes(), 1000)
					.show();
			LoadingDialog.dimmissLoading();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.date_start_bt:
		case R.id.date_end_bt:
			if (DateMin==null||DateMax==null) return;
			ShaixuanPopwindow.getShaixuanDatPopwindow(this.getActivity(), DateMin, DateMax,StartDay, EndDay, v,this);
			break;
		case R.id.zhuxing_check:
			setParams();
			break;
		default:
			break;
		}
	}

	private void setParams() {
		if (StartDay != null && EndDay != null) {
			try {
				JSONObject obj = new JSONObject(requestJson);
				JSONObject objP = obj.getJSONObject("chartJson").getJSONArray("params").getJSONObject(0);
				// objP.put("filtertype", 1);
				objP.put("st", StartDay);
				objP.put("end", EndDay);
				requestJson = obj.toString();
				sendRequest();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		if (DateMin!=null) {
			return;
		}
		if (spinner01.getVisibility() == View.VISIBLE
				&& spinner02.getVisibility() == View.VISIBLE) {
			if (shaixuan01 == null) {
				Toast.makeText(getActivity(), "第一个未选择", 1000).show();
				return;
			}
			if (shaixuan02 == null) {
				Toast.makeText(getActivity(), "第二个未选择", 1000).show();
				return;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
			try {
				Date d01=sdf.parse(shaixuan01);
				Date d02=sdf.parse(shaixuan02);
				if (d02.getTime()<d01.getTime()) {
					Toast.makeText(this.getActivity(), "开始月份不能大于结束月份", 1000).show();
					return;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try {
				JSONObject obj = new JSONObject(requestJson);
				obj.getJSONObject("chartJson").getJSONArray("params")
						.getJSONObject(0)
						.put("vals", shaixuan01 + "," + shaixuan02);
				requestJson = obj.toString();
				sendRequest();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		if (spinner01.getVisibility() == View.VISIBLE) {
			try {
				JSONObject obj = new JSONObject(requestJson);
				obj.getJSONObject("chartJson").getJSONArray("params")
						.getJSONObject(0).put("vals", shaixuan01);
				requestJson = obj.toString();
				sendRequest();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.zhuxing_spinner01:
			index01 = position;
			shaixuan01 = options01.get(position).value;
			break;
		case R.id.zhuxing_spinner02:
			index02 = position;
			shaixuan02 = options02.get(position).value;
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	public void setSelectDay(String t,String e){
		((Button)getActivity().findViewById(R.id.date_start_bt)).setText(t);
		((Button)getActivity().findViewById(R.id.date_end_bt)).setText(e);
		StartDay=t;
		EndDay=e;
	}

	@Override
	public void callBack(Object obj) {
		ArrayList<Object> dataR = (ArrayList<Object>) obj;
		LoadingDialog.dimmissLoading();
		BarData data = (BarData) dataR.get(1);
		data.setValueTextSize(10f);
		data.setValueTypeface(tf);
		mChart.setData(data);
		mChart.animateY(1000);
		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.BELOW_CHART_LEFT);
		l.setFormSize(8f);
		l.setXEntrySpace(4f);
	}

	@Override
	public void callBackFail(String obj) {
		// TODO Auto-generated method stub
		
	}
}
