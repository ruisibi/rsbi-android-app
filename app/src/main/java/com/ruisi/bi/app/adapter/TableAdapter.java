package com.ruisi.bi.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruisi.bi.app.bean.FormDataChildBean;

public class TableAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<TableRow> table;

	public TableAdapter(Context context, ArrayList<TableRow> table) {
		this.context = context;
		this.table = table;
	}

	@Override
	public int getCount() {
		return table.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public TableRow getItem(int position) {
		return table.get(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TableRow tableRow = table.get(position);
		return new TableRowView(this.context, tableRow);
	}

	/**
	 * TableRowView ʵ�ֱ���е���ʽ
	 * 
	 * @author hellogv
	 */
	class TableRowView extends LinearLayout {
		public TableRowView(Context context, TableRow tableRow) {
			super(context);

			this.setOrientation(LinearLayout.HORIZONTAL);
			if (tableRow.isHead) {
				TableRowHead tableRowHead = (TableRowHead) tableRow;
				TableCell tableCell = tableRowHead.TableCellhead;
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						tableCell.width, tableCell.height);// ���ո�Ԫָ���Ĵ�С���ÿռ�
				layoutParams.setMargins(0, 0, 2, 1);
				TextView textCell = new TextView(context);
				textCell.setLines(1);
				textCell.setGravity(Gravity.CENTER);
				textCell.setBackgroundColor(0xFFEFEFEF);// ������ɫ
				textCell.setText(tableCell.value.value);
				textCell.setTextColor(0xFF434A54);
				addView(textCell, layoutParams);
				LinearLayout layoutHeadContainer = new LinearLayout(context);
				LinearLayout layout01 = new LinearLayout(context);
				LinearLayout.LayoutParams layoutParams01 = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);// ���ո�Ԫָ���Ĵ�С���ÿռ�
				layout01.setLayoutParams(layoutParams01);
				layout01.setOrientation(LinearLayout.HORIZONTAL);
				layoutHeadContainer.setLayoutParams(layoutParams01);
				layoutHeadContainer.setOrientation(LinearLayout.VERTICAL);
				for (int i = 0; i < tableRowHead.cell.size(); i++) {
					TableCell headCell01 = tableRowHead.getCellValue(i);
					LinearLayout.LayoutParams textParams01 = new LinearLayout.LayoutParams(
							headCell01.width, headCell01.height);// ���ո�Ԫָ���Ĵ�С���ÿռ�
					textParams01.setMargins(0, 0, 2, 1);// Ԥ����϶����߿�
					if (tableCell.type == TableCell.STRING) {// ����Ԫ���ı�����
						TextView textheadCell = new TextView(context);
						textheadCell.setLines(1);
						textheadCell.setSingleLine();
						textheadCell.setEllipsize(TruncateAt.END);
						textheadCell.setGravity(Gravity.CENTER);
						textheadCell.setBackgroundColor(0xFFEFEFEF);// ������ɫ
						textheadCell.setText(headCell01.value.value);
						textheadCell.setTextColor(0xFF434A54);
						layout01.addView(textheadCell, textParams01);
					}
				}
				layoutHeadContainer.addView(layout01);
				// addView(layoutHeadContainer, layoutParams);
				LinearLayout layout02 = new LinearLayout(context);
				layout02.setLayoutParams(layoutParams01);
				layout02.setOrientation(LinearLayout.HORIZONTAL);
				for (int i = 0; i < tableRowHead.cell02.size(); i++) {
					TableCell headCell01 = tableRowHead.cell02.get(i);
					LinearLayout.LayoutParams textParams01 = new LinearLayout.LayoutParams(
							headCell01.width, headCell01.height);// ���ո�Ԫָ���Ĵ�С���ÿռ�
					textParams01.setMargins(0, 0, 2, 1);// Ԥ����϶����߿�
					if (tableCell.type == TableCell.STRING) {// ����Ԫ���ı�����
						TextView textheadCell = new TextView(context);
						textheadCell.setLines(1);
						textheadCell.setSingleLine();
						textheadCell.setEllipsize(TruncateAt.END);
						textheadCell.setGravity(Gravity.CENTER);
						textheadCell.setBackgroundColor(0xFFEFEFEF);// ������ɫ
						textheadCell.setText(headCell01.value.value);
						textheadCell.setTextColor(0xFF434A54);
						layout02.addView(textheadCell, textParams01);
					}
				}
				layoutHeadContainer.addView(layout02);
				addView(layoutHeadContainer);
			} else {
				for (int i = 0; i < tableRow.getSize(); i++) {// �����Ԫ��ӵ���
					TableCell tableCell = tableRow.getCellValue(i);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							tableCell.width, tableCell.height);// ���ո�Ԫָ���Ĵ�С���ÿռ�
					layoutParams.setMargins(0, 0, 2, 2);// Ԥ����϶����߿�
					if (tableCell.type == TableCell.STRING) {// ����Ԫ���ı�����
						TextView textCell = new TextView(context);
						textCell.setLines(1);
						textCell.setSingleLine();
						textCell.setEllipsize(TruncateAt.END);
						textCell.setGravity(Gravity.CENTER);
						textCell.setBackgroundColor(0xFFF9F9F9);// ������ɫ
						textCell.setText(tableCell.value.value);
						textCell.setTextColor(0xFF434A54);
						addView(textCell, layoutParams);
					}
				}
			}

		}
	}

	/**
	 * TableRow ʵ�ֱ�����
	 * 
	 * @author hellogv
	 */
	static public class TableRow {
		public ArrayList<TableCell> cell;
		public boolean isHead;

		public TableRow(ArrayList<TableCell> cell, boolean isHead) {
			this.cell = cell;
			this.isHead = isHead;
		}

		public int getSize() {
			return cell.size();
		}

		public TableCell getCellValue(int index) {
			if (index >= cell.size())
				return null;
			return cell.get(index);
		}

		public boolean getisHead() {
			return isHead;
		}
	}

	static public class TableRowHead extends TableRow {
		public ArrayList<TableCell> cell02;
		public TableCell TableCellhead;

		public TableRowHead(ArrayList<TableCell> cell01,
				ArrayList<TableCell> cell02, TableCell TableCellhead) {
			super(cell01, true);
			this.cell02 = cell02;
			this.TableCellhead = TableCellhead;
		}

	}

	/**
	 * TableCell ʵ�ֱ��ĸ�Ԫ
	 * 
	 * @author hellogv
	 */
	static public class TableCell {
		static public final int STRING = 0;
		static public final int IMAGE = 1;
		public FormDataChildBean value;
		public int width;
		public int height;
		private int type;

		public TableCell(FormDataChildBean value, int width, int height,
				int type) {
			this.value = value;
			this.width = width;
			this.height = height;
			this.type = type;

		}
	}

}
