# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/yuliyan/Development/sdk333/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:

-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
#-ignorewarnings
-dontoptimize

-keepclassmembers class com.shizhong.view.ui.ActivityNewsWebContent$Title{
   public *;
}
-keepclassmembers class com.shizhong.view.ui.ActivityNewsWebContent$Image{
   public *;
}
-keepclassmembers class com.shizhong.view.ui.ActivityNewsWebContent$Content{
   public *;
}


#-keepclassmembers class cn.xx.xx.Activity$AppAndroid {
#  public *;
#}
-optimizationpasses 7  #指定代码的压缩级别 0 - 7
-dontusemixedcaseclassnames  #是否使用大小写混合
-dontskipnonpubliclibraryclasses  #如果应用程序引入的有jar包，并且想混淆jar包里面的class
-dontpreverify  #混淆时是否做预校验（可去掉加快混淆速度）
-verbose #混淆时是否记录日志（混淆后生产映射文件 map 类名 -> 转化后类名的映射
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  #淆采用的算法



-dontwarn
-dontskipnonpubliclibraryclassmembers


-keep class android.** {*; }
-keep public class * extends android.view
-keep public class * extends android.content.pm
-keep public class * extends android.app.Activity  #所有activity的子类不要去混淆
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService #指定具体类不要去混淆

#-libraryjars libs/AMap_Location_V2.5.0_20160526.jar
#-libraryjars libs/android-async-http-1.4.6.jar
#-libraryjars libs/BaiduLBS_Android.jar
#-libraryjars libs/bolts-android-1.2.0.jar
#-libraryjars libs/GetuiSDK2.9.0.0.jar
#-libraryjars libs/gson-2.2.4.jar
#-libraryjars libs/happy-dns-0.2.3.1.jar
#-libraryjars libs/httpmime-4.1.3.jar
#-libraryjars libs/jpush-android-2.1.0.jar
#-libraryjars libs/json_simple-1.1.jar
#-libraryjars libs/library_vollery.jar
#-libraryjars libs/qiniu-android-sdk-7.0.7.jar
#-libraryjars libs/SocialSDK_QQZone_1.jar
#-libraryjars libs/SocialSDK_QQZone_2.jar
#-libraryjars libs/SocialSDK_QQZone_3.jar
#-libraryjars libs/SocialSDK_Sina.jar
#-libraryjars libs/SocialSDK_WeiXin_1.jar
#-libraryjars libs/SocialSDK_WeiXin_2.jar
#-libraryjars libs/umeng_social_sdk.jar
#-libraryjars libs/xiaomi_sdk.jar
-dontwarn org.apache.lang.**
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }



#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}



-keepattributes SourceFile,LineNumberTable
-keepnames class com.parse.** { *; }

# Required for Parse
-keepattributes Signature
-dontwarn com.squareup.**
-dontwarn okio.**


-dontwarn com.easemob.chat.core.EMConnectionManager**

-dontwarn  org.apache.http.conn.ssl**

-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#2.0.9后的不需要加下面这个keep
#-keep class org.xbill.DNS.** {*;}
#另外，demo中发送表情的时候使用到反射，需要keep SmileUtils
-keep class com.easemob.chatuidemo.utils.SmileUtils {*;}
#注意前面的包名，如果把这个类复制到自己的项目底下，比如放在com.example.utils底下，应该这么写（实际要去掉#）
#-keep class com.example.utils.SmileUtils {*;}
#如果使用EaseUI库，需要这么写
-keep class com.easemob.easeui.utils.EaseSmileUtils {*;}

