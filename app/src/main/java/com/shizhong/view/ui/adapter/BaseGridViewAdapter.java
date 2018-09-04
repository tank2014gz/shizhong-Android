package com.shizhong.view.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by xiaolin on 2015/1/24.
 * 
 * @param <T>
 * 
 * @param <T>
 */
public abstract class BaseGridViewAdapter<T> extends BaseAdapter {

	protected int hidePosition = AdapterView.INVALID_POSITION;
	protected Context mContext;
	protected List<T> data;
	protected int  itemTypeCount  ;
	 
	public BaseGridViewAdapter(Context context,List<T>data) {
		this(context,data,1);
	 
	}

	public BaseGridViewAdapter(Context context,List<T>data,int itemTypeCount) {
		super();
		this.mContext=context;
        this.data=data;
        this.itemTypeCount=itemTypeCount;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data!=null?data.size():0;
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return data!=null?data.get(position):null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent)  ;
	 

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return super.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return itemTypeCount;
	}
	

	public void hideView(int pos) {
		hidePosition = pos;
		notifyDataSetChanged();
	}

	public void showHideView() {
		hidePosition = AdapterView.INVALID_POSITION;
		notifyDataSetChanged();
	}

	public void removeView(int pos) {
		data.remove(pos);
		notifyDataSetChanged();
	}
	 

	// �����϶�ʱ��gridView
		public void swapView(int draggedPos, int destPos) {
			// ��ǰ����϶�������item����ǰ��
			if (draggedPos < destPos) {
				data.add(destPos + 1, getItem(draggedPos));
				data.remove(draggedPos);
			}
			// �Ӻ���ǰ�϶�������item���κ���
			else if (draggedPos > destPos) {
				data.add(destPos, getItem(draggedPos));
				data.remove(draggedPos + 1);
			}
			hidePosition = destPos;
			notifyDataSetChanged();
		}

 
	 
	 
}
