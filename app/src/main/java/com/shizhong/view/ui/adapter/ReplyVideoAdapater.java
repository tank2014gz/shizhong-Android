package com.shizhong.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.net.VideoHttpRequest;
import com.shizhong.view.ui.base.net.VideoHttpRequest.HttpRequestCallBack3;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.base.utils.SmileUtils;
import com.shizhong.view.ui.base.utils.TextViewSpannerUtils;
import com.shizhong.view.ui.base.utils.ToastUtils;
import com.shizhong.view.ui.bean.CommentsVideoBean;
import com.shizhong.view.ui.bean.UserExtendsInfo;
import com.hyphenate.easeui.ContantsActivity;
import java.util.List;

/**
 * Created by yuliyan on 15/12/24.
 */
public class ReplyVideoAdapater extends BaseSZAdapter<CommentsVideoBean, ReplyVideoAdapater.ViewHolder>
		implements View.OnClickListener {

	private ItemCallBack itemCallBack;
	private String login_token;
//	private String memeberId;//发布视频人的id
	private String userId;

	public ReplyVideoAdapater(Context context, List<CommentsVideoBean> list) {
		super(context, list);
		login_token = PrefUtils.getString(context, ContantsActivity.LoginModle.LOGIN_TOKEN, "");
		userId=PrefUtils.getString(context, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_video_detail_item;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.itemView = v.findViewById(R.id.list_item);
		holder.itemView.setOnClickListener(this);
		holder.heandImage = (SimpleDraweeView) v.findViewById(R.id.user_head);
		holder.heandImage.setOnClickListener(this);
		holder.nickName = (TextView) v.findViewById(R.id.user_nickname);
		holder.timeView = (TextView) v.findViewById(R.id.comment_time);
		holder.commentView = (TextView) v.findViewById(R.id.user_message);
		holder.likeView = (TextView) v.findViewById(R.id.like_comment);
		holder.likeView.setOnClickListener(this);
		return holder;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void initItem(int posiotion, final CommentsVideoBean data, ViewHolder holder) {

		data.position = posiotion;
		UserExtendsInfo info = data.memberInfo;
        if(info!=null) {
			String headUrl = info.headerUrl;
			if (TextUtils.isEmpty(headUrl)) {
				headUrl = "";
			} else {
				headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.SMALL,
						ContantsActivity.Image.SMALL);
			}
			holder.heandImage.setImageURI(Uri.parse(headUrl));
			String nickName = info.nickname;
			if (TextUtils.isEmpty(nickName)) {
				nickName = "null";
			}
			holder.nickName.setText(nickName);
		}else{
			holder.heandImage.setImageResource(R.drawable.sz_head_default);
			holder.nickName.setText("null");

		}
		String time = DateUtils.formateVideoCreateTime(data.createTime);
		holder.timeView.setText(time);

		long likeCount = data.likeCount;
		if (likeCount <= 0) {
			likeCount = 0;
		}
		holder.likeView.setText(likeCount + "");

		boolean isLiked = data.isLike == 1;
		if (isLiked) {
			holder.likeView.setSelected(true);
		} else {
			holder.likeView.setSelected(false);
		}

		final String toCommentNickName = data.toMemberNickname;
		String comment = data.comment;
		if (TextUtils.isEmpty(comment)) {
			comment = "";
		}
		String toMemberId=data.toMemberId;
		String commentText = "回复%s: %s";
		if (TextUtils.isEmpty(toCommentNickName)||TextUtils.isEmpty(toMemberId)) {
			commentText = comment;
			holder.commentView.setText(SmileUtils.getSmiledText(mContext, commentText), BufferType.SPANNABLE);
		} else {
//			if (!data.toMemberId.equals(memeberId)) {
				commentText = new String().format(commentText, toCommentNickName, comment);
				TextViewSpannerUtils.handleText(mContext, SmileUtils.getSmiledText(mContext, commentText), 0,
						toCommentNickName.length() + 2, 0xffede02b, holder.commentView,
						new TextViewSpannerUtils.OnClickCallBack() {
							@Override
							public void click() {
								mIntent.setClass(mContext, ActivityMemberInfor.class);
								mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, data.toMemberId);
								((Activity)mContext).startActivityForResult(mIntent,ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
							}
						});
//			} else {
//				holder.commentView.setText(SmileUtils.getSmiledText(mContext, comment), BufferType.SPANNABLE);
//			}
		}
		holder.itemView.setTag(R.string.app_name, data);
		holder.heandImage.setTag(R.string.app_name, data);
		holder.likeView.setTag(R.string.app_name, data);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.user_head:
			CommentsVideoBean data = (CommentsVideoBean) view.getTag(R.string.app_name);
			if (data != null) {
				mIntent.setClass(mContext, ActivityMemberInfor.class);
				mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, data.memberId);
				((Activity) mContext).startActivityForResult(mIntent,ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
			}
			break;
		case R.id.list_item:
			data = (CommentsVideoBean) view.getTag(R.string.app_name);
			if(data!=null) {

				if (data.memberId.equals(userId)) {
					ToastUtils.showShort(mContext, "不能评论自己");
					return;
				}
				if (itemCallBack != null) {
					itemCallBack.callBack(data);
				}
			}
			break;
		case R.id.like_comment:
			
			data = (CommentsVideoBean) view.getTag(R.string.app_name);
			if(data.memberId.equals(userId)){
				ToastUtils.showShort(mContext, "不能给自己点赞");
				return;
			}
			
			
			final int position = data.position;
			String commentId = data.commentId;
			if (data != null) {
				int isLike = data.isLike;
				String type = "";
				if (isLike == 1) {
					type = "0";
					isLike = 0;
				} else if (isLike == 0) {
					type = "1";
					isLike = 1;
				}
				data.isLike = isLike;
				data.likeCount = type.equals("1") ? data.likeCount + 1
						: (data.likeCount - 1 > 0 ? data.likeCount - 1 : 0);
				list.set(position, data);
				notifyDataSetChanged();
				VideoHttpRequest.addLikeReply(mContext, login_token, commentId, type, new HttpRequestCallBack3() {

					@Override
					public void callBackFail() {
						CommentsVideoBean data = list.get(position);
						int isLike = data.isLike;
						String type = "";
						if (isLike == 1) {
							type = "0";
							isLike = 0;
						} else if (isLike == 0) {
							type = "1";
							isLike = 1;
						}
						data.isLike = isLike;
						data.likeCount = type.equals("1") ? data.likeCount + 1
								: (data.likeCount - 1 > 0 ? data.likeCount - 1 : 0);
						list.set(position, data);
						notifyDataSetChanged();
					}
				});
			}
			break;

		}
	}

	public void setItemCallBack(ItemCallBack callBack) {
		itemCallBack = callBack;
	}

	public interface ItemCallBack {
		public void callBack(CommentsVideoBean item);
	}

	static class ViewHolder {
		View itemView;
		SimpleDraweeView heandImage;
		TextView nickName;
		TextView timeView;
		TextView commentView;
		TextView likeView;

	}

}