#2.0.9后加入语音通话功能，如需使用此功能的API，加入以下keep
-dontwarn ch.imvs.**
-dontwarn org.slf4j.**
-keep class org.ice4j.** {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}


 -dontshrink
 -dontoptimize
 -dontwarn com.google.android.maps.**
 -dontwarn com.umeng.**
 -dontwarn com.tencent.weibo.sdk.**
 -dontwarn com.facebook.**
 -keep  class javax.**
 -keep   class android.webkit.**

 -keep enum com.facebook.**
 -keepattributes Exceptions,InnerClasses,Signature

 -keep public interface com.facebook.**
 -keep public interface com.tencent.**
 -keep public interface com.umeng.socialize.**
 -keep public interface com.umeng.socialize.sensor.**
 -keep public interface com.umeng.scrshot.**

 -keep public class com.umeng.socialize.* {*;}


 -keep class com.facebook.**
 -keep class com.facebook.** { *; }
 -keep class com.umeng.scrshot.**
 -keep class com.tencent.** {*;}
 -keep class com.umeng.socialize.sensor.**
 -keep class com.umeng.socialize.handler.**
 -keep class com.umeng.socialize.handler.*
 -keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
 -keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

 -keep class im.yixin.sdk.api.YXMessage {*;}
 -keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

 -dontwarn twitter4j.**
 -keep class twitter4j.** { *; }

 -keep class com.tencent.** {*;}
 -dontwarn com.tencent.**
 -keep class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep class com.tencent.open.TDialog$*
 -keep class com.tencent.open.TDialog$* {*;}
 -keep class com.tencent.open.PKDialog
 -keep class com.tencent.open.PKDialog {*;}
 -keep class com.tencent.open.PKDialog$*
 -keep class com.tencent.open.PKDialog$* {*;}

 -keep class com.sina.** {*;}
 -dontwarn com.sina.**
 -keep class  com.alipay.share.sdk.** {
    *;
 }
 -keepnames class * implements android.os.Parcelable {
     public static final ** CREATOR;
 }



 -keep class com.linkedin.** { *; }
 -keepattributes Signature


 -dontwarn com.google.**
 -keep class com.google.gson.** {*;}

 -dontwarn com.google.**
 -keep class com.google.protobuf.** {*;}

-keep  class android.net.http.SslError
-keep  class android.webkit.WebViewClient
-dontwarn android.webkit.WebView
-dontwarn android.webkit.WebViewClient

# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
public <methods>;
protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
public ** writeValueAsString(**);
}
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

-keep class com.fasterxml.jackson.databind.ObjectMapper {
public <methods>;
protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
public ** writeValueAsString(**);
}

-keep class com.squareup.** { *; }
-dontwarn com.parse.ParseOkHttpClient**
-keep class com.parse.ParseOkHttpClient** { *; }
-keep class com.parse.ParseObject**
-keep class com.parse.ParsePinningEventuallyQueue**
-keep class com.parse.ParsePinningEventuallyQueue**
-keep class okio.AsyncTimeout**
-keep class okio.ForwardingSink**

-keep class tv.danmaku.ijk.media.player.IjkMediaPlayer**
-keep class com.squareup.okhttp**
-keep class com.tencent.connect.auth**
-keep class com.tencent.connect**
-keep class com.umeng.socialize.bean.e**
-dontnote okhttp3.**
-dontnote okio.**
-dontnote retrofit2.**
-dontnote pl.droidsonroids.**

# Parse
-keep class com.parse.** { *; }
-dontwarn com.parse.**
-keepattributes SourceFile,LineNumberTable
-keepnames class com.parse.** { *; }
-dontwarn com.squareup.**
-dontwarn android.net.SSLCertificateSocketFactory
-dontwarn android.app.Notification
-keep class bolts.** { *; }
-keepnames class bolts.** { *; }



-dontwarn org.androidannotations.api.rest.**


-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-keep class android.net.http.** { *; }
-dontwarn android.net.http.**

-keep class com.umeng.socialize.sso.CustomHandler**
-keep class com.parse.ParseObject**
-keep class com.parse.ParsePinningEventuallyQueue**
-keep class com.shizhong.view.ui.base.view.tag.TagAdapter**
-keep class com.shizhong.view.ui.base.view.pickview.listener.OnItemSelectedListener**

