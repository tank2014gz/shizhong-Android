package com.shizhong.view.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.DanceClass;

import java.util.List;

/**
 * Created by yuliyan on 15/12/27.
 */
public class DancesClassAdapter extends BaseSZAdapter<DanceClass, DancesClassAdapter.ViewHolder> {

	private GridView.LayoutParams itemLayoutParams;
	private View.OnClickListener mListener;
	private FrameLayout.LayoutParams itemImageParams;

	public DancesClassAdapter(Context context, List<DanceClass> list, View.OnClickListener listener) {
		super(context, list);
		this.mListener = listener;
		int width = (UIUtils.getScreenWidthPixels(context) / 4) - UIUtils.dipToPx(context, 10);
		itemLayoutParams = new GridView.LayoutParams(width, width, Gravity.CENTER_HORIZONTAL);
		itemImageParams = new FrameLayout.LayoutParams(width, width, Gravity.CENTER);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.item_layout_dance_class;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder viewHolder = new ViewHolder();
		v.setLayoutParams(itemLayoutParams);
		viewHolder.mDanceImage = (SimpleDraweeView) v.findViewById(R.id.dance_image_id);
		viewHolder.mDanceImage.setLayoutParams(itemImageParams);
		viewHolder.mDanceName = (TextView) v.findViewById(R.id.dance_name);
		viewHolder.mDanceImage.setOnClickListener(mListener);
		viewHolder.mSelectImage = (ImageView) v.findViewById(R.id.selected);
		viewHolder.mSelectImage .setLayoutParams(itemImageParams);
		viewHolder.mSelectImage.setOnClickListener(mListener);
		viewHolder.mFramLayout = (ImageView) v.findViewById(R.id.fram_layout);
		viewHolder.mFramLayout.setLayoutParams(itemImageParams);
		return viewHolder;
	}

	@Override
	protected void initItem(int posiotion, DanceClass data, ViewHolder holder) {
		String imageUrl = data.fileUrl;
		data.position = posiotion;
		if (TextUtils.isEmpty(imageUrl)) {
			imageUrl = "";
		} else {
			imageUrl = FormatImageURLUtils.formatURL(imageUrl, ContantsActivity.Image.MODLE,
					ContantsActivity.Image.MODLE);
		}
		holder.mDanceImage.setImageURI(Uri.parse(imageUrl));
//		Glide.with(mContext).load(imageUrl).placeholder(R.drawable.sz_head_default).error(R.drawable.sz_head_default)
//				.transform(new GlideCircleTransform(mContext)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.mDanceImage);
		String name = data.categoryName;
		if (TextUtils.isEmpty(name)) {
			name = "舞种";
		}
		holder.mDanceName.setText(name);

		boolean isSelected = data.isSelected;
		if (isSelected) {
			holder.mSelectImage.setVisibility(View.VISIBLE);
		} else {
			holder.mSelectImage.setVisibility(View.GONE);
		}
		holder.mSelectImage.setTag(R.string.app_name, data);
		holder.mDanceImage.setTag(R.string.app_name, data);
	}

	static class ViewHolder {
		public SimpleDraweeView mDanceImage;
		public TextView mDanceName;
		public ImageView mSelectImage;
		public ImageView mFramLayout;

	}

}
