package com.hyphenate.easeui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;

import com.easemob.util.EMLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuliyan on 16/7/28.
 */
public class BitmapUtils {

    public  Bitmap createImageThumbnail(String filepath,int newWidth,int newheight){
        BitmapFactory.Options opts=new BitmapFactory.Options();
        //此时并不会为  BitmapFactory.decodeFile(filepath, opts)分配空间
        //因为已经设置了inJustDecodeBounds为fales;
        opts.inJustDecodeBounds=true;
        //此时opts已经包含了未裁剪的宽高
        BitmapFactory.decodeFile(filepath, opts);

        //得到原图片的宽高
        int oldHeight=opts.outHeight;
        int oldWidth=opts.outWidth;

        //获取新旧图片的比例
        int rationHeight=oldHeight/newheight;
        int rationWidth=oldWidth/newWidth;
        opts.inSampleSize=rationHeight>rationWidth?rationWidth:rationHeight;

        //确定bitmap的单位字节像素
        opts.inPreferredConfig= Bitmap.Config.RGB_565;
        opts.inJustDecodeBounds=false;
        Bitmap bm=BitmapFactory.decodeFile(filepath, opts);
        return bm;
    }





    public static  Bitmap createImageThumbnail(Bitmap bitmap,int newWidth,int newheight){


        BitmapFactory.Options opts=new BitmapFactory.Options();
        //此时并不会为  BitmapFactory.decodeFile(filepath, opts)分配空间
        //因为已经设置了inJustDecodeBounds为fales;
        opts.inJustDecodeBounds=true;
        //此时opts已经包含了未裁剪的宽高
        BitmapFactory.decodeStream(Bitmap2IS(bitmap),new Rect(),opts);

        //得到原图片的宽高
        int oldHeight=opts.outHeight;
        int oldWidth=opts.outWidth;

        //获取新旧图片的比例
        int rationHeight=oldHeight/newheight;
        int rationWidth=oldWidth/newWidth;
        opts.inSampleSize=rationHeight>rationWidth?rationWidth:rationHeight;

        //确定bitmap的单位字节像素
        opts.inPreferredConfig= Bitmap.Config.RGB_565;
        opts.inJustDecodeBounds=false;
        Bitmap bm=  BitmapFactory.decodeStream(Bitmap2IS(bitmap),new Rect(),opts);
        return bm;
    }


    private static InputStream  Bitmap2IS(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }




}