-keep class com.parse.ParsePush**
-keep class com.parse.ParseQuery**
-keep class com.parse.ParseQueryController**
-keep class com.parse.ParseRESTObjectBatchCommand**
-keep class com.parse.ParseRESTCommand**
-keep class com.parse.ParseRequest**
-keep class u.aly.bo**
-keep class bolts.Task**
-keep class bolts.Capture**
-keep class com.parse.ParseSQLiteDatabase**
-keep class com.parse.ParseSession**
-keep class com.parse.ParseTaskUtils**
-keep class com.parse.ParseUser**
-keep class  com.parse.PushConnection$KeepAliveMonitor**
-keep class com.parse.PushRouter**
-keep class com.parse.TaskQueue**
-keep class com.shizhong.view.ui.base.view.HorizontalScrollViewEx**
-keep class com.shizhong.view.ui.base.view.MyRecyclerView**
-keep class com.shizhong.view.ui.base.view.PullToRefreshLayout**
-keep class com.shizhong.view.ui.base.view.SurfaceVideoView**
-keep class com.shizhong.view.ui.base.view.TextureVideoView**
-keep class com.shizhong.view.ui.base.view.VideoSelectionView**
-keep class com.shizhong.view.ui.base.view.VideoViewTouch**
-keep class com.shizhong.view.ui.base.view.convenientbanner.view.CBLoopViewPager**
-keep class com.shizhong.view.ui.base.view.ijksample.IjkVideoView**
-keep class tv.danmaku.ijk.media.player.IMediaPlayer**
-keep class com.shizhong.view.ui.base.view.ijksample.IjkVideoView**
-keep class com.shizhong.view.ui.base.view.pickview.lib.WheelView**
-keep class com.shizhong.view.ui.base.view.tag.TagFlowLayout**
-keep class com.squareup.okhttp.Cache**
-keep class com.squareup.okhttp.Call**
-keep class com.squareup.okhttp.internal.DiskLruCache**
-keep class com.squareup.okhttp.internal.Util**
-keep class com.squareup.okhttp.internal.http.HttpConnection**
-keep class com.squareup.okhttp.internal.http.HttpEngine**
-keep class com.squareup.okhttp.internal.http.RetryableSink**
-keep class com.squareup.okhttp.internal.http.SpdyTransport**
-keep class com.squareup.okhttp.internal.spdy.FrameReader**
-keep class com.squareup.okhttp.internal.spdy.FrameWriter**
-keep class com.squareup.okhttp.internal.spdy.Header**
-keep class com.squareup.okhttp.internal.spdy.HpackDraft09**
-keep class com.squareup.okhttp.internal.spdy.Http20Draft14**
-keep class com.squareup.okhttp.internal.spdy.NameValueBlockReader**
-keep class com.squareup.okhttp.internal.spdy.PushObserver**
-keep class com.squareup.okhttp.internal.spdy.Spdy**
-keep class com.squareup.okhttp.internal.spdy.SpdyConnection**
-keep class com.squareup.okhttp.internal.spdy.SpdyStream**
-keep class com.squareup.okhttp.internal.spdy.Variant**
-keep class com.tencent.connect.auth.AuthAgent**
-keep class com.tencent.connect.auth.QQAuth**
-keep class com.tencent.connect.common.BaseApi**
-keep class com.tencent.tauth.Tencent**
-keep class com.umeng.socialize.bean.e**
-keep class com.umeng.socialize.controller.b**
-keep class com.umeng.socialize.controller.listener.SocializeListeners$OnSnsPlatformClickListener**
-keep class com.umeng.socialize.sso.UMTencentSsoHandler**
-keep class com.umeng.socialize.sso.a**
-keep class com.umeng.socialize.weixin.controller.UMWXHandler**
-keep class com.viewpagerindicator.CirclePageIndicator**
-keep class com.viewpagerindicator.TabPageIndicator**
-keep class com.xiaomi.mipush.sdk.PushMessageReceiver**
-keep class com.xonami.javaBells.DefaultJingleSession**
-keep class com.xonami.javaBells.JinglePacketHandler**
-keep class internal.org.apache.http.entity.mime.MultipartEntity**
-keep class okio.AsyncTimeout**
-keep class okio.ForwardingSink**
-keep class okio.ForwardingSource**
-keep class tv.danmaku.ijk.media.player.IjkMediaPlayer**
-keep class android.webkit.**
-keep class com.parse.ParseQuery**
-keep class com.parse.ParseQueryAdapter**
-keep class com.parse.ParseQueryController**
-keep class com.parse.ParseRESTCommand**
-keep class com.parse.ParseRESTObjectBatchCommand**
-keep class com.parse.ParseUser**
-keep class com.parse.PushService**
-keep class com.parse.TaskQueue**
-keep class com.parse.TwitterAuthenticationProvider**
-keep class com.shizhong.view.ui.base.view.convenientbanner.view.CBLoopViewPager**
-keep class com.shizhong.view.ui.base.view.tag.TagFlowLayout**



-dontwarn com.android.volley.**
-keep class com.android.volley.** {*;}
-dontwarn android.support.v7.widget.**
-keep class android.support.v7.widget.** {*;}
#-dontwarn android.support.v4.**
#-keep class android.support.v4.** {*;}
#android.support.v4
-dontwarn android.support.v4.**




-keepclasseswithmembernames class * {
    native <methods>;  #保持 native 的方法不去混淆
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);  #保持自定义控件类不被混淆，指定格式的构造方法不去混淆
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}



-keepclasseswithmembers class * {
    void onClick*(...);
}
-keepclasseswithmembers class * {
    *** *Callback(...);
}



-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View); #保持指定规则的方法不被混淆（Android layout 布局文件中为控件配置的onClick方法不能混淆）
}

