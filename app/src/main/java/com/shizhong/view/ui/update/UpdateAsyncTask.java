package com.shizhong.view.ui.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.CameraManager;
import com.shizhong.view.ui.base.utils.NetworkUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateAsyncTask extends AsyncTask<Integer, Integer, String>{

	 private final static String SETTING_UPDATE_APK_INFO = "setting_updateapkinfo";
	    private final static String UPDATE_DATE = "updatedate";
	    private final static String APK_VERSION = "apkversion";
	    private final static String APK_VERCODE = "apkvercode";
	                                                                                                                   
	    private final static String savePath = CameraManager.getVideoCachePath();
	                                                                                                                   
	    private String fileName;
	                                                                                                                   
	    private Context mContext;
	    private ProgressBar progressView;      //进度条
	    private TextView textView;
	    private AlertDialog downloadDialog;    //下载弹出框
	                                                                                                                   
	    private UpdateApkInfo apkInfo;   //APK更新的详细信息
	                                                                                                                   
	    private boolean interceptFlag = false;  //是否取消下载
	    private boolean sdExists = false;   //是否存在SD卡
	                                                                                                                   
	    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                                                                                                                   
	    public UpdateAsyncTask(Context mContext,UpdateApkInfo apkInfo) {
	        this.mContext = mContext;
	        this.apkInfo = apkInfo;
	        if(apkInfo!=null){
	            fileName = savePath + "/" + apkInfo.getApkName();
	        }
	    }
	                                                                                                                   
	    /**
	     * 升级成功，更新升级日期和版本号，和版本code
	     */
	    private void alearyUpdateSuccess(){
	        SharedPreferences sharedPreference = mContext.getSharedPreferences(SETTING_UPDATE_APK_INFO, 0);
	        sharedPreference.edit().putString(UPDATE_DATE, sdf.format(new Date()))
	        .putString(APK_VERSION, apkInfo.getApkVersion()).putInt(APK_VERCODE, apkInfo.getAplVerCode()).commit();
	    }
	                                                                                                                   
	    /**
	     * 安装apk
	     */
	    private void installApk(){
	        File file = new File(fileName);
	        if(!file.exists()){
	            Log.i("---------软件更新之安装应用-------------", "找不到下载的软件");
	            return;
	        }
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
	        mContext.startActivity(intent);
	    }
	    /**
	     * 检测手机是否存在SD卡
	     */
	    private boolean checkSoftStage(){
	        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //判断是否存在SD卡
	            File file = new File(savePath);
	            if(!file.exists()){
	                file.mkdir();
	            }
	            sdExists = true;
	            return true;
	        }else{
	            Toast.makeText(mContext, "检测到手机没有存储卡,请安装了内存卡后再升级。", Toast.LENGTH_LONG).show();
	            return false;
	        }
	    }
	    @Override
	    protected void onPreExecute() {
	                                                                                                                       
	        if(apkInfo!=null && checkSoftStage()){
	            showDownloadDialog();
	        }
	        super.onPreExecute();
	    }
	    /**
	     * 弹出下载进度对话框
	     */
	    private void showDownloadDialog(){
	        Builder builder = new Builder(mContext);
	        builder.setTitle("正在更新版本");
	        //---------------------------- 设置在对话框中显示进度条 ---------------------------------------
	        final LayoutInflater inflater = LayoutInflater.from(mContext);
	        View view = inflater.inflate(R.layout.updateprogressbar, null);
	        textView = (TextView)view.findViewById(R.id.progressCount_text);
	        textView.setText("进度：0");
	        progressView = (ProgressBar)view.findViewById(R.id.progressbar);
	        builder.setView(view);
	                                                                                                                       
	        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                interceptFlag = true;
	            }
	        });
	        downloadDialog = builder.create();
	        downloadDialog.show();
	    }
	                                                                                                                   
	    @Override
	    protected String doInBackground(Integer... params) {
	                                                                                                                       
	        String result = "";
	        if(apkInfo==null){
	            result = "fail";
	        }else if(!NetworkUtils.checkURL(apkInfo.getApkDownloadUrl())){   //检查apk的下载地址是否可用
	            result = "netfail";
	        }else if(apkInfo!=null && sdExists){
	            InputStream is = null;
	            FileOutputStream fos = null;
	            File file = new File(savePath);
	            if(!file.exists()){
	                file.mkdirs();
	            }
	            try {
	                URL url = new URL(apkInfo.getApkDownloadUrl());
	                URLConnection urlConn = url.openConnection();
	                is = urlConn.getInputStream();
	                int length = urlConn.getContentLength();   //文件大小
	                fos = new FileOutputStream(fileName);
	                                                                                                                               
	                int count = 0,numread = 0;
	                byte buf[] = new byte[1024];
	                                                                                                                               
	                while(!interceptFlag && (numread = is.read(buf))!=-1){
	                    count+=numread;
	                    int progressCount =(int)(((float)count / length) * 100);
	                    publishProgress(progressCount);
	                    fos.write(buf, 0, numread);
	                }
	                fos.flush();
	                result = "success";
	            } catch (Exception e) {
	                e.printStackTrace();
	                result = "fail";
	            }finally{
	                try {
	                    if(fos!=null)
	                        fos.close();
	                    if(is!=null)
	                        is.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    result = "fail";
	                }
	            }
	        }
	        return result;
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        if(downloadDialog!=null){
	            downloadDialog.dismiss();
	        }
	        if(!interceptFlag && "success".equals(result)){
	            alearyUpdateSuccess();
	            installApk();
	        }else if("netfail".equals(result)){
	            Toast.makeText(mContext, "连接服务器失败，请稍后重试。", Toast.LENGTH_LONG).show();
	        }
	        super.onPostExecute(result);
	    }
	                                                                                                                   
	    @Override
	    protected void onProgressUpdate(Integer... values) {
	        int count = values[0];
	        progressView.setProgress(count);   //设置下载进度
	        textView.setText("进度："+count+"%");
	        super.onProgressUpdate(values);
	    }
}
