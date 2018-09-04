package com.shizhong.view.ui.getui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.PrefUtils;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.a.c;
import com.shizhong.view.ui.ActivitySetting;
import com.shizhong.view.ui.base.db.MessageDBManager;
import com.shizhong.view.ui.base.utils.ACache;
import com.shizhong.view.ui.base.utils.CameraManager;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.MessageInfoExtraBean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuliyan on 16/7/31.
 */
public class PushGeTuiReceiver extends BroadcastReceiver{

    private ACache mCache;
    private String member_id;
    @Override
    public void onReceive(Context context, Intent intent) {
        mCache=ACache.get(context);
        member_id = PrefUtils.getString(context, ContantsActivity.LoginModle.LOGIN_USER_ID,
                "");

        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:

                String cid = bundle.getString("clientid");
                String  token= PrefUtils.getString(context, ContantsActivity.LoginModle.LOGIN_TOKEN,"");
                GetuiSdkHttpPost.postClientId(context,cid,token);
                // TODO:处理cid返回
                Intent intent1 = new Intent(ContantsActivity.Action.ACTION_GET_GETUI_CLIENT_ID);
                intent1.putExtra(ContantsActivity.Extra.EXTRA_GET_GETUI_CLIENT_ID,cid);
                intent1.putExtra(ContantsActivity.Extra.IS_GET_GETUI_CLIENT_ID,true);
                context.sendBroadcast(intent1);

                break;
            case PushConsts.GET_MSG_DATA:

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    // TODO:接收处理透传（payload）数据
//                    LogUtils.e("shizhong:   taskid",taskid);
//                    LogUtils.e("shizhong:   messageid",messageid);
//                    LogUtils.e("shizhong:   data",data);
                    MessageInfoExtraBean info = new MessageInfoExtraBean();
                    try {
                        JSONObject   messageObject = new JSONObject(data);;
                        String  isManage=messageObject.getString("isManage");
                        if(isManage.equals("1")){
                        info.extras = data;
                        info.description = messageObject.getString("title");
                        info.isFollow = false;
                        info.isRead = false;
                        info.operateTime = messageObject.getString("operateTime");
                        info.fromHeader = messageObject.getString("fromHeader");
                        info.fromId = messageObject.getString("fromId");
                        info.fromNickname = messageObject.getString("fromNickname");
                        String targetType = messageObject.getString("targetType");
                        info.targetType = targetType;
                        info.type = MessageDBManager.getMessageType(targetType);
                        info.toHeader = messageObject.getString("toHeader");
                        info.toId = messageObject.getString("toId");
                        info.toNickname = messageObject.getString("toNickname");
                        info.content = messageObject.getJSONObject("content").toString();
//						LogUtils.e("shizhong",info.toString());
                        if(!TextUtils.isEmpty(member_id)) {
                            MessageDBManager.getInstance(context, member_id).insertMessage(info);
                        }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCache.put(ContantsActivity.Action.CACHE_GETUI_INFO_DATA,data);
                    Intent intent2=new Intent(ContantsActivity.Action.ACTION_GET_GETUI_CLIENT_ID);
                    intent2.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent2.putExtra(ContantsActivity.Extra.IS_GET_GETUI_MESSAGE,true);
                    intent2.putExtra(ContantsActivity.Extra.EXTRA_GET_GETUI_TASKID,taskid);
                    intent2.putExtra(ContantsActivity.Extra.EXTRA_GET_GETUI_MESSAGEID,messageid);
                    intent2.putExtra(ContantsActivity.Extra.EXTRA_GET_GETUI_MESSGE,data);
                    context.sendBroadcast(intent2);






                }
                break;
            default:
                break;
        }
    }
}
