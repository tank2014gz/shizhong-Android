package com.shizhong.view.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.DateUtils;
import com.shizhong.view.ui.ActivityClubDetail;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.ActivityNewsWebContent;
import com.shizhong.view.ui.ActivityTopicDetail;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.VideoPlayerActivity;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.base.view.convenientbanner.holder.Holder;
import com.shizhong.view.ui.bean.BannerBean;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class BannerHolderView2 implements Holder<BannerBean>, OnClickListener {
	private SimpleDraweeView imageView;
	private LayoutParams mBannerParams;
	private int mBannerWidth;
	private int mBannerHeight;
	private List<BannerBean> bannerBeanList;
	private Context mContext;
	private Intent intente = new Intent();
	private LayoutInflater mLayoutInflater;
	private  int next_action=-1;

	public BannerHolderView2(Context context, List<BannerBean> bannerBeanList) {
		mContext = context;
		this.bannerBeanList = bannerBeanList;
		mBannerWidth = UIUtils.getScreenWidthPixels(context);
		mBannerHeight = (mBannerWidth / 16) * 4;
	}

	@Override
	public View createView(Context context) {
		mLayoutInflater= LayoutInflater.from(context);
		View rootView=mLayoutInflater.inflate(R.layout.banner_layout,null);
		imageView =(SimpleDraweeView)rootView.findViewById(R.id.banner_image);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mBannerParams=imageView.getLayoutParams();
		mBannerParams.width=mBannerWidth;
		mBannerParams.height=mBannerHeight;
		imageView.setLayoutParams(mBannerParams);
		imageView.setOnClickListener(this);
		return imageView;

	}

	@Override
	public void UpdateUI(Context context, int position, BannerBean data) {
		String coverUrl = data.coverUrl;
		imageView.setTag(R.string.app_name, position);
		imageView.setImageURI(coverUrl);
//		Glide.with(mContext).load(coverUrl).placeholder(R.drawable.banner_default_icon)
//				.error(R.drawable.banner_default_icon).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
//				.into(imageView);
		// imageLoad.displayImage(coverUrl, imageView, options);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag(R.string.app_name);
		BannerBean bean = bannerBeanList.get(position);

		if (bean != null) {
			String bannerType = bean.targetType;
			String bannerId = bean.bannerId;
			Map<String, String> event = new HashMap<String, String>();
			event.put("banner_id", bannerId);
			event.put("type", "banner查看量");
			event.put("time", DateUtils.format9(System.currentTimeMillis()));
			MobclickAgent.onEvent(mContext, "banner_ID", event);
			// if () {
			// intente.setClass(mContext, ActivityBannerWebContent.class);
			// intente.putExtra(ContantsActivity.JieQu.NEWS_URL,
			// bean.content.url);
			// } else
			if (bannerType.equals("0")) {
				intente.setClass(mContext, ActivityMemberInfor.class);
				intente.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, bean.content.id);
				next_action=ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO;
			} else if (bannerType.equals("1")) {
				intente.setClass(mContext, VideoPlayerActivity.class);
				intente.putExtra(ContantsActivity.Video.VIDEO_ID, bean.content.id);
				next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_VIDER_INFO;
			} else if (bannerType.equals("3") || bannerType.equals("5")) {
				intente.setClass(mContext, ActivityNewsWebContent.class);
				intente.putExtra(ContantsActivity.JieQu.NEWS_URL, bean.content.url);
			} else if (bannerType.equals("2")) {
				intente.setClass(mContext, ActivityTopicDetail.class);
				intente.putExtra(ContantsActivity.Topic.TOPIC_ID, bean.content.id);
				next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_TOPIC_DETAIL;
			} else if (bannerType.equals("4")) {
				intente.setClass(mContext, ActivityClubDetail.class);
				intente.putExtra(ContantsActivity.Club.CLUB_ID, bean.content.id);
				next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_CREW_DETAIL;
			}
			((Activity) mContext).startActivityForResult(intente, next_action);
		}

		// ToastUtils.showShort(getActivity(), "bannerId:" + bean.bannerId);

	}
}
