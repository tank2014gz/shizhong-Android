package com.shizhong.view.ui.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.VideoPlayerActivity;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.db.MessageDBManager;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.base.utils.TextViewSpannerUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.base.view.NoFocationTextView;
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

public class MessageReplyAdapger extends BaseSZAdapter<MessageInfoExtraBean, MessageReplyAdapger.ViewHodler>//{
	implements OnClickListener{//, OnLongClickListener {

	private String format_video_reply = "%s %s 评论了你的视频";
	private String format_commit_reply = "%s %s 回复了你的评论";
	private int delectId;
	private int position;
	public MessageReplyAdapger(Context context, List<MessageInfoExtraBean> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.head_image:
				MessageInfoExtraBean data=(MessageInfoExtraBean)view.getTag();
				if(data!=null){
						mIntent.setClass(mContext, ActivityMemberInfor.class);
					    int next_action=ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO;
						mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, data.fromId);
						((Activity)mContext).startActivityForResult(mIntent,next_action);

				}
				break;
		}
	}

	class ViewHodler {
		View messageItemView;
		SimpleDraweeView messageHead;
		SimpleDraweeView messageCover;
		NoFocationTextView messageNickName;
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.item_message_like_layout;
	}

	@Override
	protected ViewHodler getHodler(View v) {
		ViewHodler holder = new ViewHodler();
		holder.messageItemView = v.findViewById(R.id.item_view);
		holder.messageHead = (SimpleDraweeView) v.findViewById(R.id.head_image);
		holder.messageHead.setOnClickListener(this);
		holder.messageNickName = (NoFocationTextView) v.findViewById(R.id.nick_name);
		holder.messageCover = (SimpleDraweeView) v.findViewById(R.id.video_cover);
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
		holder.messageHead.setImageURI(Uri.parse(headUrl));
		// imageLoad.displayImage(headUrl, holder.messageHead, headOptions);
//		Glide.with(mContext).load(headUrl).placeholder(R.drawable.sz_head_default).error(R.drawable.sz_head_default)
//				.transform(new GlideCircleTransform(mContext)).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
//				.into(holder.messageHead);
		String nickName = data.fromNickname;
		if (TextUtils.isEmpty(nickName)) {
			nickName = "匿名舞者";
		}

		try {
			JSONObject contentJSON = new JSONObject(data.content);
			String coverUrl = contentJSON.getString("coverUrl");
			if (TextUtils.isEmpty(coverUrl)) {
				coverUrl = "";
			} else {
				data.videoConver = coverUrl;
				coverUrl = FormatImageURLUtils.formatURL(coverUrl, ContantsActivity.Image.SMALL,
						ContantsActivity.Image.SMALL);
			}
			holder.messageCover.setImageURI(Uri.parse(coverUrl));
//			Glide.with(mContext).load(coverUrl).placeholder(R.drawable.sz_activity_default)
//					.error(R.drawable.sz_activity_default).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
//					.crossFade().into();
			String videoId = contentJSON.getString("id");
			data.videoId = videoId;
			String time = DateUtils.formateVideoCreateTime(data.operateTime);
			String replyType = contentJSON.getString("commentType");
			String content = "";
			if (replyType.equals("0")) {
				content = String.format(format_video_reply, nickName, time);
			} else if (replyType.equals("1")) {
				content = String.format(format_commit_reply, nickName, time);
			}
			TextViewSpannerUtils.handleText(mContext, content, 0, nickName.length(), 0xffede02b, holder.messageNickName,
					null);
			// holder.messageContent.setText(time);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.messageHead.setTag(data);
	}

}
