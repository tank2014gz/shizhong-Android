package com.shizhong.view.ui.base.utils;

import com.shizhong.view.ui.base.view.URLSpanNoUnderline;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yuliyan on 16/1/27.
 */
public class TextViewSpannerUtils {
	public interface OnClickCallBack {
		public void click();
	}

	public static void handleText(Context context, Spannable text, int start, int to, int color, TextView view,
			final OnClickCallBack click) {
		URLSpanNoUnderline clickableSpan = new URLSpanNoUnderline() {
			@Override
			public void onClick(View widget) {
				if (widget instanceof TextView && click != null) {
					click.click();
				}
			}
		};

		SpannableString sp = new SpannableString(text);
		sp.setSpan(clickableSpan, start, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(sp);
		view.setLinkTextColor(color);
		view.setMovementMethod(LinkMovementMethod.getInstance());
		view.setFocusable(false);
		view.setClickable(false);
		view.setLongClickable(false);

	}

	public static void handleText(Context context, String text, int start, int to, int color, TextView view,
			final OnClickCallBack click) {
		URLSpanNoUnderline clickableSpan = new URLSpanNoUnderline() {
			@Override
			public void onClick(View widget) {
				if (widget instanceof TextView && click != null) {
					click.click();
				}
			}
		};

		SpannableString sp = new SpannableString(text);
		sp.setSpan(clickableSpan, start, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(sp);
		view.setLinkTextColor(color);
		view.setMovementMethod(LinkMovementMethod.getInstance());
		view.setFocusable(false);
		view.setClickable(false);
		view.setLongClickable(false);

	}

}
