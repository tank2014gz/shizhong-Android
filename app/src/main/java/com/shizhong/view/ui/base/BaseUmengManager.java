package com.shizhong.view.ui.base;

import com.hyphenate.easeui.ContantsActivity;
import com.hyphenate.easeui.utils.FormatImageURLUtils;
import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.ShareContentBean;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class BaseUmengManager {

	// 邮箱信息： shizhong@shizhongapp.com ：
	private static final String QQ_APPID = "1105004806";
	private static final String QQ_APPKEY = "q5w99ZTfiiGNsTsm";

	// private static final String SINA_APPKEY = "852115644";// 852115644
	// private static final String SINA_APPSECRET =
	// "1d834b393449775afc2a25cb955ea3b3";// 1d834b393449775afc2a25cb955ea3b3

	private static final String WEICHART_APPID = "wxb5e19f45c220941a";// wxb5e19f45c220941a
	private static final String WEICHART_APPKEY = "4e8457832b45e7468ec80865f99cc969";// 4e8457832b45e7468ec80865f99cc969

	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
	private UMSocialService mShareController = UMServiceFactory.getUMSocialService("com.umeng.share",
			RequestType.SOCIAL);
	private UMWXHandler wxHandler;
	private UMWXHandler wxCircleHandler;
	private UMQQSsoHandler qqSsoHandler;
	private QZoneSsoHandler qZoneSsoHandler;
	private String shareSinaInfo = "(通过#失重APP#拍摄) %s ";

	public BaseUmengManager() {
		super();
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mController != null) {
			UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
		if (mShareController != null) {
			UMSsoHandler ssoHandler = mShareController.getConfig().getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
	}

	public void addLoginPermission(Activity activity) {
		if (mController != null) {
			mController.getConfig().setSsoHandler(new SinaSsoHandler());
			// 微信
			wxHandler = new UMWXHandler(activity, WEICHART_APPID, WEICHART_APPKEY);
			wxHandler.addToSocialSDK();
			// 支持微信朋友圈
			wxCircleHandler = new UMWXHandler(activity, WEICHART_APPID, WEICHART_APPKEY);
			wxCircleHandler.setToCircle(true);
			wxCircleHandler.addToSocialSDK();
			// QQ
			// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
			qqSsoHandler = new UMQQSsoHandler(activity, QQ_APPID, QQ_APPKEY);
			qqSsoHandler.addToSocialSDK();
			// 添加QZone平台
			qZoneSsoHandler = new QZoneSsoHandler(activity, QQ_APPID, QQ_APPKEY);
			qZoneSsoHandler.addToSocialSDK();

		}
	}


	public   void  addSharePermission(Activity activity){
		if(mShareController!=null){
			mShareController.getConfig().setSsoHandler(new SinaSsoHandler());
			// 微信
			wxHandler = new UMWXHandler(activity, WEICHART_APPID, WEICHART_APPKEY);
			wxHandler.addToSocialSDK();
			// 支持微信朋友圈
			wxCircleHandler = new UMWXHandler(activity, WEICHART_APPID, WEICHART_APPKEY);
			wxCircleHandler.setToCircle(true);
			wxCircleHandler.addToSocialSDK();
			// QQ
			// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
			qqSsoHandler = new UMQQSsoHandler(activity, QQ_APPID, QQ_APPKEY);
			qqSsoHandler.addToSocialSDK();
			// 添加QZone平台
			qZoneSsoHandler = new QZoneSsoHandler(activity, QQ_APPID, QQ_APPKEY);
			qZoneSsoHandler.addToSocialSDK();
		}
	}

	/**
	 * 授权
	 * 
	 * @param context
	 * @param platform
	 * @param listener
	 */
	public void doAuthor(Context context, SHARE_MEDIA platform, UMAuthListener listener) {
		if (mController != null) {
			mController.getConfig().openToast();
			mController.doOauthVerify(context, platform, listener);
		}
	}

	/**
	 * 获取平台信息
	 * 
	 * @param context
	 * @param platform
	 * @param listener
	 */
	public void getPlactformInfo(Context context, SHARE_MEDIA platform, UMDataListener listener) {
		if (mController != null) {
			mController.getConfig().openToast();
			mController.getPlatformInfo(context, platform, listener);
		}
	}

	/**
	 * 删除授权
	 * 
	 * @param context
	 * @param platform
	 * @param
	 */
	public void delectuthor(Context context, SHARE_MEDIA platform) {
		if (mController != null) {
			mController.deleteOauth(context, platform, null);
		}
	}

	/**
	 * 根据报名【判断手机是否已经安装此应用】
	 * 
	 * @param context
	 * @param pkgName
	 * @return
	 */
	private boolean isPkgInstall(Context context, String pkgName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isInstallApp(SHARE_MEDIA plateform, Context context) {
		String packageName = "";
		if (plateform == SHARE_MEDIA.SINA) {
			packageName = "com.sina.weibo";
		} else if (plateform == SHARE_MEDIA.QQ) {
			packageName = "com.tencent.mobileqq";
		} else if (plateform == SHARE_MEDIA.WEIXIN || plateform == SHARE_MEDIA.WEIXIN_CIRCLE) {
			packageName = "com.tencent.mm";
		}
		return isPkgInstall(context, packageName);
	}

	@SuppressWarnings("static-access")
	public void sharePlactformInfo(final Activity context, final SHARE_MEDIA platform, final ShareContentBean shareBean,
			SnsPostListener listener) {
		LogUtils.i("sharebean", "  sharePlactformInfo{}" + platform.name() + "share_bean" + shareBean.toString());

		// String videoUrl = shareBean.shareVideo;
		String shartaTitle = shareBean.shareContent;
		String shareUrl = shareBean.shareUrl;
		String coverUrl = shareBean.shareImage;
		if(!TextUtils.isEmpty(coverUrl)){
			coverUrl=FormatImageURLUtils.formatURL(coverUrl, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		UMVideo video = new UMVideo(shareUrl);
		// 视频分享
		video.setThumb(coverUrl);
		video.setTitle(shartaTitle);
//		video.setTargetUrl(shareUrl);
		switch (platform) {
		case SINA:
			shartaTitle = shartaTitle += shareSinaInfo.format(shareSinaInfo, TextUtils.isEmpty(shareUrl)
					? "http://a.app.qq.com/o/simple.jsp?pkgname=com.shizhong.view.ui" : shareUrl);
			SinaShareContent s = new SinaShareContent(shartaTitle);
			UMImage image = new UMImage(context, coverUrl);
			s.setShareImage(image);
			if (mShareController != null) {
				mShareController.setShareMedia(s);
			}
			break;
		case QQ:

			QQShareContent qqContent = new QQShareContent(video);
			qqContent.setTitle(shartaTitle);
			if (mShareController != null) {
				mShareController.setShareMedia(qqContent);
			}
			break;
		case QZONE:
			QZoneShareContent qzoneContent = new QZoneShareContent(video);
			if (mShareController != null) {
				mShareController.setShareMedia(qzoneContent);
			}
			break;
		case WEIXIN:
			WeiXinShareContent weiXinContent = new WeiXinShareContent(video);
			// weiXinContent.setTargetUrl(shareUrl);
			weiXinContent.setTitle(shartaTitle);
			if (mShareController != null) {
				mShareController.setShareMedia(weiXinContent);
			}

		case WEIXIN_CIRCLE:
			CircleShareContent circleContent = new CircleShareContent(video);
			circleContent.setShareVideo(video);
			// circleContent.setTargetUrl(shareUrl);
			if (mShareController != null) {
				mShareController.setShareMedia(circleContent);
			}
			break;
		default:
			break;
		}
		mShareController.getConfig().openToast();
		mShareController.directShare(context, platform, listener);

	}

	public void inviteFriends(final Activity context, final SHARE_MEDIA platform, final ShareContentBean shareBean) {

		String shartaTitle = shareBean.shareTitle;
		String shareUrl = shareBean.shareUrl;
		int app_icon_id = shareBean.app_icon_id;
		UMImage image = new UMImage(context, app_icon_id);
		image.setTargetUrl(shareUrl);
		switch (platform) {
		case SINA:
			SinaShareContent sinaContent = new SinaShareContent();
			shartaTitle = "我刚下载了“失重app” 这是款街舞软件，希望喜欢街舞的朋友一起来玩哦。（不论何时何地，保留住对街舞那份热爱。）#失重APP#";
			sinaContent.setShareImage(image);
			sinaContent.setTargetUrl(shareUrl);
			sinaContent.setTitle(shartaTitle);
			sinaContent.setShareContent(shartaTitle);
			if (mShareController != null) {
				mShareController.setShareMedia(sinaContent);
			}
			break;
		case QQ:

			QQShareContent qqContent = new QQShareContent();
			qqContent.setShareImage(image);
			qqContent.setTargetUrl(shareUrl);
			qqContent.setTitle(shartaTitle);
			qqContent.setShareContent(" ");
			if (mShareController != null) {
				mShareController.setShareMedia(qqContent);
			}
			break;
		case QZONE:
			QZoneShareContent qzoneContent = new QZoneShareContent();
			qzoneContent.setTargetUrl(shareUrl);
			qzoneContent.setTitle(shartaTitle);
			qzoneContent.setShareContent(" ");
			qzoneContent.setShareImage(image);
			if (mShareController != null) {
				mShareController.setShareMedia(qzoneContent);
			}
			break;
		case WEIXIN:
			WeiXinShareContent weiXinContent = new WeiXinShareContent();
			weiXinContent.setTargetUrl(shareUrl);
			weiXinContent.setTitle(shartaTitle);
			weiXinContent.setShareContent(" ");
			weiXinContent.setShareImage(image);
			if (mShareController != null) {
				mShareController.setShareMedia(weiXinContent);
			}
			break;
		case WEIXIN_CIRCLE:
			CircleShareContent circleContent = new CircleShareContent();
			circleContent.setTargetUrl(shareUrl);
			circleContent.setTitle(shartaTitle);
			circleContent.setShareContent(" ");
			circleContent.setShareImage(image);
			if (mShareController != null) {
				mShareController.setShareMedia(circleContent);
			}
			break;
		default:
			break;
		}
		mShareController.getConfig().openToast();
		mShareController.directShare(context, platform, null);

	}

	public void shareWeb(final Activity context, final SHARE_MEDIA platform, final ShareContentBean shareBean) {

		String shartaTitle = shareBean.shareTitle;
		String shareUrl = shareBean.shareUrl;
		String shareImage = shareBean.shareImage;
		String shareContent = shareBean.shareContent;
		if(!TextUtils.isEmpty(shareImage)){
			shareImage=FormatImageURLUtils.formatURL(shareImage, ContantsActivity.Image.SMALL,
					ContantsActivity.Image.SMALL);
		}
		UMImage image = new UMImage(context, shareImage);
		image.setTargetUrl(shareUrl);
		switch (platform) {
		case SINA:
			SinaShareContent sinaContent = new SinaShareContent();
			sinaContent.setShareImage(image);
			sinaContent.setTargetUrl(shareUrl);
			sinaContent.setTitle(shartaTitle);
			sinaContent.setShareContent(shareContent);
			if (mShareController != null) {
				mShareController.setShareMedia(sinaContent);
			}
			break;
		case QQ:

			QQShareContent qqContent = new QQShareContent();
			qqContent.setShareImage(image);
			qqContent.setTargetUrl(shareUrl);
			qqContent.setTitle(shartaTitle);
			qqContent.setShareContent(shareContent);
			if (mShareController != null) {
				mShareController.setShareMedia(qqContent);
			}
			break;
		case QZONE:
			QZoneShareContent qzoneContent = new QZoneShareContent();
			qzoneContent.setTargetUrl(shareUrl);
			qzoneContent.setTitle(shartaTitle);
			qzoneContent.setShareContent(shareContent);
			qzoneContent.setShareImage(image);
			if (mShareController != null) {
				mShareController.setShareMedia(qzoneContent);
			}
			break;
		case WEIXIN:
			WeiXinShareContent weiXinContent = new WeiXinShareContent();
			weiXinContent.setTargetUrl(shareUrl);
			weiXinContent.setTitle(shartaTitle);
			weiXinContent.setShareContent(shareContent);
			weiXinContent.setShareImage(image);
			if (mShareController != null) {
				mShareController.setShareMedia(weiXinContent);
			}
			break;
		case WEIXIN_CIRCLE:
			CircleShareContent circleContent = new CircleShareContent();
			circleContent.setTargetUrl(shareUrl);
			circleContent.setTitle(shartaTitle);
			circleContent.setShareContent(shareContent);
			circleContent.setShareImage(image);
			if (mShareController != null) {
				mShareController.setShareMedia(circleContent);
			}
			break;
		default:
			break;
		}
		mShareController.getConfig().openToast();
		mShareController.directShare(context, platform, null);

	}

	public void inviteNewSinaUser(final Activity context, ShareContentBean shareBean) {

		String shareUrl = shareBean.shareUrl;
		int app_icon_id = shareBean.app_icon_id;
		UMImage image = new UMImage(context, app_icon_id);
		image.setTargetUrl(shareUrl);
		SinaShareContent sinaContent = new SinaShareContent();
		String shartaTitle = "我刚下载了“失重app” 这是款街舞软件，希望喜欢街舞的朋友一起来玩哦。（不论何时何地，保留住对街舞那份热爱。）#失重APP#";
		sinaContent.setShareImage(image);
		sinaContent.setTargetUrl(shareUrl);
		sinaContent.setTitle(shartaTitle);
		sinaContent.setShareContent(shartaTitle);
		if (mShareController != null) {
			mShareController.setShareMedia(sinaContent);
		}
		mShareController.getConfig().closeToast();
		mShareController.directShare(context, SHARE_MEDIA.SINA, null);
	}

}
