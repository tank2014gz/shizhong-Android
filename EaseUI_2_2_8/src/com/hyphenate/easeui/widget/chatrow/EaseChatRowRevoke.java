package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Date;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.hyphenate.easeui.EaseConstant;
import com.hyhenate.easeui.R;
import com.hyphenate.easeui.widget.EaseChatMessageList;

/**
 * 撤回消息的 ChatRow 类，实现撤回消息的内容显示
 * 
 * @author lzan13
 *
 */
public class EaseChatRowRevoke extends EaseChatRow {

	private TextView contentvView;

	public EaseChatRowRevoke(Context context, EMMessage message, int position, BaseAdapter adapter,
			EMConversation conversation, String fromHeader) {
		super(context, message, position, adapter, conversation, fromHeader);
	}

	public void setUpView(EMMessage message, int position,
			EaseChatMessageList.MessageListItemClickListener itemClickListener) {
		this.message = message;
		this.position = position;
		this.itemClickListener = itemClickListener;
		onSetUpView();
	}

	@Override
	protected void onInflatView() {
		// 撤回消息只有一个布局，不区分发送和接收方
		if (message.getBooleanAttribute(EaseConstant.EASE_ATTR_REVOKE, false)) {
			inflater.inflate(R.layout.ease_row_revoke_message, this);
		}
	}

	@Override
	protected void onFindViewById() {
		contentvView = (TextView) findViewById(R.id.tv_chatcontent);
	}

	@Override
	protected void onSetUpView() {
		// 设置时间
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
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		contentvView.setText(txtBody.getMessage());
	}

	@Override
	protected void onUpdateView() {

	}

	@Override
	protected void onBubbleClick() {

	}

}
