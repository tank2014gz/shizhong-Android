package com.shizhong.view.ui.base.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.shizhong.view.ui.bean.ContantsChart;

/**
 * Created by yuliyan on 15/12/25. 自定义的textview，用来处理复制粘贴的消息
 */
public class PasteEditText extends EditText {

	public PasteEditText(Context context) {
		super(context);
	}

	public PasteEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PasteEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTextContextMenuItem(int id) {
		if (id == android.R.id.paste) {
			ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
			if (clip == null || clip.getText() == null) {
				return false;
			}
			String text = clip.getText().toString();
			if (text.startsWith(ContantsChart.ChartContent.CHART_IMAGE_COPY)) {
				// intent.setDataAndType(Uri.fromFile(new
				// File("/sdcard/mn1.jpg")), "image/*");
				text = text.replace(ContantsChart.ChartContent.CHART_IMAGE_COPY, "");
				// Intent intent = new Intent(context,
				// AlertDialogActivity.class);
				// String str =
				// context.getResources().getString(R.string.Send_the_following_pictures);
				// intent.putExtra("title", str);
				// intent.putExtra("forwardImage", text);
				// intent.putExtra("cancel", true);
				// ((Activity) context).startActivityForResult(intent,
				// ContantsChart.ChartAction.REQUEST_CODE_COPY_AND_PASTE);
				// clip.setText("");
			}
		}
		return super.onTextContextMenuItem(id);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		if (!TextUtils.isEmpty(text) && text.toString().startsWith(ContantsChart.ChartContent.CHART_IMAGE_COPY)) {
			setText("");
		} 
		// else if (!TextUtils.isEmpty(text)) {
		// setText(SmileUtils.getSmiledText(getContext(), text),
		// BufferType.SPANNABLE);
		// }
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}
}
