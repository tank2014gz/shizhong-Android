package com.shizhong.view.ui.adapter;

import java.util.List;


import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.shizhong.view.ui.base.net.VideoHttpRequest.HttpRequestCallBack2;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberFansAdapter extends BaseSZAdapter<UserExtendsInfo, MemberFansAdapter.ViewHolder>
		implements OnClickListener {

	private String login_token;

	public MemberFansAdapter(Context context, List<UserExtendsInfo> list) {
		super(context, list);
		login_token = PrefUtils.getString(mContext, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
	}

	class ViewHolder {
		View itemView;
		SimpleDraweeView headImage;
		TextView nickName;
		TextView signture;
		ImageView attention;
		TextView hasAttention;
		ImageView gender;
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.item_member_attention_layout;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.itemView = v.findViewById(R.id.item_view);
		holder.itemView.setOnClickListener(this);
		holder.headImage = (SimpleDraweeView) v.findViewById(R.id.image_id);
		holder.nickName = (TextView) v.findViewById(R.id.nick_name);
		holder.gender = (ImageView) v.findViewById(R.id.gender);
		holder.signture = (TextView) v.findViewById(R.id.signture);
		holder.attention = (ImageView) v.findViewById(R.id.attention_btn);
		holder.hasAttention = (TextView) v.findViewById(R.id.has_attention);
		holder.attention.setOnClickListener(this);
		return holder;
	}

	@Override
	protected void initItem(int posiotion, UserExtendsInfo data, ViewHolder holder) {
		data.postion = posiotion;
		String imageUrl = data.headerUrl;
		if (TextUtils.isEmpty(imageUrl)) {
			imageUrl = "";
		} else {
			imageUrl = FormatImageURLUtils.formatURL(imageUrl, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		holder.headImage.setImageURI(Uri.parse(imageUrl));
//		Glide.with(mContext).load(imageUrl).placeholder(R.drawable.sz_head_default).error(R.drawable.sz_head_default)
//				.transform(new GlideCircleTransform(mContext)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.headImage);

		String nickName = data.nickname;
		if (TextUtils.isEmpty(nickName)) {
			nickName = "";
		}
		holder.nickName.setText(nickName);

		String sex = data.sex;
		if ("1".equals(sex)) {
			holder.gender.setImageResource(R.drawable.man_icon);
		} else if ("0".equals(sex)) {
			holder.gender.setImageResource(R.drawable.girl_icon);
		}

		String signture = data.signature;
		if (TextUtils.isEmpty(signture)) {
			signture = "";
		}
		holder.signture.setText(signture);

		String attentent = data.isAttention;
		if ("0".equals(attentent)) {
			holder.attention.setVisibility(View.VISIBLE);
			holder.hasAttention.setVisibility(View.GONE);
		} else if ("1".equals(attentent)) {
			holder.attention.setVisibility(View.GONE);
			holder.hasAttention.setVisibility(View.VISIBLE);
			holder.hasAttention.setText("已关注");
		}
		holder.attention.setTag(data);
		holder.itemView.setTag(data.memberId);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.attention_btn:
			UserExtendsInfo info = (UserExtendsInfo) v.getTag();
			if (info != null) {
				String memnerId = info.memberId;
				String type = info.isAttention;
				if (type.equals("0")) {
					type = "1";
				} else if (type.equals("1")) {
					type = "0";
				}
				final int position = info.postion;
				info.isAttention = type;
				list.set(position, info);
				notifyDataSetChanged();

				VideoHttpRequest.addAttention(mContext, login_token, memnerId, position, type,
						new HttpRequestCallBack2() {

							@Override
							public void callBackFail() {
								UserExtendsInfo info = list.get(position);
								String type = info.isAttention;
								if (type.equals("0")) {
									type = "1";
								} else if (type.equals("1")) {
									type = "0";
								}
								int position = info.postion;
								info.isAttention = type;
								list.set(position, info);
								notifyDataSetChanged();
							}
						});
			}
			break;
		case R.id.item_view:
			String memberId = (String) v.getTag();
			if (!TextUtils.isEmpty(memberId)) {
				mIntent.setClass(mContext, ActivityMemberInfor.class);
				mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
				((Activity) mContext).overridePendingTransition(R.anim.dialog_right_in_anim,
						R.anim.dialog_right_out_anim);
			}
			break;
		}
	}


}
