package com.shizhong.view.ui.base;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.shizhong.view.ui.R;

import java.util.List;

/**
 * ListView基类填充器
 *
 * @param <T>
 * @author Administrator
 */
public abstract class BaseSZAdapter<T, H> extends BaseAdapter {

	public List<T> list;
	public Context mContext;
	public LayoutInflater mInflater;
	public Intent mIntent = new Intent();

	public BaseSZAdapter(Context context, List<T> list) {
		this.list = list;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View v, ViewGroup group) {
		H h = null;
		if (v == null) {
			v = mInflater.inflate(getLayoutId(), null);
			h = getHodler(v);
			v.setTag(R.string.app_name, h);
		} else {
			h = (H) v.getTag(R.string.app_name);
		}
		T t = null;
		if (list != null && list.size() > 0 && (t = list.get(position)) != null) {
			initItem(position, t, h);
			return v;
		} else {
			return null;
		}
	}

	protected abstract int getLayoutId();

	protected abstract H getHodler(View v);

	protected abstract void initItem(int posiotion, T data, H holder);
}
