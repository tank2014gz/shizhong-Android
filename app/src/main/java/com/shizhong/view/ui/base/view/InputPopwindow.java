package com.shizhong.view.ui.base.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import com.hyphenate.easeui.utils.EaseSmileUtils;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.adapter.ExpressionAdapter;
import com.shizhong.view.ui.adapter.ExpressionPagerAdapter;
import com.shizhong.view.ui.base.utils.InputMethodUtils;
import com.shizhong.view.ui.base.utils.SmileUtils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class InputPopwindow extends PopupWindow implements OnClickListener {

	private Context mContext;
	private View mRootView;
	private View mEditFramLayout;
	private ImageView mOptionEmoj;
	private TextView mSendBtn;
	private PasteEditText mEditView;
	private View emojiIconContainer;// 表情的外部容器
	private ViewPager mEmojViewPager;
	private ArrayList<String> mEmojTextList;
	private InputCallBack mInputCallBack;

	public InputPopwindow(Context context) {
		this(context, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	}

	public InputPopwindow(Context context, int width, int height) {
		this.mContext = context;
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(width);
		setHeight(height);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_input_fram_window, null);
		mRootView.setOnClickListener(this);
		initView(mRootView);
		setContentView(mRootView);
//		this.setAnimationStyle(R.style.dialog_anim_bottom);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		mEditFramLayout.setVisibility(View.INVISIBLE);
		if (mContext != null && mInputCallBack != null && mEditView != null) {
			mInputCallBack.getInputCallBack(mEditView.getText().toString(), false);
		}
		if(mInputCallBack!=null){
			mInputCallBack.hideCallBack();
		}
	}

	public void setInputCallBack(InputCallBack inputCallBack) {
		this.mInputCallBack = inputCallBack;
	}

	private void initView(View mRootView2) {
		mEditFramLayout = mRootView2.findViewById(R.id.edit_layout_window);
		mOptionEmoj = (ImageView) mRootView2.findViewById(R.id.chart_iemo_btn_window);
		mOptionEmoj.setOnClickListener(this);
		mSendBtn = (TextView) mRootView2.findViewById(R.id.btn_send_window);
		mSendBtn.setOnClickListener(this);
		mEditView = (PasteEditText) mRootView2.findViewById(R.id.chart_edit_window);
		mEditView.requestFocusFromTouch();
		mEditView.setFocusable(true);
		mEditView.setOnClickListener(this);
		emojiIconContainer = mRootView2.findViewById(R.id.ll_face_container);
		emojiIconContainer.setVisibility(View.GONE);
		mEmojViewPager = (ViewPager) mRootView2.findViewById(R.id.vPager);
		mEmojTextList = (ArrayList<String>) getExpressionRes(90);
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		View gv3=getGridChildView(3);
		View gv4=getGridChildView(4);
		View gv5=getGridChildView(5);
		views.add(gv1);
		views.add(gv2);
		views.add(gv3);
		views.add(gv4);
		views.add(gv5);
		mEmojViewPager.setAdapter(new ExpressionPagerAdapter(views));
	}

	public void show(View view, String text) {
		mEditFramLayout.setVisibility(View.VISIBLE);
//		this.setAnimationStyle(R.style.dialog_anim_bottom);
		if (view instanceof TextView) {
			String context = ((TextView) view).getText().toString();
			if (TextUtils.isEmpty(context)) {
				mEditView.setText("");
				mEditView.setHint(text);
			} else {
				mEditView.setText(context);
			}
		}
		showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

	public void showEmjView() {
		emojiIconContainer.setVisibility(View.VISIBLE);
	}

	public void hideEmjView() {
		emojiIconContainer.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.root_layout:
			dismiss();
			break;
		case R.id.chart_iemo_btn_window:
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
			} else {
				InputMethodUtils.hide(mContext, v);
				emojiIconContainer.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btn_send_window:
			dismiss();
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
			}
			if (this.mInputCallBack != null) {
				this.mInputCallBack.getInputCallBack(mEditView.getText().toString(), true);
			}
			break;
		case R.id.chart_edit_window:
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
			}
			InputMethodUtils.show(mContext, v);
			break;
		}

	}

	/**
	 * gv2 获取表情的gridview的子view
	 *
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(mContext, R.layout.expression_gridview, null);
		MyGridView gv = (MyGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = mEmojTextList.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(mEmojTextList.subList(20, 40));
		}else if(i==3){
			list.addAll(mEmojTextList.subList(40,60));
		}
		else if(i==4){
			list.addAll(mEmojTextList.subList(60,80));
		}else if(i==5){
			list.addAll(mEmojTextList.subList(80,mEmojTextList.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(mContext, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (mEditFramLayout.getVisibility() == View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							@SuppressWarnings("rawtypes")
							Class clz = Class.forName("com.shizhong.view.ui.base.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditView.append(SmileUtils.getSmiledText(mContext, (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditView.getText())) {

								int selectionStart = mEditView.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditView.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (EaseSmileUtils.containsKey(cs.toString()))
											mEditView.getEditableText().delete(i, selectionStart);
										else
											mEditView.getEditableText().delete(selectionStart - 1, selectionStart);
									} else {
										mEditView.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	private List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int i = 1; i <=getSum; i++) {
			String filename = "ee_" + i;
			reslist.add(filename);
		}
		return reslist;
	}

	public interface InputCallBack {
		public void getInputCallBack(String input, boolean isSend);

		public  void  hideCallBack();
	}

	public void release() {
		// TODO Auto-generated method stub
		if(mEmojTextList!=null){
			mEmojTextList.clear();
			mEmojTextList=null;
		}
		if(mEmojViewPager!=null){
			mEmojViewPager.removeAllViews();
			mEmojViewPager=null;
		}
		
	}



}
