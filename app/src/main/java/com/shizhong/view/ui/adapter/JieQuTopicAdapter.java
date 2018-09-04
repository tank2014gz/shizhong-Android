package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityTopicDetail;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.TopicBean;
import com.hyphenate.easeui.ContantsActivity;

import java.util.List;

/**
 * Created by yuliyan on 16/1/27.
 */
public class JieQuTopicAdapter extends BaseSZAdapter<TopicBean, JieQuTopicAdapter.ViewHolder>{
//		implements OnClickListener {

	private int item_width;
	private int item_height;
	private GridView.LayoutParams mLayoutParams;

	public JieQuTopicAdapter(Context context, List<TopicBean> list) {
		super(context, list);
		item_width = UIUtils.getScreenWidthPixels(mContext) / 2-UIUtils.dipToPx(mContext,10);
		item_height = (item_width / 16) * 9;
		mLayoutParams=new GridView.LayoutParams(item_width,item_height);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.jiequ_topic_list_item_layout;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		v.setLayoutParams(mLayoutParams);
		holder.imageView = (SimpleDraweeView) v.findViewById(R.id.topic_image);
		holder.flowLayout = v.findViewById(R.id.flow_layout);
//		holder.imageView.setOnClickListener(this);
		holder.titleView = (TextView) v.findViewById(R.id.topic_title);
		return holder;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void initItem(int posiotion, TopicBean data, ViewHolder holder) {

		String imageUrl = data.coverUrl;
		if (TextUtils.isEmpty(imageUrl)) {
			imageUrl = "";
		} else {
			imageUrl = FormatImageURLUtils.formatURL(imageUrl, ContantsActivity.Image.MODLE,
					ContantsActivity.Image.MODLE);
		}
		holder.imageView.setImageURI(Uri.parse(imageUrl));

		String topicTitle = data.topicName;
		String topic = "#%s#";
		if (TextUtils.isEmpty(topicTitle)) {
			topicTitle = "";
		} else {
			topicTitle = new String().format(topic, topicTitle);
		}
		holder.titleView.setText(topicTitle);
		holder.imageView.setTag(R.string.app_name, data);
	}

	class ViewHolder {
		SimpleDraweeView imageView;
		TextView titleView;
		View flowLayout;
	}

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.topic_image:
//			TopicBean bean = (TopicBean) v.getTag(R.string.app_name);
//			if (bean != null) {
//				String topicId = bean.topicId;
//				String topicName = bean.topicName;
//				mIntent.setClass(mContext, ActivityTopicDetail.class);
//				mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, "#" + topicName + "#");
//				mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID, topicId);
//				((Activity) mContext).startActivityForResult(mIntent, -1);
//			}
//			break;
//
//		default:
//			break;
//		}
//
//	}
}
