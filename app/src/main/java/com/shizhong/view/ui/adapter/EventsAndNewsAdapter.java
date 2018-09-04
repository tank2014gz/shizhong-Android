package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityNewsWebContent;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.bean.EventsNewsBean;
import com.shizhong.view.ui.bean.EventsNewsContentBean;

import java.util.List;

/**
 * Created by yuliyan on 16/1/29.
 */
public class EventsAndNewsAdapter extends BaseSZAdapter<EventsNewsBean, EventsAndNewsAdapter.ViewHolder>
		implements OnClickListener {

	public EventsAndNewsAdapter(Context context, List<EventsNewsBean> list) {
		super(context, list);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.item_layout_envents_news;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.newsCover = (SimpleDraweeView) v.findViewById(R.id.news_cover);
		holder.newContent = (TextView) v.findViewById(R.id.news_content);
		holder.itemView = v.findViewById(R.id.item_view);
		holder.itemView.setOnClickListener(this);
		holder.newsTime = (TextView) v.findViewById(R.id.news_time);
		return holder;
	}

	@Override
	protected void initItem(int posiotion, EventsNewsBean data, ViewHolder holder) {
		String newsCover = data.coverUrl;
		if (TextUtils.isEmpty(newsCover)) {
			newsCover = "";
		} else {
			newsCover = FormatImageURLUtils.formatURL(newsCover, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		holder.newsCover.setImageURI(Uri.parse(newsCover));
		//
		// imageLoad.displayImage(newsCover, holder.newsCover, options);
//		Glide.with(mContext).load(newsCover).placeholder(R.drawable.sz_activity_default)
//				.error(R.drawable.sz_activity_default).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.newsCover);

		// Uri uri = Uri.parse(newsCover);
		// holder.newsCover.setImageURI(uri);

		String newsContent = data.title;
		if (TextUtils.isEmpty(newsContent)) {
			newsContent = "";
		}
		holder.newContent.setText(newsContent);
		String time = DateUtils.formateVideoCreateTime(data.createTime);
		if (TextUtils.isEmpty(time)) {
			time = "";
		}
		holder.newsTime.setText(time);
		holder.itemView.setTag(R.string.app_name, data.content);
	}

	class ViewHolder {
		SimpleDraweeView newsCover;
		TextView newContent;
		TextView newsTime;
		View itemView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_view:
			EventsNewsContentBean bean = (EventsNewsContentBean) v.getTag(R.string.app_name);
			if (bean != null) {
				mIntent.setClass(mContext, ActivityNewsWebContent.class);
				mIntent.putExtra(ContantsActivity.JieQu.NEWS_URL, bean.url);
				((Activity) mContext).startActivityForResult(mIntent, -1);
			}
			break;

		default:
			break;
		}

	}

}
