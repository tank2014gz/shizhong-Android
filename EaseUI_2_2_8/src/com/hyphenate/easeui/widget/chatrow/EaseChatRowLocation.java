package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.LocationMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.hyhenate.easeui.R;

public class EaseChatRowLocation extends EaseChatRow{

    private TextView locationView;
    private LocationMessageBody locBody;

	public EaseChatRowLocation(Context context, EMMessage message, int position, BaseAdapter adapter,EMConversation conversation, String fromHeader) {
        super(context, message, position, adapter,  conversation, fromHeader);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_location : R.layout.ease_row_sent_location, this);
    }

    @Override
    protected void onFindViewById() {
    	locationView = (TextView) findViewById(R.id.tv_location);
    }


    @Override
    protected void onSetUpView() {
		locBody = (LocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());

		// deal with send message
		if (message.direct == EMMessage.Direct.SEND) {
		    setMessageSendCallback();
            switch (message.status) {
            case CREATE: 
                progressBar.setVisibility(View.GONE);
                statusView.setVisibility(View.VISIBLE);
                break;
            case SUCCESS: // 发送成功
                progressBar.setVisibility(View.GONE);
                statusView.setVisibility(View.GONE);
                break;
            case FAIL: // 发送失败
                progressBar.setVisibility(View.GONE);
                statusView.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS: // 发送中
                progressBar.setVisibility(View.VISIBLE);
                statusView.setVisibility(View.GONE);
                break;
            default:
               break;
            }
        }else{
            if(!message.isAcked() && message.getChatType() == ChatType.Chat){
                try {
                    EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                    message.isAcked = true;
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onBubbleClick() {
		// Intent intent = new Intent(context, EaseBaiduMapActivity.class);
		// intent.putExtra("latitude", locBody.getLatitude());
		// intent.putExtra("longitude", locBody.getLongitude());
		// intent.putExtra("address", locBody.getAddress());
		// activity.startActivity(intent);
    }
    

}
