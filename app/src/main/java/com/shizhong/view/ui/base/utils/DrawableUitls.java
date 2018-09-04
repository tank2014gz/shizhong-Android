package com.shizhong.view.ui.base.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class DrawableUitls {

	@SuppressWarnings("deprecation")
	public static void setCompoundDrawablesWithIntrinsicBounds(TextView view, int left, int top, int right,
			int bottom) {
		final Resources resources = view.getResources();
		setCompoundDrawablesWithIntrinsicBounds(view, left != 0 ? resources.getDrawable(left) : null,
				top != 0 ? resources.getDrawable(top) : null, right != 0 ? resources.getDrawable(right) : null,
				bottom != 0 ? resources.getDrawable(bottom) : null);
	}

	public static void setCompoundDrawablesWithIntrinsicBounds(TextView view, Drawable left, Drawable top,
			Drawable right, Drawable bottom) {

		if (left != null) {
			left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
		}
		if (right != null) {
			right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
		}
		if (top != null) {
			top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
		}
		if (bottom != null) {
			bottom.setBounds(0, 0, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
		}
		view.setCompoundDrawables(left, top, right, bottom);
	}
}
