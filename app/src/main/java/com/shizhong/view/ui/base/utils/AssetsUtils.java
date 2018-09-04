package com.shizhong.view.ui.base.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yuliyan on 16/1/2.
 */
public class AssetsUtils {
    private final String TAG = "AssetsUtils";
    private final Context mContext;
    private final AssetManager mAssetManager;
    private static AssetsUtils mInstance;
    private final String mDBCacheDirctor;


    private AssetsUtils(Context context) {
        mContext = context;
        mAssetManager = mContext.getAssets();
        mDBCacheDirctor = mContext.getCacheDir().getAbsolutePath();
        Log.i(TAG, mDBCacheDirctor);
    }


    public static AssetsUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AssetsUtils(context);
        }
        return mInstance;
    }

    public void copyDBToSD(final String dbName) {
    	new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				 File file = new File(mDBCacheDirctor + File.separator + dbName);
			        LogUtils.i(TAG, file.getAbsolutePath());
			        if (!file.exists()) {
			            try {
			                file.createNewFile();
			                InputStream source = mAssetManager.open(dbName);
			                OutputStream fs = new FileOutputStream(file);
			                byte[] buffer = new byte[1024];
			                int byteread = 0;
			                while ((byteread = source.read(buffer)) != -1) {
			                    fs.write(buffer, 0, byteread);
			                }
			                fs.close();
			                return true;
			            } catch (IOException e) {
			                ToastUtils.showShort(mContext, e.toString());
			                e.printStackTrace();
			            }
			        }
			        return false;
			}
		}.execute();
       
        
    }

}
