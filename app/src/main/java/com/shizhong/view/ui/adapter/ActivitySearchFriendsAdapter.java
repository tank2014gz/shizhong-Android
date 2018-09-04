package com.shizhong.view.ui.adapter;

import java.util.List;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.PlaceDBManager;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.shizhong.view.ui.base.net.VideoHttpRequest.HttpRequestCallBack2;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.UserExtendsInfo;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivitySearchFriendsAdapter
		extends BaseSZAdapter<UserExtendsInfo, ActivitySearchFriendsAdapter.ViewHolder> implements OnClickListener {
	private String login_token;
	private String user_id;
	private PlaceDBManager mPlaceDBManager;

	static class ViewHolder {
		public View itemView;
		public SimpleDraweeView headphoto_view;
		public TextView nickname_view;
		public ImageView sex_view;
		public TextView address_view;
		public TextView fanscount_view;
		public TextView hasAttent_view;
		public ImageView addAttent_view;

	}

	public ActivitySearchFriendsAdapter(Context context, List<UserExtendsInfo> list) {
		super(context, list);
		login_token = PrefUtils.getString(context, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		user_id = PrefUtils.getString(context, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		mPlaceDBManager = new PlaceDBManager(context);
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.item_layout_search_friends;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.itemView = v.findViewById(R.id.persom_item);
		holder.itemView.setOnClickListener(this);
		holder.headphoto_view = (SimpleDraweeView) v.findViewById(R.id.person_head);
		holder.nickname_view = (TextView) v.findViewById(R.id.person_nickname);
		holder.sex_view = (ImageView) v.findViewById(R.id.person_sex);
		holder.address_view = (TextView) v.findViewById(R.id.person_des);
		
		holder.fanscount_view = (TextView) v.findViewById(R.id.person_fans_count);
		holder.hasAttent_view = (TextView) v.findViewById(R.id.has_attent);
		holder.addAttent_view = (ImageView) v.findViewById(R.id.person_attention);
		holder.addAttent_view.setOnClickListener(this);
		return holder;
	}

	@Override
	protected void initItem(int posiotion, UserExtendsInfo data, ViewHolder holder) {
		String headUrl = data.headerUrl;
		if (TextUtils.isEmpty(headUrl)) {
			headUrl = "";
		} else {
			headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		data.postion = posiotion;
		holder.headphoto_view.setImageURI(Uri.parse(headUrl));
//		Glide.with(mContext).load(headUrl).placeholder(R.drawable.sz_head_default).error(R.drawable.sz_head_default)
//				.transform(new GlideCircleTransform(mContext)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.headphoto_view);

		String nickName = data.nickname;
		if (TextUtils.isEmpty(nickName)) {
			nickName = "匿名舞者";
		}
		holder.nickname_view.setText(nickName);
		String memeberId = data.memberId;
		if (TextUtils.isEmpty(memeberId)) {
			LogUtils.e("nearby person", "附近用户ID为空");
		}
		if (!TextUtils.isEmpty(memeberId)) {
			if (memeberId.equals(user_id)) {
				holder.addAttent_view.setVisibility(View.INVISIBLE);
			} else {
				String isAttent = data.isAttention;
				if (isAttent.equals("0")) {
					holder.hasAttent_view.setVisibility(View.GONE);
					holder.addAttent_view.setVisibility(View.VISIBLE);
				} else if (isAttent.equals("1")) {
					holder.hasAttent_view.setVisibility(View.VISIBLE);
					holder.addAttent_view.setVisibility(View.GONE);
				}
			}
		}

		boolean isMen = "1".equals(data.sex);
		if (isMen) {
			holder.sex_view.setImageResource(R.drawable.man_icon);
		} else {
			holder.sex_view.setImageResource(R.drawable.girl_icon);
		}

		String cityId = data.cityId;
		String provinceId = data.provinceId;
		String districtId = data.districtId;

		String city = mPlaceDBManager.getCity(cityId);
		String provice = mPlaceDBManager.getProvice(provinceId);
		String zone = mPlaceDBManager.getZone(districtId);
		if (TextUtils.isEmpty(city) && TextUtils.isEmpty(provice) && TextUtils.isEmpty(zone)) {
			holder.address_view.setText("舞者位置信息不详");
		} else {
			StringBuilder address = new StringBuilder();
			if (provice.contains(city)) {
				address.append(provice);
			} else {
				address.append(provice).append(" ").append(city);
			}
			if (!TextUtils.isEmpty(zone)) {
				address.append(" ").append(zone);
			}
			holder.address_view.setText(address.toString());
		}
		holder.addAttent_view.setTag(data);
		holder.itemView.setTag(data.memberId);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.persom_item:
			String memberId = (String) v.getTag();
			if (!TextUtils.isEmpty(memberId)) {
				mIntent.setClass(mContext, ActivityMemberInfor.class);
				mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
				((Activity) mContext).overridePendingTransition(R.anim.dialog_right_in_anim,
						R.anim.dialog_right_out_anim);
			}
			break;
		case R.id.person_attention:
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
		}

	}

}
