package com.shizhong.view.ui.base.view;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.UIUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {

	private ProgressBar progressbar;

	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progressbar = new ProgressBar(context, null,android.R.attr.progressBarStyleHorizontal);
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, UIUtils.dipToPx(context, 3), 0, 0));
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.progress_bar_drawable2);
		progressbar.setProgressDrawable(drawable);
		addView(progressbar);
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}


	public  void setProgressbarVisibility(boolean visibility){
		if(progressbar!=null){
			if(visibility){
				progressbar.setVisibility(VISIBLE);
			}else {
				progressbar.setVisibility(GONE);
			}
		}
	}

	public  void setProgressbarProgress(int progress){
		if(progressbar!=null){
			progressbar.setProgress(progress);
		}
	}

	@Override
	public void destroy() {
		super.destroy();

	  if(progressbar!=null){
		  removeView(progressbar);
		  progressbar.destroyDrawingCache();
		  progressbar=null;
	  }
		System.gc();
	}
}
