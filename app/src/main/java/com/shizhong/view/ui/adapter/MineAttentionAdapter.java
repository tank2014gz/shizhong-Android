package com.shizhong.view.ui.adapter;

import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MineAttentionAdapter extends BaseSZAdapter<UserExtendsInfo, MineAttentionAdapter.ViewHolder>
		implements OnClickListener {


	public MineAttentionAdapter(Context context, List<UserExtendsInfo> list) {
		super(context, list);
	}

	class ViewHolder {
		View itemView;
		SimpleDraweeView headImage;
		TextView nickName;
		TextView signture;
		TextView attention;
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
		holder.headImage = (SimpleDraweeView)v.findViewById(R.id.image_id);
		holder.nickName = (TextView) v.findViewById(R.id.nick_name);
		holder.gender = (ImageView) v.findViewById(R.id.gender);
		holder.signture = (TextView) v.findViewById(R.id.signture);
		holder.attention = (TextView) v.findViewById(R.id.has_attention);
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
		// imageLoad.displayImage(imageUrl, holder.headImage, headOptions);
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

		holder.attention.setVisibility(View.VISIBLE);
		String attentent = data.isAttention;
		if ("0".equals(attentent)) {
			holder.attention.setText("已关注");
		} else if ("1".equals(attentent)) {
			holder.attention.setText("互相关注");
		}

		holder.itemView.setTag(data.memberId);
	}

	public void onClick(View v) {
		switch (v.getId()) {
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
