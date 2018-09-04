package com.shizhong.view.ui.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.shizhong.view.ui.ActivityMemberInfor;
import com.shizhong.view.ui.ActivityMessageTypes;
import com.shizhong.view.ui.ChatActivity;
import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.db.MessageDBManager;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.utils.GlideCircleTransform;
import com.shizhong.view.ui.base.view.DialogUtils;
import com.shizhong.view.ui.base.view.DialogUtils.ConfirmDialog;
import com.shizhong.view.ui.base.view.RoundImageView;
import com.shizhong.view.ui.bean.MessageTypeListItem;
import com.hyphenate.easeui.utils.PrefUtils;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.EaseConstant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MessageAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener {

	private Context mContext;
	private List<Object> mList;
	private LayoutInflater mLayoutInflater;
	private Intent mIntent = new Intent();
	private String member_id;
	private String mMineHeader = null;

	public MessageAdapter(Context context, List<Object> list) {
		this.mContext = context;
		this.mList = list;
		mLayoutInflater = LayoutInflater.from(mContext);
		member_id = PrefUtils.getString(mContext, ContantsActivity.LoginModle.LOGIN_USER_ID, "");
		mMineHeader = PrefUtils.getString(mContext, ContantsActivity.LoginModle.HEAD_URL, "");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList != null ? (mList.size() + 1) : 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList != null ? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHodler holder = null;
		if (position < 4) {
			holder = new ViewHodler();
			convertView = mLayoutInflater.inflate(R.layout.item_message_head_layout, null);
			holder.icon_message_tag = (ImageView) convertView.findViewById(R.id.message_tag);
			holder.itemLayout = convertView.findViewById(R.id.item_layout);
			holder.itemLayout.setOnClickListener(this);
			holder.icon_image = (ImageView) convertView.findViewById(R.id.icon_image);
			holder.icon_name = (TextView) convertView.findViewById(R.id.icon_name);
		} else if (position == 4) {
			convertView = mLayoutInflater.inflate(R.layout.item_message_text_layout, null);
		} else {
			holder = new ViewHodler();
			convertView = mLayoutInflater.inflate(R.layout.item_message_foot_layout, null);
			holder.itemLayout = convertView.findViewById(R.id.item_layout);
			holder.itemLayout.setOnClickListener(this);
			holder.itemLayout.setOnLongClickListener(this);
			holder.head_image = (SimpleDraweeView) convertView.findViewById(R.id.icon_image);
			holder.head_image.setOnClickListener(this);
			holder.icon_name = (TextView) convertView.findViewById(R.id.nick_name);
			holder.icon_content = (TextView) convertView.findViewById(R.id.msg_content);
			holder.icon_count = (TextView) convertView.findViewById(R.id.msg_count);
		}
		if (position < 4) {
			MessageTypeListItem bean = (MessageTypeListItem) mList.get(position);
			if (bean != null) {
				int iconId = bean.iconId;
				bean.position = position;
				holder.icon_image.setImageResource(iconId);

				String iconName = bean.iconName;
				holder.icon_name.setText(iconName);
				boolean hasNoRead = MessageDBManager.getInstance(mContext, member_id).isReaded(bean.type);
				if (hasNoRead) {
					holder.icon_message_tag.setVisibility(View.VISIBLE);
				} else {
					holder.icon_message_tag.setVisibility(View.GONE);
				}
				holder.itemLayout.setTag(bean);
			}

		} else if (position > 4) {
			Object obj = mList.get(position - 1);
			if (obj instanceof EMConversation) {
				EMConversation bean = (EMConversation) obj;
				if (bean != null) {
					String memeberIfoJson = bean.getExtField();
					try {
						JSONObject JSONInfo = new JSONObject(memeberIfoJson);
						String headUrl = JSONInfo.getString("headerUrl");
						headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.SMALL,
								ContantsActivity.Image.SMALL);
						holder.head_image.setImageURI(Uri.parse(headUrl));
//						Glide.with(mContext).load(headUrl).placeholder(R.drawable.sz_head_default)
//								.error(R.drawable._FF202020).transform(new GlideCircleTransform(mContext))
//								.diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.icon_image);
						String nickName = JSONInfo.getString("nickname");
						if (TextUtils.isEmpty(nickName)) {
							nickName = "匿名舞者";
						}
						holder.icon_name.setText(nickName);
						EMMessage lastMessage = bean.getLastMessage();
						if (lastMessage != null) {
							Spannable context = EaseSmileUtils.getSmiledText(mContext,
									EaseCommonUtils.getMessageDigest(lastMessage, (mContext)));
							holder.icon_content.setText(context, BufferType.SPANNABLE);
						}
						int count = bean.getUnreadMsgCount();
						if (count <= 0) {
							holder.icon_count.setVisibility(View.GONE);
						} else {
							holder.icon_count.setVisibility(View.VISIBLE);
							holder.icon_count.setText("" + count);
						}
						holder.itemLayout.setTag(bean);
						holder.head_image.setTag(bean);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		return convertView;
	}

	class ViewHodler {
		View itemLayout;
		ImageView icon_image;
		SimpleDraweeView head_image;
		TextView icon_name;
		TextView icon_content;
		ImageView icon_message_tag;
		TextView icon_count;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.item_layout:
			Object tag = v.getTag();
			if (tag != null) {
				if (tag instanceof MessageTypeListItem) {
					String type = ((MessageTypeListItem) tag).type;
					int position = ((MessageTypeListItem) tag).position;
					MessageDBManager.getInstance(mContext, member_id).changeReaded(type);
					MessageTypeListItem item = ((MessageTypeListItem) tag);
					item.hasNoRead = 1;
					mList.set(position, item);
					mIntent.setClass(mContext, ActivityMessageTypes.class);
					mIntent.putExtra(ContantsActivity.Message.MESSAGE_TYPE, type);
					((Activity) mContext).startActivityForResult(mIntent,ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_MESSEGE_LIST );
				} else if (tag instanceof EMConversation) {
					EMConversation bean = (EMConversation) tag;
					mIntent.setClass(mContext, ChatActivity.class);
					mIntent.putExtra(EaseConstant.EXTRA_USER_ID, bean.getUserName());
					mIntent.putExtra(ContantsActivity.LoginModle.HEAD_URL, mMineHeader);
					((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_GO_LOOK_CHART_LIST);
				}
			}
			break;
		case R.id.icon_image:
			tag = v.getTag();
			if (tag instanceof EMConversation) {
				EMConversation bean = (EMConversation) tag;
				mIntent.setClass(mContext, ActivityMemberInfor.class);
				mIntent.putExtra(ContantsActivity.LoginModle.LOGIN_USER_ID, bean.getUserName());
				((Activity) mContext).startActivityForResult(mIntent, ContantsActivity.Action.REQUEST_ACTION_LOOK_OVER_MEMBER_INFO);
			}

			break;

		default:
			break;
		}
	}

	private EMConversation mDelecteConversation;

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.item_layout:
			mDelecteConversation = (EMConversation) v.getTag();
			showAlterDialog();
			// EMClient.getInstance().chatManager().deleteConversation(bean.conversationId(),
			// true);
			break;

		default:
			break;
		}
		return true;
	}

	private Dialog mAlterDialog;

	public void showAlterDialog() {
		if (mAlterDialog == null) {
			mAlterDialog = DialogUtils.confirmDialog(mContext, "是否要删除当前会话", "确定", "取消", new ConfirmDialog() {

				@Override
				public void onOKClick(Dialog dialog) {
					if (mDelecteConversation != null) {
						if (mList != null && mList.size() > 4) {
							mList.remove(mDelecteConversation);
							notifyDataSetChanged();
							EMChatManager.getInstance().deleteConversation(mDelecteConversation.getUserName(), true);
							mDelecteConversation = null;
						}

					}

				}

				@Override
				public void onCancleClick(Dialog dialog) {
					// TODO Auto-generated method stub

				}
			});
		}
		if (!mAlterDialog.isShowing()) {
			mAlterDialog.show();
		}
	}

}
