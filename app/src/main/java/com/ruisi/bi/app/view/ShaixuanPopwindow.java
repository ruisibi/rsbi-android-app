package com.ruisi.bi.app.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ruisi.bi.app.ConditionAcitivity;
import com.ruisi.bi.app.FormActivity;
import com.ruisi.bi.app.R;
import com.ruisi.bi.app.bean.ShaixuanBean;
import com.ruisi.bi.app.bean.WeiduOptionBean;
import com.ruisi.bi.app.common.AppContext;
import com.ruisi.bi.app.fragment.TuBingxingFragment;
import com.ruisi.bi.app.fragment.TuLeidaFragment;
import com.ruisi.bi.app.fragment.TuQuxianFragment;
import com.ruisi.bi.app.fragment.TuTiaoxingFragment;
import com.ruisi.bi.app.fragment.TuZhuxingFragment;

public class ShaixuanPopwindow {
	public static PopupWindow getShaixuanPopwindow(final Activity context,
			final ArrayList<WeiduOptionBean> options, View vs, String head) {
		View mView = LayoutInflater.from(context).inflate(
				R.layout.select_popwindow_layout, null);

		// ((TextView)
		// mView.findViewById(R.id.MyPopwindow_title)).setText(head);
		ListView lv = (ListView) mView.findViewById(R.id.MyPopwindow_listview);
		lv.setAdapter(new ShaixuanAdapter(context, options, 0));
		final PopupWindow menuWindow = new PopupWindow(mView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setFocusable(true);
		menuWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		menuWindow.setTouchable(true);
		menuWindow.setContentView(mView);
		// int[] location = new int[2];
		// vs.getLocationOnScreen(location);
		menuWindow.showAtLocation(vs, Gravity.TOP | Gravity.LEFT, 0, 0);
		mView.findViewById(R.id.cancle).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow.dismiss();
					}
				});

		mView.findViewById(R.id.commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						StringBuffer value = new StringBuffer();
						for (int i = 0; i < options.size(); i++) {
							WeiduOptionBean bean = options.get(i);
							if (bean.isChecked) {
								value.append(bean.value);
								value.append(",");
							}
						}
						if (value.length() > 0) {
							value.delete(value.length() - 1, value.length());
						}
						((ConditionAcitivity) context).setShaixuan(value
								.toString());
						menuWindow.dismiss();
					}
				});
		mView.findViewById(R.id.MyPopwindow_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow.dismiss();
					}
				});

		return menuWindow;
	}

	static class ShaixuanAdapter extends BaseAdapter {
		private ArrayList<WeiduOptionBean> Options;
		private LayoutInflater inflater;
		private int type;

		public ShaixuanAdapter(Context context,
				ArrayList<WeiduOptionBean> Options, int type) {
			this.inflater = LayoutInflater.from(context);
			this.Options = Options;
			this.type = type;
		}

		@Override
		public int getCount() {
			return Options.size();
		}

		@Override
		public Object getItem(int position) {
			return Options.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.theme_item, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.cb = (CheckBox) convertView
						.findViewById(R.id.cb_contacts);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_contacts_item_name);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final WeiduOptionBean weiduBean = Options.get(position);
			holder.tv_name.setText(weiduBean.text);
			// holder.cb.setBackgroundResource(R.drawable.fang_check_style);
			holder.cb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setChecked(position);
				}
			});
			holder.tv_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setChecked(position);
				}
			});
			holder.cb.setChecked(weiduBean.isChecked);
			return convertView;
		}

		private void setChecked(int position) {
			Options.get(position).isChecked = !Options.get(position).isChecked;
			if (type != 0)
				for (int i = 0; i < Options.size(); i++) {
					if (i != position) {
						Options.get(i).isChecked = false;
					}
				}
			notifyDataSetChanged();
		}

		private final class ViewHolder {
			TextView tv_name;
			CheckBox cb;
		}
	}

	public static void getShaixuanTimePopwindow(final Activity context,
			final ShaixuanBean bean, View vs, String head,
			final boolean shaixuan_isHand) {
		View mView = LayoutInflater.from(context).inflate(
				R.layout.shaixuan_time_layout, null);

		// ((TextView)
		// mView.findViewById(R.id.MyPopwindow_title)).setText(head);

		((TextView) mView.findViewById(R.id.start_text))
				.setText(bean.startName);
		((TextView) mView.findViewById(R.id.end_text)).setText(bean.endName);
		final ListView start_sp = ((ListView) mView
				.findViewById(R.id.shaixuan_time_start_sp));
		start_sp.setAdapter(new ShaixuanAdapter(context, bean.startOptions, 1));
		final ListView end_sp = ((ListView) mView
				.findViewById(R.id.shaixuan_time_end_sp));
		end_sp.setAdapter(new ShaixuanAdapter(context, bean.endOptions, 1));

		final PopupWindow menuWindow = new PopupWindow(mView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setFocusable(true);
		menuWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		menuWindow.setTouchable(true);
		menuWindow.setContentView(mView);
		// int[] location = new int[2];
		// vs.getLocationOnScreen(location);
		menuWindow.showAtLocation(vs, Gravity.TOP | Gravity.LEFT, 0, 0);
		mView.findViewById(R.id.cancle).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow.dismiss();
					}
				});

		mView.findViewById(R.id.commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						String end_text = null, start_text = null;
						for (int i = 0; i < bean.endOptions.size(); i++) {
							if (bean.endOptions.get(i).isChecked) {
								end_text = bean.endOptions.get(i).value;
								break;
							}
						}
						for (int j = 0; j < bean.startOptions.size(); j++) {
							if (bean.startOptions.get(j).isChecked) {
								start_text = bean.startOptions.get(j).value;
								break;
							}
						}
						
						if (end_text != null && start_text != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
							try {
								Date d01 = sdf.parse(start_text);
								Date d02 = sdf.parse(end_text);
								if (d02.getTime() < d01.getTime()) {
									Toast.makeText(context, "开始月份不能大于结束月份", 1000)
											.show();
									return;
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (shaixuan_isHand)
								((ConditionAcitivity) context)
										.setShaixuanTimeHang(start_text,
												end_text, bean.type);
							else
								((ConditionAcitivity) context)
										.setShaixuanTimeLie(start_text,
												end_text, bean.type);
							menuWindow.dismiss();
						} else {
							Toast.makeText(context, "请选择！！！", 1000).show();
						}
					}
				});
		mView.findViewById(R.id.MyPopwindow_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow.dismiss();
					}
				});
	}

	public static void getShaixuanDatPopwindow(final Activity context,
			final String startD, final String endD,
			final String selectedStartD, final String selectedEndD, View vs,
			final Fragment f) {
		String[] startDate = new String[3];
		String[] endDate = new String[3];
		if (selectedStartD != null) {
			startDate[0] = selectedStartD.substring(0, 4);
			startDate[1] = selectedStartD.substring(4, 6);
			startDate[2] = selectedStartD.substring(6, 8);
		} else {
			startDate[0] = startD.substring(0, 4);
			startDate[1] = startD.substring(4, 6);
			startDate[2] = startD.substring(6, 8);
		}
		if (selectedEndD != null) {
			endDate[0] = selectedEndD.substring(0, 4);
			endDate[1] = selectedEndD.substring(4, 6);
			endDate[2] = selectedEndD.substring(6, 8);
		} else {
			endDate[0] = endD.substring(0, 4);
			endDate[1] = endD.substring(4, 6);
			endDate[2] = endD.substring(6, 8);
		}

		if (startDate == null || endDate == null)
			return;
		final int[] startDateINT = { Integer.parseInt(startDate[0]),
				Integer.parseInt(startDate[1]), Integer.parseInt(startDate[2]) };
		final int[] endDateINT = { Integer.parseInt(endDate[0]),
				Integer.parseInt(endDate[1]), Integer.parseInt(endDate[2]) };
		View mView = LayoutInflater.from(context).inflate(
				R.layout.shaixuan_dat_layout, null);
		final NumberPicker start_year = (NumberPicker) mView
				.findViewById(R.id.np_start_year);
		final NumberPicker start_mouth = (NumberPicker) mView
				.findViewById(R.id.np_start_mouth);
		final NumberPicker start_dat = (NumberPicker) mView
				.findViewById(R.id.np_start_dat);

		final NumberPicker end_year = (NumberPicker) mView
				.findViewById(R.id.np_end_year);
		final NumberPicker end_mouth = (NumberPicker) mView
				.findViewById(R.id.np_end_mouth);
		final NumberPicker end_dat = (NumberPicker) mView
				.findViewById(R.id.np_end_dat);

		start_year.setMaxValue(endDateINT[0]);
		start_year.setMinValue(startDateINT[0]);
		start_year.setValue(startDateINT[0]);
		end_year.setMaxValue(endDateINT[0]);
		end_year.setMinValue(startDateINT[0]);
		end_year.setValue(endDateINT[0]);
		start_mouth.setMaxValue(12);
		start_mouth.setMinValue(1);
		start_mouth.setValue(startDateINT[1]);
		end_mouth.setMaxValue(12);
		end_mouth.setMinValue(1);
		end_mouth.setValue(endDateINT[1]);
		start_dat.setMaxValue(30);
		start_dat.setMinValue(1);
		start_dat.setValue(startDateINT[2]);
		end_dat.setMaxValue(30);
		end_dat.setMinValue(1);
		end_dat.setValue(endDateINT[2]);
		// ((TextView)
		// mView.findViewById(R.id.MyPopwindow_title)).setText("日期");

		final PopupWindow menuWindow = new PopupWindow(mView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setFocusable(true);
		menuWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		menuWindow.setTouchable(true);
		menuWindow.setContentView(mView);
		int[] location = new int[2];
		vs.getLocationOnScreen(location);
		menuWindow.showAtLocation(vs, Gravity.TOP | Gravity.LEFT, location[0],
				location[1]);
		mView.findViewById(R.id.cancle).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow.dismiss();
					}
				});

		mView.findViewById(R.id.commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						int[] startTime = { start_year.getValue(),
								start_mouth.getValue(), start_dat.getValue() };
						int[] endTime = { end_year.getValue(),
								end_mouth.getValue(), end_dat.getValue() };
						if (checkDat(startDateINT, endDateINT, startTime,
								endTime)) {
							String startm = startTime[1] + "";
							String startd = startTime[2] + "";
							String endm = endTime[1] + "";
							String endd = endTime[2] + "";
							if (startTime[1] < 10) {
								startm = "0" + startTime[1];
							}
							if (startTime[2] < 10) {
								startd = "0" + startTime[2];
							}
							if (endTime[1] < 10) {
								endm = "0" + endm;
							}
							if (endTime[2] < 10) {
								endd = "0" + endd;
							}
							String startT = startTime[0] + "" + startm + startd;
							String endT = endTime[0] + "" + endm + endd;
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyyMMdd");
							try {
								Date d01 = sdf.parse(startT);
								Date d02 = sdf.parse(endT);
								if (d02.getTime() < d01.getTime()) {
									Toast.makeText(context, "开始时间不能大于结束时间",
											1000).show();
									return;
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}

							if (context instanceof FormActivity) {
								((FormActivity) context).setSelectDay(startT,
										endT);

							}
							if (f != null) {
								if (f instanceof TuQuxianFragment) {
									((TuQuxianFragment) f).setSelectDay(startT,
											endT);
								} else if (f instanceof TuZhuxingFragment) {
									((TuZhuxingFragment) f).setSelectDay(
											startT, endT);
								} else if (f instanceof TuBingxingFragment) {
									((TuBingxingFragment) f).setSelectDay(
											startT, endT);
								} else if (f instanceof TuTiaoxingFragment) {
									((TuTiaoxingFragment) f).setSelectDay(
											startT, endT);
								} else if (f instanceof TuLeidaFragment) {
									((TuLeidaFragment) f).setSelectDay(startT,
											endT);
								}
							}

							menuWindow.dismiss();
						} else {
							Toast.makeText(context, "日期超界", 3000).show();
						}
					}
				});
		mView.findViewById(R.id.MyPopwindow_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow.dismiss();
					}
				});
	}

	public static void getShaixuanDatPopwindow(final Activity context,
			final ShaixuanBean bean, View vs, String head,
			final boolean shaixuan_isHand) {
		String[] startDate=new String[3];
		String[] endDate=new String[3];
		if (bean.endName == null) {
			String[] start = bean.startDayMin.split("-");
			String[] end = bean.endDayMax.split("-");
			startDate[0]=start[0];
			startDate[1]=start[1];
			startDate[2]=start[2];
			endDate[0]=end[0];
			endDate[1]=end[1];
			endDate[2]=end[2];
		} else if (bean.endName != null) {
			String[] start = { bean.startName.substring(0, 4),
					bean.startName.substring(4, 6),
					bean.startName.substring(6, 8) };
			String[] end = { bean.endName.substring(0, 4),
					bean.endName.substring(4, 6),
					bean.endName.substring(6, 8) };
			startDate[0]=start[0];
			startDate[1]=start[1];
			startDate[2]=start[2];
			endDate[0]=end[0];
			endDate[1]=end[1];
			endDate[2]=end[2];
		}

		if (startDate == null || endDate == null)
			return;
		final int[] startDateINT = { Integer.parseInt(startDate[0]),
				Integer.parseInt(startDate[1]), Integer.parseInt(startDate[2]) };
		final int[] endDateINT = { Integer.parseInt(endDate[0]),
				Integer.parseInt(endDate[1]), Integer.parseInt(endDate[2]) };
		View mView = LayoutInflater.from(context).inflate(
				R.layout.shaixuan_dat_layout, null);
		final NumberPicker start_year = (NumberPicker) mView
				.findViewById(R.id.np_start_year);
		final NumberPicker start_mouth = (NumberPicker) mView
				.findViewById(R.id.np_start_mouth);
		final NumberPicker start_dat = (NumberPicker) mView
				.findViewById(R.id.np_start_dat);

		final NumberPicker end_year = (NumberPicker) mView
				.findViewById(R.id.np_end_year);
		final NumberPicker end_mouth = (NumberPicker) mView
				.findViewById(R.id.np_end_mouth);
		final NumberPicker end_dat = (NumberPicker) mView
				.findViewById(R.id.np_end_dat);

		start_year.setMaxValue(endDateINT[0]);
		start_year.setMinValue(startDateINT[0]);
		start_year.setValue(startDateINT[0]);
		end_year.setMaxValue(endDateINT[0]);
		end_year.setMinValue(startDateINT[0]);
		end_year.setValue(endDateINT[0]);
		start_mouth.setMaxValue(12);
		start_mouth.setMinValue(1);
		start_mouth.setValue(startDateINT[1]);
		end_mouth.setMaxValue(12);
		end_mouth.setMinValue(1);
		end_mouth.setValue(endDateINT[1]);
		start_dat.setMaxValue(30);
		start_dat.setMinValue(1);
		start_dat.setValue(startDateINT[2]);
		end_dat.setMaxValue(30);
		end_dat.setMinValue(1);
		end_dat.setValue(endDateINT[2]);
		// ((TextView)
		// mView.findViewById(R.id.MyPopwindow_title)).setText(head);

		final PopupWindow menuWindow = new PopupWindow(mView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setFocusable(true);
		menuWindow.setBackgroundDrawable(new ColorDrawable(-00000));
		menuWindow.setTouchable(true);
		menuWindow.setContentView(mView);
		// int[] location = new int[2];
		// vs.getLocationOnScreen(location);
		menuWindow.showAtLocation(vs, Gravity.TOP | Gravity.LEFT, 0, 0);
		mView.findViewById(R.id.cancle).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow.dismiss();
					}
				});

		mView.findViewById(R.id.commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						int[] startTime = { start_year.getValue(),
								start_mouth.getValue(), start_dat.getValue() };
						int[] endTime = { end_year.getValue(),
								end_mouth.getValue(), end_dat.getValue() };
						if (checkDat(startDateINT, endDateINT, startTime,
								endTime)) {
							String startm = startTime[1] + "";
							String startd = startTime[2] + "";
							String endm = endTime[1] + "";
							String endd = endTime[2] + "";
							if (startTime[1] < 10) {
								startm = "0" + startTime[1];
							}
							if (startTime[2] < 10) {
								startd = "0" + startTime[2];
							}
							if (endTime[1] < 10) {
								endm = "0" + endm;
							}
							if (endTime[2] < 10) {
								endd = "0" + endd;
							}
							String startT = startTime[0] + "" + startm + startd;
							String endT = endTime[0] + "" + endm + endd;
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyyMMdd");
							try {
								Date d01 = sdf.parse(startT);
								Date d02 = sdf.parse(endT);
								if (d02.getTime() < d01.getTime()) {
									Toast.makeText(context, "开始时间不能大于结束时间",
											1000).show();
									return;
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (shaixuan_isHand)
								((ConditionAcitivity) context)
										.setShaixuanTimeHang(startT, endT,
												bean.type);
							else
								((ConditionAcitivity) context)
										.setShaixuanTimeLie(startT, endT,
												bean.type);
							menuWindow.dismiss();
						} else {
							Toast.makeText(context, "日期超界", 3000).show();
						}
					}
				});
		mView.findViewById(R.id.MyPopwindow_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuWindow.dismiss();
					}
				});
	}

	private static boolean checkDat(int[] orginStart, int[] orginEnd,
			int[] start, int[] end) {
		if (orginStart[0] > start[0]) {
			return false;
		} else if (orginStart[0] == start[0] && orginStart[1] > start[1]) {
			return false;
		} else if (orginStart[0] == start[0] && orginStart[1] == start[1]
				&& orginStart[2] > start[2]) {
			return false;
		}
		if (orginEnd[0] < end[0]) {
			return false;
		} else if (orginEnd[0] == end[0] && orginEnd[1] < end[1]) {
			return false;
		} else if (orginEnd[0] == end[0] && orginEnd[1] == end[1]
				&& orginEnd[2] < end[2]) {
			return false;
		}
		if (start[0] > end[0]) {
			return false;
		} else if (start[0] == end[0] && start[1] > end[1]) {
			return false;
		} else if (start[0] == end[0] && start[1] == end[1]
				&& start[2] > end[2]) {
			return false;
		}
		return true;
	}
}
