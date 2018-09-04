package com.hyphenate.easeui.widget.chatrow;

import java.io.File;
import java.net.URI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.ImageMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hyphenate.easeui.EaseConstant;
import com.hyhenate.easeui.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easeui.utils.BitmapUtils;
import com.hyphenate.easeui.utils.EaseACKUtil;
import com.hyphenate.easeui.utils.EaseBlurUtils;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.UIUtils;

public class EaseChatRowImage extends EaseChatRowFile {

    protected ImageView imageView;
    private ImageMessageBody imgBody;
    private  Context mContext;

    public EaseChatRowImage(Context context, EMMessage message, int position, BaseAdapter adapter,EMConversation conversation, String fromHeader) {
        super(context, message, position, adapter,  conversation, fromHeader);
        this.mContext=context;
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_picture
                : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }

    @Override
    protected void onSetUpView() {
        imgBody = (ImageMessageBody) message.getBody();
        // 接收方向的消息
        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.status == EMMessage.Status.INPROGRESS) {
                imageView.setImageResource(R.drawable.ease_default_image);
                setMessageReceiveCallback();
            } else {
                progressBar.setVisibility(View.GONE);
                percentageView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.ease_default_image);
                if (imgBody.getLocalUrl() != null) {
                    Log.e("shizong","imagepath0:getLocalUrl"+imgBody.getLocalUrl());
                    String remotePath = imgBody.getRemoteUrl();
                    Log.e("shizong","imagepath0:remotePath"+imgBody.getRemoteUrl());
                    String filePath = EaseImageUtils.getImagePath(remotePath);
                    Log.e("shizong","imagepath0:filePath"+filePath);
                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
                    Log.e("shizong","imagepath0:thumbRemoteUrl"+imgBody.getThumbnailUrl());
                    String thumbnailPath = EaseImageUtils.getThumbnailImagePath(thumbRemoteUrl);
                    Log.e("shizong","imagepath0:thumbnailPath"+thumbnailPath);
                    showImageView(thumbnailPath, imageView, filePath, message);



                }
            }
            return;
        }

        String filePath = imgBody.getLocalUrl();
        Log.e("shizong","imagepath1:"+filePath);
        if (filePath != null) {
            showImageView(filePath, imageView, filePath, message);
        }
        handleSendMessage();
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onBubbleClick() {
        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        File file = new File(imgBody.getLocalUrl());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
            intent.putExtra(EaseConstant.EASE_ATTR_REVOKE_MSG_ID, message.getMsgId());
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            intent.putExtra("secret", imgBody.getSecret());
            intent.putExtra("remotepath", imgBody.getRemoteUrl());
            // 这里把当前消息的id传递过去，是为了实现查看大图之后销毁这条消息
            intent.putExtra(EaseConstant.EASE_ATTR_REVOKE_MSG_ID, message.getMsgId());
        }
        if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
        		&& message.getChatType() == ChatType.Chat) {
            sendACKMessage();
        }
        context.startActivity(intent);
    }

    /**
     * ACK 消息的发送，根据是否发送成功做些相应的操作，这里是把发送失败的消息id和username保存在序列化类中
     */
    private void sendACKMessage() {
        try {
            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
            message.isAcked = true;
        } catch (EaseMobException e) {
            e.printStackTrace();
            EaseACKUtil.getInstance(context).saveACKDataId(message.getMsgId(), message.getFrom());
        } finally {
        	if(message.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
                    && message.direct == Direct.RECEIVE){
        		EMChatManager.getInstance().getConversation(message.getFrom()).removeMessage(message.getMsgId());
        		onUpdateView();
        	}
        }
    }
    /**
     * load image into image view
     * 
     * @param thumbernailPath
     * @param iv
     * @param
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath,
            final EMMessage message) {

//        if (message.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
//                && message.direct == Direct.RECEIVE) {

//            Uri imageUri=Uri.parse(thumbernailPath);
//            Log.e("shizhong","recvice：thumbernailPath"+thumbernailPath);
//
//            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(imageUri)
//                    .setResizeOptions(new ResizeOptions(UIUtils.dipToPx(mContext,160), UIUtils.dipToPx(mContext,160)))
//                    .build();
//            DraweeController controller = Fresco.newDraweeControllerBuilder()
//                    .setImageRequest(request)
//                    .setOldController(iv.getController())
//                    .setControllerListener(new BaseControllerListener<ImageInfo>())
//                    .build();
//            iv.setController(controller);
//               Glide.with(this.context).load(thumbernailPath).placeholder(R.drawable.sz_activity_default)
//                .error(R.drawable.sz_activity_default).override(UIUtils.dipToPx(mContext,160), UIUtils.dipToPx(mContext,160))
//                .fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
//        } else {
//            Uri imageUri=Uri.parse(localFullSizePath);
//            Log.e("shizhong","send：localFullSizePath"+localFullSizePath);
//            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(imageUri)
//                    .setResizeOptions(new ResizeOptions(UIUtils.dipToPx(mContext,160), UIUtils.dipToPx(mContext,160)))
//                    .build();
//            DraweeController controller = Fresco.newDraweeControllerBuilder()
//                    .setImageRequest(request)
//                    .setOldController(iv.getController())
//                    .setControllerListener(new BaseControllerListener<ImageInfo>())
//                    .build();
//            iv.setController(controller);

//            Glide.with(this.context).load(localFullSizePath).placeholder(R.drawable.sz_activity_default)
//                    .error(R.drawable.sz_activity_default).override(UIUtils.dipToPx(mContext,160), UIUtils.dipToPx(mContext,160))
//                    .fitCenter() .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
//        }
//        return true;

        Bitmap bitmap = EaseImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            // 加上当前图片是否是阅后即焚类型的判断，如果是 则模糊图片在设置给imageView控件
            if (message.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
                    && message.direct == Direct.RECEIVE) {
                imageView.setImageBitmap(EaseBlurUtils.blurBitmap(bitmap));
            } else {
                iv.setImageBitmap(bitmap);
            }
            return true;
        } else {
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return EaseImageUtils.decodeScaleImage(thumbernailPath, 160, 160);
                    } else {
                        if (message.direct == EMMessage.Direct.SEND) {
                            return EaseImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
                        } else {
                            return null;
                        }
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        // 加上当前图片是否是阅后即焚类型的判断，如果是 则模糊图片在设置给imageView控件
                        if (message.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
                                && message.direct == Direct.RECEIVE) {
                            imageView.setImageBitmap(EaseBlurUtils.blurBitmap(image));
                        } else {
                            iv.setImageBitmap(image);
                        }
                        EaseImageCache.getInstance().put(thumbernailPath, image);
                    } else {
                        if (message.status == EMMessage.Status.FAIL) {
                            if (EaseCommonUtils.isNetWorkConnected(activity)) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        EMChatManager.getInstance().asyncFetchMessage(message);
                                    }
                                }).start();
                            }
                        }

                    }
                }
            }.execute();
            return true;
    } }
}
