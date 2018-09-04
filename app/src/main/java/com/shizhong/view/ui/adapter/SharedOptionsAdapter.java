package com.shizhong.view.ui.adapter;

import java.util.List;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.ShareItemBean;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

public class SharedOptionsAdapter extends BaseSZAdapter<ShareItemBean, SharedOptionsAdapter.ViewHolder> {

	private AbsListView.LayoutParams mItemLayoutParams;
	private int width;
	private OnClickListener mChoiseListener;
	private int mTextColor;

	public SharedOptionsAdapter(Context context, List<ShareItemBean> list, OnClickListener listener) {
		super(context, list);
		width = (UIUtils.getScreenWidthPixels(context) / 4) - UIUtils.dipToPx(mContext, 20);
		mItemLayoutParams = new AbsListView.LayoutParams(width, width);
		this.mChoiseListener = listener;
	}

	public SharedOptionsAdapter(Context context, List<ShareItemBean> list, OnClickListener listener, int textColor) {
		super(context, list);
		width = (UIUtils.getScreenWidthPixels(context) / 4) - UIUtils.dipToPx(mContext, 20);
		mItemLayoutParams = new AbsListView.LayoutParams(width, width);
		this.mChoiseListener = listener;
		this.mTextColor = textColor;
	}

	class ViewHolder {
		ImageView iconImage;
		TextView iconName;
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_chart_more_option_item;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder viewHolder = new ViewHolder();
		v.setLayoutParams(mItemLayoutParams);
		viewHolder.iconImage = (ImageView) v.findViewById(R.id.options_image);
		viewHolder.iconImage.setOnClickListener(this.mChoiseListener);
		viewHolder.iconName = (TextView) v.findViewById(R.id.options_text);
		viewHolder.iconName.setTextColor(mTextColor);
		return viewHolder;
	}

	@Override
	protected void initItem(int posiotion, ShareItemBean data, ViewHolder holder) {
		if (data != null) {
			int imageId = data.itemImageId;
			holder.iconImage.setImageResource(imageId);

			data.itemPosition = posiotion;
			String iconName = data.itemName;
			holder.iconName.setText(iconName);

			holder.iconImage.setTag(R.string.app_name, data);

		}

	}

}
