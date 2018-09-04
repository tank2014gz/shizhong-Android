package com.hyphenate.easeui.widget.chatrow;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.util.DateUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.EaseConstant;
import com.hyhenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseChatMessageList.MessageListItemClickListener;

public abstract class EaseChatRow extends LinearLayout {
	protected static final String TAG = EaseChatRow.class.getSimpleName();

	protected LayoutInflater inflater;
	protected Context context;
	protected BaseAdapter adapter;
	protected EMMessage message;
	protected int position;

	protected TextView timeStampView;
	protected SimpleDraweeView userAvatarView;
	protected View bubbleLayout;
	protected TextView usernickView;

	protected TextView percentageView;
	protected ProgressBar progressBar;
	protected ImageView statusView;
	protected Activity activity;

	protected TextView ackedView;
	protected TextView deliveredView;

	protected EMCallBack messageSendCallback;
	protected EMCallBack messageReceiveCallback;

	protected MessageListItemClickListener itemClickListener;
	protected String fromHeader;
	protected EMConversation conversation;

	public EaseChatRow(Context context, EMMessage message, int position, BaseAdapter adapter,
			EMConversation conversation, String fromHeader) {
		super(context);
		this.context = context;
		this.activity = (Activity) context;
		this.message = message;
		this.conversation = conversation;
		this.position = position;
		this.adapter = adapter;
		this.fromHeader = fromHeader;
		inflater = LayoutInflater.from(context);

		initView();
	}

	private void initView() {
		onInflatView();
		timeStampView = (TextView) findViewById(R.id.timestamp);
		userAvatarView = (SimpleDraweeView) findViewById(R.id.iv_userhead);
		bubbleLayout = findViewById(R.id.bubble);
		usernickView = (TextView) findViewById(R.id.tv_userid);

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		statusView = (ImageView) findViewById(R.id.msg_status);
		ackedView = (TextView) findViewById(R.id.tv_ack);
		deliveredView = (TextView) findViewById(R.id.tv_delivered);

		onFindViewById();
	}

	/**
	 * 根据当前message和position设置控件属性等
	 * 
	 * @param message
	 * @param position
	 */
	public void setUpView(EMMessage message, int position,
			EaseChatMessageList.MessageListItemClickListener itemClickListener) {
		this.message = message;
		this.position = position;
		this.itemClickListener = itemClickListener;

		setUpBaseView();
		onSetUpView();
		setClickListener();
	}

