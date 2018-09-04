package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.shizhong.view.ui.base.net.VideoHttpRequest.HttpRequestCallBack2;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.MathUtils;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.hyphenate.easeui.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import java.util.List;

/**
 * Created by yuliyan on 16/1/28.
 */
public class NearByPeopleAdapter extends BaseSZAdapter<UserExtendsInfo, NearByPeopleAdapter.ViewHolder>
		implements View.OnClickListener {

	private double user_lat;
	private double user_lng;
	private String login_token;
	private String user_id;

	public NearByPeopleAdapter(Context context, List<UserExtendsInfo> list) {
		super(context, list);
		login_token = PrefUtils.getString(mContext, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		user_id = PrefUtils.getString(context, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
	}

	public void setLoaction(String user_lng, String user_lat) {
		this.user_lng = Double.valueOf(user_lng);
		this.user_lat = Double.valueOf(user_lat);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.item_layout_nearby_people;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.itemView = v.findViewById(R.id.persom_item);
		holder.itemView.setOnClickListener(this);
		holder.headImage = (SimpleDraweeView) v.findViewById(R.id.person_head);
		holder.nickName = (TextView) v.findViewById(R.id.person_nickname);
		holder.personDes = (TextView) v.findViewById(R.id.person_des);
		holder.personDis = (TextView) v.findViewById(R.id.person_distance);
		holder.personIsAtt = (ImageView) v.findViewById(R.id.person_attention);
		holder.personIsAtt.setOnClickListener(this);
		holder.personSex = (ImageView) v.findViewById(R.id.person_sex);
		holder.hasAttentText = (TextView) v.findViewById(R.id.has_attent);
		return holder;
	}

	@SuppressWarnings("static-access")
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
		holder.headImage.setImageURI(Uri.parse(headUrl));
		// imageLoad.displayImage(headUrl, holder.headImage, headOptions);
//		Glide.with(mContext).load(headUrl).placeholder(R.drawable.sz_head_default).error(R.drawable.sz_head_default)
//				.transform(new GlideCircleTransform(mContext)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.headImage);

		String nickName = data.nickname;
		if (TextUtils.isEmpty(nickName)) {
			nickName = "匿名舞者";
		}
		holder.nickName.setText(nickName);

		String perSingtuer = data.signature;
		if (TextUtils.isEmpty(perSingtuer)) {
			perSingtuer = "说句牛逼的话让大家记住你！";
		}
		holder.personDes.setText(perSingtuer);

		double member_lat = Double.valueOf(data.lat);
		double member_lng = Double.valueOf(data.lng);
		int distance = (int) MathUtils.Distance(user_lng, user_lat, member_lng, member_lat);
		String dis = "";
		if (distance >= 1000) {
			dis = String.format("%.2fk", ((float) distance / 1000));
		} else {
			dis = distance + "";
		}
		String disDes = new String().format("距离你%s米", dis);
		holder.personDis.setText(disDes);
		String memeberId = data.memberId;
		if (TextUtils.isEmpty(memeberId)) {
			LogUtils.e("nearby person", "附近用户ID为空");
		}
		if (!TextUtils.isEmpty(memeberId)) {
			if (memeberId.equals(user_id)) {
				holder.personIsAtt.setVisibility(View.INVISIBLE);
			} else {
				String isAttent = data.isAttention;
				if (isAttent.equals("0")) {
					holder.hasAttentText.setVisibility(View.GONE);
					holder.personIsAtt.setVisibility(View.VISIBLE);
				} else if (isAttent.equals("1")) {
					holder.hasAttentText.setVisibility(View.VISIBLE);
					holder.personIsAtt.setVisibility(View.GONE);
				}
			}
		}

		boolean isMen = "1".equals(data.sex);
		if (isMen) {
			holder.personSex.setImageResource(R.drawable.man_icon);
		} else {
			holder.personSex.setImageResource(R.drawable.girl_icon);
		}
		holder.personIsAtt.setTag(R.string.app_name, data);
		holder.itemView.setTag(R.string.app_name, data.memberId);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.persom_item:
			String memberId = (String) view.getTag(R.string.app_name);
			if (!TextUtils.isEmpty(memberId)) {
				mIntent.setClass(mContext, ActivityMemberInfor.class);
				mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, memberId);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
				((Activity) mContext).overridePendingTransition(R.anim.dialog_right_in_anim,
						R.anim.dialog_right_out_anim);
			}
			break;
		case R.id.person_attention:
			UserExtendsInfo info = (UserExtendsInfo) view.getTag(R.string.app_name);
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

	class ViewHolder {
		View itemView;
		SimpleDraweeView headImage;
		TextView nickName;
		TextView personDes;
		TextView personDis;
		ImageView personIsAtt;
		ImageView personSex;
		TextView hasAttentText;

	}
}
