package com.shizhong.view.ui.adapter;

import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.db.MessageDBManager;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.base.utils.TextViewSpannerUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.bean.MessageInfoExtraBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NewFriendsAdapter extends BaseSZAdapter<MessageInfoExtraBean, NewFriendsAdapter.ViewHodler>{
		//implements OnClickListener{, OnLongClickListener {

	private String format_attent = "%s %s 关注了你";
//	private int delectId;
	private int position;
	public NewFriendsAdapter(Context context, List<MessageInfoExtraBean> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	class ViewHodler {
		View itemView;
		SimpleDraweeView headImage;
		TextView nickNameView;

	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.item_message_new_friends_layout;
	}

	@Override
	protected ViewHodler getHodler(View v) {
		ViewHodler holder = new ViewHodler();
		holder.headImage = (SimpleDraweeView) v.findViewById(R.id.head_image);
		holder.itemView = v.findViewById(R.id.item_view);
		holder.nickNameView = (TextView) v.findViewById(R.id.nick_name);
		return holder;
	}

	@Override
	protected void initItem(int posiotion, MessageInfoExtraBean data, ViewHodler holder) {
		String headUrl = data.fromHeader;
		if (TextUtils.isEmpty(headUrl)) {
			headUrl = "";
		} else {
			headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		holder.headImage.setImageURI(Uri.parse(headUrl));

		String nick_name = data.fromNickname;
		if (TextUtils.isEmpty(nick_name)) {
			nick_name = "匿名名称";
		}
		holder.nickNameView.setText(nick_name);

		String time = DateUtils.formateVideoCreateTime(data.operateTime);
		String content=String.format(format_attent,nick_name,time);
		TextViewSpannerUtils.handleText(mContext, content, 0, nick_name.length(), 0xffede02b, holder.nickNameView,
				null);

	}

}
