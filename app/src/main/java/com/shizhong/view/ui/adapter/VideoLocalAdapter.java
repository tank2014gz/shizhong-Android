package com.shizhong.view.ui.adapter;

import java.util.List;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.base.utils.UIUtils;
import com.shizhong.view.ui.bean.VideoLocalBean;
import com.shizhong.view.ui.video.VideoCutOutActivity;
import com.hyphenate.easeui.ContantsActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.LruCache;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class VideoLocalAdapter extends BaseSZAdapter<VideoLocalBean, VideoLocalAdapter.ViewHodler>
		implements OnClickListener {

	private int width;
	private Handler mHandler = new Handler();
	private final int CODE_IMPORT_VIDEO = 0x900;
	private String topic_name;
	private int topic_page;
	private String topic_id;
	private PopupWindow mPopWindow;
	private LruCache<String, Bitmap> mImaageBitmap;
	private ACache mACache = null;

	public VideoLocalAdapter(Context context, List<VideoLocalBean> list) {
		super(context, list);
		width = (UIUtils.getScreenWidthPixels(mContext) / 4) - UIUtils.dipToPx(context, 1);
		int maxSize = (int) (Runtime.getRuntime().maxMemory() / 20);
		mImaageBitmap = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// TODO Auto-generated method stub
				return value.getByteCount() / 1024;
			}
		};
		mACache = ACache.get(mContext);
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mImaageBitmap.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mImaageBitmap.get(key);
	}

	public void setTopicInfo(String topic_name,String topic_id ,int topic_page) {
		this.topic_name = topic_name;
		this.topic_page = topic_page;
		this.topic_id=topic_id;
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.item_video_local_layout;
	}

	@Override
	protected ViewHodler getHodler(View v) {
		ViewHodler holder = new ViewHodler();
		holder.itemView = v.findViewById(R.id.item_layout);
		holder.videoImage = (ImageView) v.findViewById(R.id.video_image);
		LayoutParams layoutParams = (LayoutParams) holder.videoImage.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = width;
		holder.videoImage.setLayoutParams(layoutParams);
		holder.itemView.setOnClickListener(this);
		holder.videoDuring = (TextView) v.findViewById(R.id.during);
		return holder;
	}

	@Override
	protected void initItem(int posiotion, VideoLocalBean data, final ViewHodler holder) {
		if (data != null) {
			String path = data.path;
			holder.videoDuring.setText(data.durationDes);
			holder.videoImage.setImageResource(R.drawable.sz_activity_default);
			getVideoThumbnail(path, width, width, Thumbnails.MICRO_KIND, new ImageLoader() {

				@Override
				public void callback(final Bitmap bitmap) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							holder.videoImage.setImageBitmap(bitmap);
						}
					});
				}
			},holder.videoImage);

			holder.itemView.setTag(R.string.app_name, data);
		}
	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images. Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	private void getVideoThumbnail(final String videoPath, final int width, final int height, final int kind,
			final ImageLoader loader,ImageView imageView) {
		Bitmap bitamp = getBitmapFromMemCache(videoPath);
		if (bitamp != null) {
			loader.callback(bitamp);
			return;
		}
		imageView.setImageResource(R.drawable.sz_activity_default);
		bitamp = mACache.getAsBitmap(videoPath);
		if (bitamp != null) {
			addBitmapToMemoryCache(videoPath, bitamp);
			loader.callback(bitamp);
		}

		new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				mACache.put(videoPath, bitmap);
				loader.callback(bitmap);
			}
		}.start();

	}

	public interface ImageLoader {
		public void callback(Bitmap bitmap);
	}

	class ViewHodler {
		View itemView;
		ImageView videoImage;
		TextView videoDuring;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_layout:

			VideoLocalBean tag = (VideoLocalBean) v.getTag(R.string.app_name);
			long during = tag.during;
			if (during < 5000) {
				ToastUtils.showShort(mContext, "视频长度不能低于5秒");
				return;
			}
			if (during > 10 * 60 * 1000) {
				ToastUtils.showShort(mContext, "视频长度超过10分钟");
				return;
			}

			if (this.mPopWindow != null && this.mPopWindow.isShowing()) {
				mPopWindow.dismiss();
			}

			String path = tag.path;
			mIntent.setClass(mContext, VideoCutOutActivity.class);
			mIntent.putExtra(ContantsActivity.Video.VIDEO_PATH, path);
			mIntent.putExtra(ContantsActivity.Topic.TOPIC_NAME, topic_name);
			mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID,topic_id);
			mIntent.putExtra(ContantsActivity.Topic.TOPIC_PAGE, topic_page);
			((Activity) mContext).startActivityForResult(mIntent, CODE_IMPORT_VIDEO);

			break;

		default:
			break;
		}
	}

	public void onActivityResult(int req, int res, Intent data) {
		switch (req) {
		case CODE_IMPORT_VIDEO:
			if (res == Activity.RESULT_OK) {
				((Activity) mContext).setResult(Activity.RESULT_OK, data);
				((Activity) mContext).finish();
			} else {

			}
			break;

		default:
			break;
		}

	}

	public void setWindow(PopupWindow choiseVideoWindow) {
		// TODO Auto-generated method stub
		this.mPopWindow = choiseVideoWindow;
	}
}
