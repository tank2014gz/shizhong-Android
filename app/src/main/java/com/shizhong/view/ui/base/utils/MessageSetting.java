package com.shizhong.view.ui.base.utils;

import com.hyphenate.easeui.utils.PrefUtils;

import android.content.Context;

public class MessageSetting {

	private Context mContext;
	private final String IS_ACCEPT_FANS = "is accept fans message";
	private final String IS_ACCEPT_COMMEN = "is accept comment message";
	private final String IS_ACCEPT_LILE = "is accept lile message";


	public MessageSetting(Context context) {
		this.mContext = context;
	}


	public void setMessageNewFans(boolean isAccept) {
		PrefUtils.putBoolean(mContext, IS_ACCEPT_FANS, isAccept);
	}

	public boolean isMessageNewFans() {
		return PrefUtils.getBoolean(mContext, IS_ACCEPT_FANS, true);
	}

	public void setMessageComment(boolean isAccept) {
		PrefUtils.putBoolean(mContext, IS_ACCEPT_COMMEN, isAccept);
	}

	public boolean isMessageComment() {
		return PrefUtils.getBoolean(mContext, IS_ACCEPT_COMMEN, true);
	}

	public void setMessageLike(boolean isAccept) {
		PrefUtils.putBoolean(mContext, IS_ACCEPT_LILE, isAccept);
	}

	public boolean isMessageLike() {
		return PrefUtils.getBoolean(mContext, IS_ACCEPT_LILE, true);
	}
}
