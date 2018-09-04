package com.shizhong.view.ui.adapter;

import java.util.List;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.view.tag.FlowLayout;
import com.shizhong.view.ui.base.view.tag.TagAdapter;
import com.shizhong.view.ui.bean.TopicBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class TagesAdapter extends TagAdapter<TopicBean> {

	private Context mContext;

	public TagesAdapter(List<TopicBean> datas, Context context) {
		super(datas);
		mContext = context;

	}

	@Override
	public View getView(FlowLayout parent, int position, TopicBean s) {
		TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tagview_item, parent, false);
		tv.setText(s.topicName);
		return tv;
	}

}
