package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.VideoPlayerActivity;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.hyphenate.easeui.utils.GlideRoundTransform;
import com.shizhong.view.ui.base.utils.DrawableUitls;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.BaseVideoBean;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;

import java.util.List;

public class RecommendDancerAdapter extends BaseSZAdapter<BaseVideoBean, RecommendDancerAdapter.ViewHolder>
		implements View.OnClickListener {

	private int itemWidth;
	private LayoutParams mItemLayoutParams;
	private int itemImageWidth;
	private RelativeLayout.LayoutParams mItemImageLayoutParams;
	private RelativeLayout.LayoutParams mItemBottomLayoutParams;
	private int listCount;

	public RecommendDancerAdapter(Context context, List<BaseVideoBean> list) {
		super(context, list);
		this.itemWidth = ((UIUtils.getScreenWidthPixels(context)) / 2);
		this.mItemLayoutParams = new LayoutParams(itemWidth, itemWidth + UIUtils.dipToPx(mContext, 28));
		this.itemImageWidth = itemWidth - UIUtils.dipToPx(context, 1);
		this.mItemImageLayoutParams = new RelativeLayout.LayoutParams(itemImageWidth, itemImageWidth);
		this.mItemBottomLayoutParams = new RelativeLayout.LayoutParams(itemImageWidth, UIUtils.dipToPx(context, 28));
		this.mItemBottomLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	}

	@Override
	public int getCount() {
		int count = list.size();
		this.listCount = count;
		return count % 2 == 0 ? count / 2 : (count / 2) + 1;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.image:
		case R.id.image1:
			BaseVideoBean bean = (BaseVideoBean) view.getTag(R.string.app_name);
			// mIntent.setClass(mContext, VideoDetailActivity.class);
			mIntent.setClass(mContext, VideoPlayerActivity.class);
			mIntent.putExtra(ContantsActivity.Video.VIDEO_ID, bean.videoId);
			mIntent.putExtra(ContantsActivity.Video.VIDEO_POSITION, bean.position);
			((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.ACTION_SKIP_VIDEO_DETALE);

			break;
		case R.id.user_head:
		case R.id.user_head1:
			String memberId = (String) view.getTag(R.string.app_name);
			if (!TextUtils.isEmpty(memberId)) {
				mIntent.setClass(mContext, ActivityMemberInfor.class);
				mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
			}
			break;
		}
	}

	static class ViewHolder {
		SimpleDraweeView imageView;
		SimpleDraweeView headView;
		View layoutView;
		TextView nickNameView;
		TextView likeView;
		SimpleDraweeView imageView1;
		TextView signture;
		View bottomLayout;
		SimpleDraweeView headView1;
		View layoutView1;
		TextView nickNameView1;
		TextView likeView1;
		View bottomLayout1;
		TextView signture1;
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_item_dance_image;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.imageView = (SimpleDraweeView) v.findViewById(R.id.image);
		holder.imageView.setLayoutParams(mItemImageLayoutParams);
		holder.imageView.setOnClickListener(this);
		holder.layoutView = v.findViewById(R.id.layout);
		holder.layoutView.setLayoutParams(mItemLayoutParams);
		holder.nickNameView = (TextView) v.findViewById(R.id.nickname);
		holder.likeView = (TextView) v.findViewById(R.id.like);
		holder.likeView.setEnabled(false);
		holder.headView = (SimpleDraweeView) v.findViewById(R.id.user_head);
		holder.headView.setOnClickListener(this);
		holder.bottomLayout = v.findViewById(R.id.bottom_layout);
		holder.bottomLayout.setLayoutParams(mItemBottomLayoutParams);
		holder.signture = (TextView) v.findViewById(R.id.signture);
		holder.layoutView1 = v.findViewById(R.id.layout1);
		holder.layoutView1.setLayoutParams(mItemLayoutParams);
		holder.imageView1 = (SimpleDraweeView) v.findViewById(R.id.image1);
		holder.imageView1.setOnClickListener(this);
		holder.imageView1.setLayoutParams(mItemImageLayoutParams);
		holder.nickNameView1 = (TextView) v.findViewById(R.id.nickname_1);
		holder.likeView1 = (TextView) v.findViewById(R.id.like_1);
		holder.headView1 = (SimpleDraweeView) v.findViewById(R.id.user_head1);
		holder.headView1.setOnClickListener(this);
		holder.bottomLayout1 = v.findViewById(R.id.bottom_layout1);
		holder.bottomLayout1.setLayoutParams(mItemBottomLayoutParams);
		holder.signture1 = (TextView) v.findViewById(R.id.signture1);
		return holder;
	}

	@Override
	protected void initItem(int posiotion, BaseVideoBean data1, ViewHolder holder) {
		// TODO
		if (list.size() > 0) {
			int leftPosition = (posiotion * 2);
			if (leftPosition < listCount) {
				BaseVideoBean data_left = list.get(leftPosition);
				data_left.position = leftPosition;
				UserExtendsInfo leftInfo = data_left.memberInfo;
				String imageUrl = data_left.coverUrl;
				if (TextUtils.isEmpty(imageUrl)) {
					imageUrl = "";
				} else {
					imageUrl = FormatImageURLUtils.formatURL(imageUrl, ContantsActivity.Image.MODLE_HDPI,
							ContantsActivity.Image.MODLE_HDPI);
				}
				holder.imageView.setImageURI(Uri.parse(imageUrl));
				// imageLoad.displayImage(imageUrl, holder.imageView, options);
//				Glide.with(mContext).load(imageUrl).transform(new GlideRoundTransform(mContext, 5))
//						.placeholder(R.drawable.sz_activity_default).error(R.drawable.sz_activity_default)
//						.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.imageView);
				String headImageUrl = "";
				if (leftInfo != null) {
					headImageUrl = leftInfo.headerUrl;
				}
				if (TextUtils.isEmpty(headImageUrl)) {
					headImageUrl = "";
				} else {
					headImageUrl = FormatImageURLUtils.formatURL(headImageUrl, ContantsActivity.Image.SMALL,
							ContantsActivity.Image.SMALL);
				}
				holder.headView.setImageURI(Uri.parse(headImageUrl));
//				Glide.with(mContext).load(headImageUrl).placeholder(R.drawable.sz_head_default)
//						.error(R.drawable.sz_head_default).transform(new GlideCircleTransform(mContext))
//						.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.headView);
				String nickName = "";
				if (leftInfo != null) {
					nickName = data_left.memberInfo.nickname;
				}
				if (TextUtils.isEmpty(nickName)) {

					nickName = "匿名舞者";
				}
				holder.nickNameView.setText(nickName);

				String signture = data_left.description;
				if (TextUtils.isEmpty(signture)) {
					signture = "";
				}
				holder.signture.setText(signture);

				long likeCount = data_left.likeCount;
				if (likeCount <= 0) {
					holder.likeView.setText("0");
				} else {
					holder.likeView.setText(likeCount + "");
				}
				String isLikeStr = data_left.isLike;

				if (!TextUtils.isEmpty(isLikeStr)) {
					boolean isLike = isLikeStr.equals("1");
					if (isLike) {
						DrawableUitls.setCompoundDrawablesWithIntrinsicBounds(holder.likeView,
								R.drawable.sz_like_red_small, 0, 0, 0);
					} else {
						DrawableUitls.setCompoundDrawablesWithIntrinsicBounds(holder.likeView,
								R.drawable.sz_like_grey_small, 0, 0, 0);
					}
				}

				holder.imageView.setTag(R.string.app_name, data_left);
				holder.headView.setTag(R.string.app_name, data_left.memberId);
				holder.layoutView.setVisibility(View.VISIBLE);
			} else {
				holder.layoutView.setVisibility(View.GONE);
			}
			int rightPosition = posiotion * 2 + 1;
			if (rightPosition < listCount) {

				BaseVideoBean data_right = list.get(rightPosition);
				data_right.position = rightPosition;
				UserExtendsInfo rightInfo = data_right.memberInfo;
				String imageUrl = data_right.coverUrl;
				if (TextUtils.isEmpty(imageUrl)) {
					imageUrl = "";
				} else {
					imageUrl = FormatImageURLUtils.formatURL(imageUrl, ContantsActivity.Image.MODLE_HDPI,
							ContantsActivity.Image.MODLE_HDPI);
				}
				holder.imageView1.setImageURI(Uri.parse(imageUrl));
				// imageLoad.displayImage(imageUrl, holder.imageView1, options);
//				Glide.with(mContext).load(imageUrl).placeholder(R.drawable.sz_activity_default)
//						.error(R.drawable.sz_activity_default).transform(new GlideRoundTransform(mContext, 5))
//						.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.imageView1);
				String headImageUrl = "";
				if (rightInfo != null) {
					headImageUrl = data_right.memberInfo.headerUrl;
				}
				if (TextUtils.isEmpty(headImageUrl)) {
					headImageUrl = "";
				} else {
					headImageUrl = FormatImageURLUtils.formatURL(headImageUrl, ContantsActivity.Image.SMALL,
							ContantsActivity.Image.SMALL);
				}
				holder.headView1.setImageURI(Uri.parse(headImageUrl));
				// imageLoad.displayImage(headImageUrl, holder.headView1,
				// headOptions);
//				Glide.with(mContext).load(headImageUrl).placeholder(R.drawable.sz_head_default)
//						.error(R.drawable.sz_head_default).transform(new GlideCircleTransform(mContext))
//						.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.headView1);
				String nickName = "";
				if (rightInfo != null) {
					nickName = rightInfo.nickname;
				}
				if (TextUtils.isEmpty(nickName)) {
					nickName = "匿名舞者";
				}
				holder.nickNameView1.setText(nickName);

				String signture = data_right.description;
				if (TextUtils.isEmpty(signture)) {
					signture = "";
				}
				holder.signture1.setText(signture);

				long likeCount = data_right.likeCount;
				if (likeCount <= 0) {
					holder.likeView1.setText("0");
				} else {
					holder.likeView1.setText(likeCount + "");
				}

				String isLikeStr = data_right.isLike;

				if (!TextUtils.isEmpty(isLikeStr)) {
					boolean isLike = isLikeStr.equals("1");
					if (isLike) {
						DrawableUitls.setCompoundDrawablesWithIntrinsicBounds(holder.likeView1,
								R.drawable.sz_like_red_small, 0, 0, 0);
					} else {
						DrawableUitls.setCompoundDrawablesWithIntrinsicBounds(holder.likeView1,
								R.drawable.sz_like_grey_small, 0, 0, 0);
					}
				}
				holder.imageView1.setTag(R.string.app_name, data_right);
				holder.headView1.setTag(R.string.app_name, data_right.memberId);
				holder.layoutView1.setVisibility(View.VISIBLE);
			} else {
				holder.layoutView1.setVisibility(View.GONE);
			}

		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ContantsActivity.Action.ACTION_SKIP_VIDEO_DETALE:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					int position = data.getIntExtra(ContantsActivity.Video.VIDEO_POSITION, 0);
					boolean isDelect = data.getBooleanExtra(ContantsActivity.Video.VIDEO_DELECT, false);
					if (isDelect) {
						list.remove(position);
					} else {
						boolean isLike = data.getBooleanExtra(ContantsActivity.Video.VIDEO_IS_LIKED, false);
						long likeCount = data.getLongExtra(ContantsActivity.Video.VIDEO_LIKE_NUM, 0);
						long replyCount = data.getLongExtra(ContantsActivity.Video.VIDEO_REPLY_COUNT, 0);
						if (list.size() > position) {
							BaseVideoBean bean = list.get(position);
							bean.isLike = isLike ? "1" : "0";
							bean.likeCount = likeCount;
							bean.commentCount = replyCount;
							list.set(position, bean);

						}
					}
					notifyDataSetChanged();
				}
			}
			break;

		default:
			break;
		}
	}

}
