package com.shizhong.view.ui.adapter;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.UIUtils;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class VideoNewCutAdapter extends RecyclerView.Adapter<VideoNewCutAdapter.ViewHolder> {
	/** 数据集 */
	private ArrayList<String> list;
	/** 点击事件 */
	private MyItemClickListener onClickListener;
	private LayoutParams lp;
	private int itemWidth;
	private Context mContext;

	public VideoNewCutAdapter(Context context, ArrayList<String> list) {
		this.list = list;
		itemWidth = UIUtils.getScreenWidthPixels(context) / 8;
		this.mContext = context;
	}

	public MyItemClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(MyItemClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		String url = list.get(position);
		if (!TextUtils.isEmpty(url)) {
			viewHolder.img.setImageURI(Uri.parse("file://"+url));
//			Glide.with(this.mContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//					.into(viewHolder.img);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		// 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
		View view = View.inflate(viewGroup.getContext(), R.layout.item_video_new_cut, null);
		// 创建一个ViewHolder
		ViewHolder holder = new ViewHolder(view, onClickListener);

		return holder;
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

		public SimpleDraweeView img;
		private MyItemClickListener listener;

		public ViewHolder(View itemView, MyItemClickListener listener) {
			super(itemView);
			this.listener = listener;
			img = (SimpleDraweeView) itemView.findViewById(R.id.item_video_new_img);
			lp = (LayoutParams) img.getLayoutParams();
			lp.width = itemWidth;
			lp.height = itemWidth;
			img.setLayoutParams(lp);
			img.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (listener != null) {
				listener.onItemClick(img, getPosition());
			}
		}
	}

	public interface MyItemClickListener {
		public void onItemClick(View view, int position);
	}
}
