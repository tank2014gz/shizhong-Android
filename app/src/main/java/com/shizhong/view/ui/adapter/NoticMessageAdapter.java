package com.shizhong.view.ui.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.shizhong.view.ui.ActivityClubDetail;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.ActivityNewsWebContent;
import com.shizhong.view.ui.ActivityTopicDetail;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.VideoPlayerActivity;
import com.shizhong.view.ui.base.BaseSZAdapter;
import com.shizhong.view.ui.base.db.MessageDBManager;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.hyphenate.easeui.utils.DateUtils;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.PrefUtils;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.bean.MessageInfoExtraBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ContantsActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticMessageAdapter extends BaseSZAdapter<MessageInfoExtraBean, NoticMessageAdapter.ViewHolder>
		implements OnClickListener{//}, OnLongClickListener {

	public NoticMessageAdapter(Context context, List<MessageInfoExtraBean> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	private int next_action=-1;

	class ViewHolder {
		View itemView;
		ImageView messageIcon;
		TextView messageTag;
		TextView messageCommnet;
		TextView messageTime;
		TextView messageMore;

	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.item_message_notic_layout;
	}

	@Override
	protected ViewHolder getHodler(View v) {
		ViewHolder holder = new ViewHolder();
		holder.messageIcon = (ImageView) v.findViewById(R.id.head_image);
		holder.itemView = v.findViewById(R.id.item_view);
//		holder.itemView.setOnLongClickListener(this);
		holder.messageTag = (TextView) v.findViewById(R.id.messge_tag);
		holder.messageMore = (TextView) v.findViewById(R.id.message_more);
		holder.messageCommnet = (TextView) v.findViewById(R.id.messge_content);
		holder.messageTime = (TextView) v.findViewById(R.id.time);
		return holder;
	}

	@Override
	protected void initItem(int posiotion, MessageInfoExtraBean data, ViewHolder holder) {

		String targetType = data.targetType;
		data.position = posiotion;
		int tagImageId = 0;
		String tagName = null;
		String messageComment = null;
		String time = DateUtils.formateVideoCreateTime(data.operateTime);

		if (targetType.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_PERSON)) {// 推个人
			tagImageId = R.drawable.recomen_person_icon;
			String content = data.content;
			String nickName = "";
			String gender = "0";
			String reasion = "";
			try {
				JSONObject contentJSON = new JSONObject(content);
				nickName = contentJSON.getString("nickName") + "  ";
				gender = contentJSON.getString("sex");
				reasion = data.message;//

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bitmap genderBitmap = null;
			if (gender.equals("0")) {
				genderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl_icon);
			} else {
				genderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.man_icon);

			}
			messageComment = nickName + gender + "  " + reasion;
			int startPoi = nickName.length();
			tagName = "推荐关注";
			ImageSpan imgSpan = new ImageSpan(mContext, genderBitmap);
			SpannableString spanString = new SpannableString(messageComment);
			spanString.setSpan(imgSpan, startPoi, startPoi + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.messageCommnet.setText(spanString);
			holder.messageMore.setVisibility(View.VISIBLE);
			holder.messageIcon.setImageResource(tagImageId);
			if(holder.messageMore!=null){
				holder.messageMore.setOnClickListener(null);
			}
		} else if (targetType.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_VIDEO)) {// 视频
			tagImageId = R.drawable.hot_video_icon;
			messageComment = data.description;
			tagName = "热门视频";
			holder.messageCommnet.setText(messageComment);
			holder.messageMore.setVisibility(View.VISIBLE);
			holder.messageIcon.setImageResource(tagImageId);
			if(holder.messageMore!=null){
				holder.messageMore.setOnClickListener(null);
			}
		} else if (targetType.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_TOPIC)) {// 话题
			tagImageId = R.drawable.recomen_activity_icon;
			tagName = "推荐话题";
			messageComment = data.description;
			holder.messageCommnet.setText(messageComment);
			holder.messageMore.setVisibility(View.VISIBLE);
			holder.messageIcon.setImageResource(tagImageId);
			if(holder.messageMore!=null){
				holder.messageMore.setOnClickListener(this);
			}
		} else if (targetType.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_NEWS)) {// 活动
			tagImageId = R.drawable.recomen_activity_icon;
			messageComment = data.description;
			tagName = "推荐资讯";
			holder.messageCommnet.setText(messageComment);
			holder.messageMore.setVisibility(View.VISIBLE);
			holder.messageIcon.setImageResource(tagImageId);
		} else if (targetType.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_CLUB)) {// 舞社
			tagImageId = R.drawable.recomen_activity_icon;
			tagName = "推荐舞社";
			messageComment = data.description;
			holder.messageCommnet.setText(messageComment);
			holder.messageMore.setVisibility(View.VISIBLE);
			holder.messageIcon.setImageResource(tagImageId);
			if(holder.messageMore!=null){
				holder.messageMore.setOnClickListener(this);
			}
		} else {
			// 暂时明天
			messageComment = data.description;
			holder.messageCommnet.setText(messageComment);
			tagName = "失重提示";
			holder.messageMore.setVisibility(View.GONE);
			String headrUrl = data.fromHeader;
			headrUrl = FormatImageURLUtils.formatURL(headrUrl, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
			Glide.with(mContext).load(headrUrl).placeholder(R.drawable.sz_head_default)
					.error(R.drawable.sz_head_default).transform(new GlideCircleTransform(mContext))
					.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.messageIcon);
		}

		holder.messageTag.setText(tagName);
		holder.messageTime.setText(time);
		holder.itemView.setTag(data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_more:

			MessageInfoExtraBean data = (MessageInfoExtraBean) v.getTag();
			if (data != null) {
				try {
					String type = data.targetType;
					String content = data.content;
					JSONObject contentJSON = new JSONObject(content);
					if (ContantsActivity.Message.MESSAGE_RECOMMEND_CLUB.equals(type)) {
						mIntent.setClass(mContext, ActivityClubDetail.class);
						next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_CREW_DETAIL;
						mIntent.putExtra(ContantsActivity.Club.CLUB_ID, contentJSON.getString("id"));
					} else if (ContantsActivity.Message.MESSAGE_RECOMMEND_PERSON.equals(type)) {
						mIntent.setClass(mContext, ActivityMemberInfor.class);
						next_action=ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO;
						mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, contentJSON.getString("id"));
					} else if (ContantsActivity.Message.MESSAGE_RECOMMEND_VIDEO.equals(type)) {
						mIntent.setClass(mContext, VideoPlayerActivity.class);
						next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_VIDER_INFO;
						mIntent.putExtra(ContantsActivity.Video.VIDEO_ID, contentJSON.getString("id"));
					} else if (ContantsActivity.Message.MESSAGE_RECOMMEND_TOPIC.equals(type)) {
						mIntent.setClass(mContext, ActivityTopicDetail.class);
						next_action=ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_OVER_TOPIC_DETAIL;
						mIntent.putExtra(ContantsActivity.Topic.TOPIC_ID, contentJSON.getString("id"));
					} else if (ContantsActivity.Message.MESSAGE_RECOMMEND_NEWS.equals(type)) {
						mIntent.setClass(mContext, ActivityNewsWebContent.class);
						mIntent.putExtra(ContantsActivity.JieQu.NEWS_URL, contentJSON.getString("newsUrl"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((Activity) mContext).startActivityForResult(mIntent,next_action);
			}

			break;
		default:
			break;
		}

	}

//	private int delectId;
//	private int position;
//
//	@Override
//	public boolean onLongClick(View v) {
//		switch (v.getId()) {
//		case R.id.item_view:
//			MessageInfoExtraBean data = (MessageInfoExtraBean) v.getTag();
//			delectId = data.id;
//			position = data.position;
//			showAlterDialog();
//			break;
//
//		default:
//			break;
//		}
//		return false;
//	}
//
//	private Dialog mDialog;
//
//	private void showAlterDialog() {
//		if (mDialog == null) {
//			mDialog = DialogUtils.confirmDialog(mContext, "确定删除当前消息", "确定", "取消", new ConfirmDialog() {
//
//				@Override
//				public void onOKClick(Dialog dialog) {
//					list.remove(position);
//					MessageDBManager
//							.getInstance(mContext,
//									PrefUtils.getString(mContext, ContantsActivity.LoginModle.LOGIN_USER_ID, ""))
//							.delect(delectId);
//					notifyDataSetChanged();
//				}
//
//				@Override
//				public void onCancleClick(Dialog dialog) {
//					// TODO Auto-generated method stub
//
//				}
//			});
//		}
//		if (!mDialog.isShowing()) {
//			mDialog.show();
//		}
//
//	}

}
