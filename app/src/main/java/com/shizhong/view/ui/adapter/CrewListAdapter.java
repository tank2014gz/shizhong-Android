package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityClubDetail;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.bean.Clubbean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import java.util.List;

/**
 * Created by yuliyan on 16/1/28.
 */
public class CrewListAdapter extends BaseSZAdapter<Clubbean, CrewListAdapter.ViewHolder>
		implements View.OnClickListener {

	public CrewListAdapter(Context context, List<Clubbean> list) {
		super(context, list);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.item_crew_layout;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.clubItem = v.findViewById(R.id.club_item);
		holder.clubItem.setOnClickListener(this);
		holder.clubImage = (SimpleDraweeView) v.findViewById(R.id.club_image);
		holder.clubName = (TextView) v.findViewById(R.id.club_name);
		holder.clubDes = (TextView) v.findViewById(R.id.club_des);
		holder.clubPlace = (TextView) v.findViewById(R.id.club_place);
		return holder;
	}

	@Override
	protected void initItem(int posiotion, Clubbean data, ViewHolder holder) {
		String clubUrl = data.logoUrl;
		if (TextUtils.isEmpty(clubUrl)) {
			clubUrl = "";
		} else {
			clubUrl = FormatImageURLUtils.formatURL(clubUrl, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		holder.clubImage.setImageURI(Uri.parse(clubUrl));
		// imageLoad.displayImage(clubUrl, holder.clubImage, options);
//		Glide.with(mContext).load(clubUrl).placeholder(R.drawable.sz_head_default).error(R.drawable.sz_head_default)
//				.transform(new GlideCircleTransform(mContext)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.clubImage);

		String clubName = data.clubName;
		if (TextUtils.isEmpty(clubName)) {
			clubName = "官方舞社";
		}
		holder.clubName.setText(clubName);

		String clubDes = data.description;
		if (TextUtils.isEmpty(clubDes)) {
			clubDes = "";
		}
		holder.clubDes.setText(clubDes);

		String clubAddress = data.address.trim();
		if (TextUtils.isEmpty(clubAddress)) {
			clubAddress = "地址：";
		} else {
			clubAddress = "地址：" + clubAddress;
		}
		holder.clubPlace.setText(clubAddress);
		holder.clubItem.setTag(data);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.club_item:
			Clubbean clubbean = (Clubbean) view.getTag();
			if (clubbean != null) {
				String clubId = clubbean.clubId;
				String clubName = clubbean.clubName;
				mIntent.setClass(mContext, ActivityClubDetail.class);
				mIntent.putExtra(ContantsActivity.Club.CLUB_ID, clubId);
				mIntent.putExtra(ContantsActivity.Club.CLUB_NAME, clubName);
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_CREW_DETAIL);
			}
			break;
		}
	}

	class ViewHolder {
		SimpleDraweeView clubImage;
		TextView clubName;
		TextView clubDes;
		TextView clubPlace;
		View clubItem;

	}
}