	@SuppressWarnings("deprecation")
	private void setUpBaseView() {
		// 设置用户昵称头像，bubble背景等
		TextView timestamp = (TextView) findViewById(R.id.timestamp);
		if (timestamp != null) {
			if (position == 0) {
				timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			} else {
				// 两条消息时间离得如果稍长，显示时间
				EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
				if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
					timestamp.setVisibility(View.GONE);
				} else {
					timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
					timestamp.setVisibility(View.VISIBLE);
				}
			}
		}
		// 设置头像和nick
		if (message.direct == Direct.SEND) {

			String header = FormatImageURLUtils.formatURL(fromHeader, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
			userAvatarView.setImageURI(Uri.parse(header));
//			Glide.with(this.context).load(header).placeholder(R.drawable.sz_head_default)
//					.error(R.drawable.sz_head_default).transform(new GlideCircleTransform(this.context))
//					.diskCacheStrategy(DiskCacheStrategy.ALL).into(userAvatarView);
			// 发送方不显示nick
			// UserUtils.setUserNick(EMChatManager.getInstance().getCurrentUser(),
			// usernickView);
		} else {
			// EaseUserUtils.setUserAvatar(context, message.getFrom(),
			// userAvatarView);
			// EaseUserUtils.setUserNick(message.getFrom(), usernickView);
			if (conversation != null) {
				String extfile = conversation.getExtField();
				try {
					JSONObject extJOSN = new JSONObject(extfile);
					String headUrl = extJOSN.getString("headerUrl");
					headUrl = FormatImageURLUtils.formatURL(headUrl, ContantsActivity.Image.SMALL,
							ContantsActivity.Image.SMALL);
					userAvatarView.setImageURI(Uri.parse(headUrl));
//					Glide.with(this.context).load(headUrl).placeholder(R.drawable.sz_head_default)
//							.error(R.drawable.sz_head_default).transform(new GlideCircleTransform(this.context))
//							.diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(userAvatarView);
					// mImageLoader.displayImage(FormatImageURLUtils.formatURL(headUrl,
					// ContantsActivity.Image.SMALL,
					// ContantsActivity.Image.SMALL), userAvatarView,
					// headOptions);
					String nickName = extJOSN.getString("nickname");
					usernickView.setText(nickName == null ? "" : nickName);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		if (deliveredView != null) {
			if (message.isDelivered) {
				deliveredView.setVisibility(View.INVISIBLE);
			} else {
				deliveredView.setVisibility(View.INVISIBLE);
			}
		}

		if (ackedView != null) {
			if (message.isAcked) {
				if (deliveredView != null) {
					deliveredView.setVisibility(View.INVISIBLE);
				}
				ackedView.setVisibility(View.INVISIBLE);
				if (message.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)) {
					// 因为当某一条消息不在内存中时，removeMessage方法无效，所以在当前聊天界面显示消息的时候去判断此消息是否是阅后即焚类型，并且已读，这样来进行删除
					EMChatManager.getInstance().getConversation(message.getTo()).removeMessage(message.getMsgId());
					onUpdateView();
				}
			} else {
				ackedView.setVisibility(View.INVISIBLE);
			}
		}

		if (adapter instanceof EaseMessageAdapter) {
			if (((EaseMessageAdapter) adapter).isShowAvatar())
				userAvatarView.setVisibility(View.VISIBLE);
			else
				userAvatarView.setVisibility(View.GONE);
			if (usernickView != null) {
				if (((EaseMessageAdapter) adapter).isShowUserNick())
					usernickView.setVisibility(View.VISIBLE);
				else
					usernickView.setVisibility(View.GONE);
			}
			if (message.direct == Direct.SEND) {
				if (((EaseMessageAdapter) adapter).getMyBubbleBg() != null)
					bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
				// else
				// bubbleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.chatto_bg));
			} else if (message.direct == Direct.RECEIVE) {
				if (((EaseMessageAdapter) adapter).getOtherBuddleBg() != null)
					bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBuddleBg());
				// else
				// bubbleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ease_chatfrom_bg));
			}
		}
	}

	/**
	 * 设置消息发送callback
	 */
	protected void setMessageSendCallback() {
		if (messageSendCallback == null) {
			messageSendCallback = new EMCallBack() {

				@Override
				public void onSuccess() {
					updateView();
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (percentageView != null)
								percentageView.setText(progress + "%");

						}
					});
				}

				@Override
				public void onError(int code, String error) {
					updateView();
				}
			};
		}
		message.setMessageStatusCallback(messageSendCallback);
	}

	/**
	 * 设置消息接收callback
	 */
	protected void setMessageReceiveCallback() {
		if (messageReceiveCallback == null) {
			messageReceiveCallback = new EMCallBack() {

				@Override
				public void onSuccess() {
					updateView();
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							if (percentageView != null) {
								percentageView.setText(progress + "%");
							}
						}
					});
				}

				@Override
				public void onError(int code, String error) {
					updateView();
				}
			};
		}
		message.setMessageStatusCallback(messageReceiveCallback);
	}

	private void setClickListener() {
		if (bubbleLayout != null) {
			bubbleLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (itemClickListener != null) {
						if (!itemClickListener.onBubbleClick(message)) {
							// 如果listener返回false不处理这个事件，执行lib默认的处理
							onBubbleClick();
						}
					}
				}
			});

			bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (itemClickListener != null) {
						itemClickListener.onBubbleLongClick(message);
					}
					return true;
				}
			});
		}

		if (statusView != null) {
			statusView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (itemClickListener != null) {
						itemClickListener.onResendClick(message);
					}
				}
			});
		}

		userAvatarView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (itemClickListener != null) {
					if (message.direct == Direct.SEND) {
						itemClickListener.onUserAvatarClick(EMChatManager.getInstance().getCurrentUser());
					} else {
						itemClickListener.onUserAvatarClick(message.getFrom());
					}
				}
			}
		});
	}

	protected void updateView() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (message.status == EMMessage.Status.FAIL) {

					if (message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT) {
						Toast.makeText(activity, activity.getString(R.string.send_fail)
								+ activity.getString(R.string.error_send_invalid_content), 0).show();
					} else if (message.getError() == EMError.MESSAGE_SEND_NOT_IN_THE_GROUP) {
						Toast.makeText(activity, activity.getString(R.string.send_fail)
								+ activity.getString(R.string.error_send_not_in_the_group), 0).show();
					} else if (message.getError() == EMError.MESSAGE_SEND_IN_BLACKLIST) {
						Toast.makeText(activity, activity.getString(R.string.send_fail)
								+ activity.getString(R.string.error_send_in_blacklist), 0).show();
					} else {
						Toast.makeText(activity, activity.getString(R.string.send_fail)
								+ activity.getString(R.string.connect_failuer_toast), 0).show();
					}
				}

				onUpdateView();
			}
		});

	}

	/**
	 * 填充layout
	 */
	protected abstract void onInflatView();

	/**
	 * 查找chatrow里的控件
	 */
	protected abstract void onFindViewById();

	/**
	 * 消息状态改变，刷新listview
	 */
	protected abstract void onUpdateView();

	/**
	 * 设置更新控件属性
	 */
	protected abstract void onSetUpView();

	/**
	 * 聊天气泡被点击事件
	 */
	protected abstract void onBubbleClick();

}
