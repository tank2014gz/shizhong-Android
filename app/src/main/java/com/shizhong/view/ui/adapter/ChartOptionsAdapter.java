package com.shizhong.view.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.ChatOptionItem;

import java.util.List;

/**
 * Created by yuliyan on 15/12/30.
 */
public class ChartOptionsAdapter extends BaseSZAdapter<ChatOptionItem, ChartOptionsAdapter.ViewHolder> {

	private AbsListView.LayoutParams mItemLayoutParams;
	private int width;

	public ChartOptionsAdapter(Context context, List<ChatOptionItem> list) {
		super(context, list);
		width = (UIUtils.getScreenWidthPixels(context) / 4) - UIUtils.dipToPx(mContext, 20);
		mItemLayoutParams = new AbsListView.LayoutParams(width, width);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_chart_more_option_item;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder viewHolder = new ViewHolder();
		v.setLayoutParams(mItemLayoutParams);
		viewHolder.optionImage = (ImageView) v.findViewById(R.id.options_image);
		viewHolder.optionText = (TextView) v.findViewById(R.id.options_text);
		return viewHolder;
	}

	@Override
	protected void initItem(int posiotion, ChatOptionItem data, ViewHolder holder) {
		if (data != null) {
			holder.optionImage.setImageResource(data.optionImageId);
			holder.optionText.setText(data.optionStringId);
		}
	}

	class ViewHolder {
		ImageView optionImage;
		TextView optionText;

	}

}