-keep public class * extends android.view.View {  #保持自定义控件指定规则的方法不被混淆
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclassmembers enum * {  #保持枚举 enum 不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {  #保持 Parcelable 不被混淆（aidl文件不能去混淆）
    public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable #需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）

-keepclassmembers class * implements java.io.Serializable { #保护实现接口Serializable的类中，指定规则的类成员不被混淆
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}




-keepclassmembers class **.R$* {
    public static <fields>;
}


  # keep 所有的 javabean
  -keep class com.shizhong.view.ui.bean.**{*;}
  # keep 泛型
  -keepattributes Signature

  -keep public class * implements java.io.Serializable {
      public *;
  }

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}




-keep class com.shizhong.view.ui.base.view.ijksample**

-dontwarn  com.squareup.okhttp.**
-keep class com.squareup.okhttp.OkHttpClient
-keep class com.squareup.okhttp.Response
-keep class com.squareup.okhttp.ResponseBody
-keep class com.squareup.okhttp.MediaType
-keep class com.squareup.okhttp.Headers
-keep class com.squareup.okhttp.Request
-keep class okio.BufferedSink
-keep  class com.squareup.okhttp.RequestBody
-keep  class okio.BufferedSink





-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn org.apache.commons.codec.binary.**


-keep class com.amap.api.location.**{*;}

-keep class com.amap.api.fence.**{*;}

-keep class com.autonavi.aps.amapapi.model.**{*;}


-dontwarn  android.net.http.**
-keep class android.net.http.SslCertificate
-keep class org.apache.http.conn.scheme.HostNameResolver
-keep class org.apache.http.conn.ssl.SSLSocketFactory





-dontwarn  com.squareup.okhttp.**
-dontwarn  android.webkit.**

-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient


-keep class com.wind.ffmpeghelper.FFmpegNativeHelper{*;}
-keep class com.wind.ffmpeghelper.FFmpegUtils{*;}
-keep class com.wind.ffmpeghelper.VideoHandlerCallBack{*;}
-dontwarn tv.danmaku.ijk.media.playerp.**
-keep class tv.danmaku.ijk.media.player.**{*;}
-keep class tv.danmaku.ijk.media.player.misc.**{*;}
-keep class tv.danmaku.ijk.media.player.exceptions.**{*;}
-keep class tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi{*;}
-keep class tv.danmaku.ijk.media.player.annotations.**{*;}
-keep class tv.danmaku.ijk.media.player.** { *;}
-keep class tv.danmaku.ijk.media.player.AndroidMediaPlayer.**

-keep class com.hyphenate.easeui.widget.emojicon.**{*;}
-keep class com.hyphenate.easeui.widget.chatrow.**{*;}
-keep class com.hyphenate.easeui.widget.photoview.**{*;}
-keep class com.hyphenate.easeui.widget.**{*;}
-keep class com.hyphenate.easeui.**{*;}
-keep class com.hyphenate.easeui.ui.**{*;}
-keep class com.hyphenate.easeui.model.**{*;}
-keep class com.hyphenate.easeui.domain.**{*;}
-keep class com.hyphenate.easeui.controller.**{*;}
-keep class com.hyphenate.easeui.adapter.**{*;}

-keep class com.shizhong.view.ui.base.utils.SmileUtils{*;}

-keepattributes Signature  #过滤泛型（不写可能会出现类型转换错误，一般情况把这个加上就是了）

-keepattributes *Annotation*  #假如项目中有用到注解，应加入这行配置

-keep class **.R$* { *; }  #保持R文件不被混淆，否则，你的反射是获取不到资源id的

-keep class **.Webview2JsInterface { *; }  #保护WebView对HTML页面的API不被混淆
-keepclassmembers class * extends android.webkit.WebViewClient {  #如果你的项目中用到了webview的复杂操作 ，最好加入
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {  #如果你的项目中用到了webview的复杂操作 ，最好加入
     public void *(android.webkit.WebView,java.lang.String);
}
#对WebView的简单说明下：经过实战检验,做腾讯QQ登录，如果引用他们提供的jar，若不加防止WebChromeClient混淆的代码，oauth认证无法回调，反编译基代码后可看到他们有用到WebChromeClient，加入此代码即可。

-keepclassmembernames class com.cgv.cn.movie.common.bean.** { *; }  #转换JSON的JavaBean，类成员名称保护，使其不被混淆

-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**

-dontwarn  tv.danmaku.ijk.media.player.**
-dontwarn android.media.MediaDataSource.**

-dontwarn com.igexin.**
-keep class com.igexin.**{*;}


