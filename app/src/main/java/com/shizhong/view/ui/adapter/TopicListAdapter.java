package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityTopicDetail;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.bean.TopicBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import java.util.List;

/**
 * Created by yuliyan on 16/1/29.
 */
public class TopicListAdapter extends BaseSZAdapter<TopicBean, TopicListAdapter.ViewHolder>
		implements View.OnClickListener {

	public TopicListAdapter(Context context, List<TopicBean> list) {
		super(context, list);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.item_layout_topic_list;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.itemView = v.findViewById(R.id.topic_item);
		holder.itemView.setOnClickListener(this);
		holder.topivImage = (SimpleDraweeView) v.findViewById(R.id.topic_cover);
		holder.topicNickName = (TextView) v.findViewById(R.id.topic_name);
		holder.topicDes = (TextView) v.findViewById(R.id.topic_content);
		return holder;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void initItem(int posiotion, TopicBean data, ViewHolder holder) {
		data.position = posiotion;
		String topicUrl = data.coverUrl;
		if (TextUtils.isEmpty(topicUrl)) {
			topicUrl = "";
		} else {
			topicUrl = FormatImageURLUtils.formatURL(topicUrl, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		holder.topivImage.setImageURI(Uri.parse(topicUrl));
		// imageLoad.displayImage(topicUrl, holder.topivImage, options);
//		Glide.with(mContext).load(topicUrl).placeholder(R.drawable.sz_activity_default)
//				.error(R.drawable.sz_activity_default).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.topivImage);
		String name = data.topicName;
		if (TextUtils.isEmpty(name)) {
			name = "热门话题";
		}
		holder.topicNickName.setText(new String().format("#%s#", name));

		String des = data.description;
		if (TextUtils.isEmpty(des)) {
			des = "";
		}
		holder.topicDes.setText(des);
		holder.itemView.setTag(R.string.app_name, data);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.topic_item:
			TopicBean bean = (TopicBean) view.getTag(R.string.app_name);
			if (bean != null) {
				String topicId = bean.topicId;
				String topicName = "#" + bean.topicName + "#";
				int position = bean.position + 1;
				int page = (position / 10) + (position % 10 > 0 ? 1 : 0);
				mIntent.setClass(mContext, ActivityTopicDetail.class);
				mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topicName);
				mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID, topicId);
				mIntent.putExtra(ContantsActivity.Topic.TOPIC_PAGE, page);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_TOPIC_DETAIL);
			}
			break;
		}
	}

	class ViewHolder {
		View itemView;
		SimpleDraweeView topivImage;
		TextView topicNickName;
		TextView topicDes;

	}

}
