package com.shizhong.view.ui.base.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.UpdateAppearance;

public abstract class URLSpanNoUnderline extends ClickableSpan implements UpdateAppearance {

	@Override
	public void updateDrawState(TextPaint ds) {
		super.updateDrawState(ds);
		ds.setUnderlineText(false);
	}
}
