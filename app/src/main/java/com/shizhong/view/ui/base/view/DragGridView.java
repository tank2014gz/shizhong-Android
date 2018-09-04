package com.shizhong.view.ui.base.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.BaseGridViewAdapter;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.base.utils.UIUtils;

public class DragGridView extends GridView {
	private static final String TAG = "DragGridView";
	private static final int DRAG_IMG_SHOW = 1;
	private static final int DRAG_IMG_NOT_SHOW = 0;
	private static final String LOG_TAG = "DragGridView";
	private static final float AMP_FACTOR = 1.1f;

	private ImageView dragImageView;
	private WindowManager.LayoutParams dragImageViewParams;
	private WindowManager windowManager;
	private boolean isViewOnDrag = false;

	/** previous dragged over position */
	private int preDraggedOverPositon = AdapterView.INVALID_POSITION;

	public  DragFinishListener mDrageFinishListener;


	public  void setDrageFinishListener(DragFinishListener dragListener){
		this.mDrageFinishListener=dragListener;
	}

	@SuppressWarnings("unused")
	private int downRawX;
	@SuppressWarnings("unused")
	private int downRawY;
	private OnItemLongClickListener onLongClickListener = new OnItemLongClickListener() {

		@Override
		// 长按item开始拖动
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Log.i(TAG, "记录长按item位置" + position);
			// 记录长按item位置
			if(position>0){
			preDraggedOverPositon = position;

			// 获取被长按item的drawing cache
			view.destroyDrawingCache();
			view.setDrawingCacheEnabled(true);
			// 通过被长按item，获取拖动item的bitmap
			Bitmap dragBitmap = Bitmap.createBitmap(view.getDrawingCache());

			// 设置拖动item的参数
			dragImageViewParams.gravity = Gravity.TOP | Gravity.LEFT;
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
				Log.i(TAG, "view point(" + view.getX() + "," + view.getY()
						+ ")");
				dragImageViewParams.x = (int) view.getX();
				dragImageViewParams.y = (int) view.getY()
						+ UIUtils.dipToPx(getContext(), 60);
			} else {
				dragImageViewParams.x = (int) view.getLeft();
				dragImageViewParams.y = (int) view.getTop()
						+ UIUtils.dipToPx(getContext(), 60);
			}
			// 设置拖动item为原item 1.2倍
			dragImageViewParams.width = (int) (AMP_FACTOR * dragBitmap
					.getWidth());
			dragImageViewParams.height = (int) (AMP_FACTOR * dragBitmap
					.getHeight());
			dragImageViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
			dragImageViewParams.format = PixelFormat.TRANSLUCENT;
			dragImageViewParams.windowAnimations = 0;

			// dragImageView为被拖动item的容器，清空上一次的显示
			if ((Integer) dragImageView.getTag() == DRAG_IMG_SHOW) {
				Log.i(TAG, " dragImageView为被拖动item的容器，清空上一次的显示");
				windowManager.removeView(dragImageView);
				dragImageView.setTag(DRAG_IMG_NOT_SHOW);
			}

			// 设置本次被长按的item
			dragImageView.setImageBitmap(dragBitmap);
			Log.i(TAG, " 设置本次被长按的item");
			// 添加拖动item到屏幕
			windowManager.addView(dragImageView, dragImageViewParams);
			dragImageView.setTag(DRAG_IMG_SHOW);
			isViewOnDrag = true;

			// 设置被长按item不显示
			((BaseGridViewAdapter<?>) getAdapter()).hideView(position);
			Log.i(TAG, " 设置被长按item不显示");}
			return true;
		}
	};

	public DragGridView(Context context) {
		super(context);
		initView();
	}

	public DragGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public void initView() {
		setOnItemLongClickListener(onLongClickListener);
		// 初始化显示被拖动item的image view
		dragImageView = new ImageView(getContext());
		dragImageView.setBackgroundResource(R.drawable.white_fram_shape);
		dragImageView.setTag(DRAG_IMG_NOT_SHOW);
		// 初始化用于设置dragImageView的参数对象
		dragImageViewParams = new WindowManager.LayoutParams();
		// 获取窗口管理对象，用于后面向窗口中添加dragImageView
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		// 被按下时记录按下的坐标
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// 获取触摸点相对于屏幕的坐标
			downRawX = (int) ev.getRawX();
			downRawY = (int) ev.getRawY();
		}
		// dragImageView处于被拖动时，更新dragImageView位置
		else if ((ev.getAction() == MotionEvent.ACTION_MOVE)
				&& (isViewOnDrag == true)) {
			Log.i(LOG_TAG, "" + ev.getRawX() + " " + ev.getRawY());
			// 设置触摸点为dragImageView中心
			dragImageViewParams.x = (int) (ev.getRawX() - dragImageView
					.getWidth() / 2);
			dragImageViewParams.y = (int) (ev.getRawY() - dragImageView
					.getHeight() / 2);
			// 更新窗口显示
			windowManager.updateViewLayout(dragImageView, dragImageViewParams);
			// 获取当前触摸点的item position
			int currDraggedPosition = pointToPosition((int) ev.getX(),
					(int) ev.getY());
			LogUtils.e("shizhong","currDraggedPosition:"+currDraggedPosition);
			// 如果当前停留位置item不等于上次停留位置的item，交换本次和上次停留的item
			if ((currDraggedPosition != AdapterView.INVALID_POSITION)
					&& (currDraggedPosition != preDraggedOverPositon)&&currDraggedPosition>0) {
				((BaseGridViewAdapter<?>) getAdapter()).swapView(
						preDraggedOverPositon, currDraggedPosition);
				preDraggedOverPositon = currDraggedPosition;
				if(mDrageFinishListener!=null){
					mDrageFinishListener.dragFinish();
				}
			}
		}
		// 释放dragImageView
		else if ((ev.getAction() == MotionEvent.ACTION_UP)
				&& (isViewOnDrag == true)) {
			((BaseGridViewAdapter<?>) getAdapter()).showHideView();
			if ((Integer) (dragImageView.getTag()) == DRAG_IMG_SHOW) {
				windowManager.removeView(dragImageView);
				dragImageView.setTag(DRAG_IMG_NOT_SHOW);

			}
			isViewOnDrag = false;
		}
		return super.onTouchEvent(ev);
	}

	public interface  DragFinishListener{
		public  void dragFinish();
	}

}