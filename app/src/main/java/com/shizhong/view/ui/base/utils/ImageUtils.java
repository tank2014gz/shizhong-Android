package com.shizhong.view.ui.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    private final static String TAG = "ImageUtils";
    public static final int MAX_SIZE_990 = 990;
    public static final int MAX_SIZE_1024 = 1024;
    public static final int MAX_SIZE_640 = 640;

    public static Bitmap readZoomBitmapToMemory(String url, int outWidth,
                                                int outHeight, boolean isMemorySmall) {
        if (TextUtils.isEmpty(url) || !new File(url).exists()) {
            return null;
        }
        int ratote = getJpgRotation(url);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(url, opts);
        int imageWidth = opts.outWidth;
        int imageHeight = opts.outHeight;
        Log.i("imagesize", "原图 imagesize:(" + imageWidth + "," + imageHeight
                + ")");
        String msg = "";
        int halfWidth = imageWidth / 2;
        int halfHeight = imageHeight / 2;

        int scale = 1;
        if (ratote % 180 == 0) {
            if (isMemorySmall) {
                while ((halfWidth / scale) > outWidth
                        || (halfHeight / scale) > outHeight) { // ||
                    scale *= 2;
                }
            } else {
                scale = Math
                        .min(imageWidth / outWidth, imageHeight / outHeight); // min
            }
            msg = "ratote % 180 != 0" + ("inSampleSize=" + scale);
        } else {
            if (imageWidth > outHeight && imageHeight > outWidth) {

                if (isMemorySmall) {
                    while ((halfWidth / scale) > outHeight
                            || (halfHeight / scale) > outWidth) { // ||
                        scale *= 2;
                    }
                } else {
                    scale = Math.min(imageWidth / outHeight, imageHeight
                            / outWidth); // min
                }
            }
        }
        opts.inSampleSize = scale;
        Log.i(TAG, "opts.inSampleSize:" + scale);
        Log.i("MSG", msg);
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Config.ARGB_8888;
        boolean flag = false;
        Bitmap inBmp = null;
        try {
            inBmp = BitmapFactory.decodeFile(url, opts);
        } catch (OutOfMemoryError e) {
            opts.inSampleSize++;
            flag = true;
        }
        while (flag) {
            try {
                inBmp = BitmapFactory.decodeFile(url, opts);
                flag = false;
            } catch (OutOfMemoryError e) {
                opts.inSampleSize++;
                flag = true;
            }
        }
        // Bitmap inBmp = BitmapFactory.decodeFile(url, opts); // 内存中的bitmap对象
        if (inBmp != null && inBmp.isRecycled()) {
            inBmp.recycle();
            inBmp = null;
        }
        System.gc();
        return inBmp;
    }


    public static String getScaleImagePath(String path, Context context,
                                           int maxWidth, boolean isMemorySmall) {
        // Log.i("TAG", "PATH:" + path);
        File file = new File(path);
        Bitmap bitmap = readZoomBitmapToMemory(path, maxWidth, maxWidth,
                isMemorySmall);
        if (bitmap != null) {
            return saveBitmap(bitmap, context, path, file.getName());
        } else {
            return null;
        }

    }

    /**
     * @param bitmap
     * @param fileName
     * @return String 保存后的文件路径
     * @Title: saveBitmap
     * @Description:
     */
    private static String saveBitmap(Bitmap bitmap, Context context,
                                     String path, String fileName) {
        File file = new File(PathUtils.getTempUploadPath(context), fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
            try {
                out.close();
                out = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.gc();
        }

        return null;
    }


    public static int getJpgRotation(String img) {
        if (img == null)
            return 0;
        if (img.endsWith(".jpg") == false && img.endsWith(".JPG") == false
                && img.endsWith(".dat") == false
                && img.endsWith(".dat") == false)
            return 0;
        ExifInterface exif;
        try {
            exif = new ExifInterface(img);
            int r = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (r) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }

        } catch (Exception e) {
        }
        return 0;
    }


    public static Bitmap getRotateBitmap(Bitmap bmp, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                bmp.getHeight(), matrix, false);
        return rotaBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return Degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap resizeWithAspect(Bitmap image, int maxWidth, int maxHeight) {
        Bitmap ret = null;
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            ret = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            if (image != null && !image.isRecycled()) {
                image.recycle();
                image = null;
            }
            return ret;
        } else {
            return ret;
        }
    }

    /**
     * 底片效果
     */
    public static Bitmap Revert(Bitmap bitmap) {
        int color;
        int pixelsR, pixelsG, pixelsB, pixelsA;
        int width, height;
        int[] oldPixels;
        int[] newPixels;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        oldPixels = new int[width * height];
        newPixels = new int[width * height];
        // 获取像素
        bitmap.getPixels(oldPixels, 0, width, 0, 0, width, height);

        for (int i = 1; i < height * width; i++) {
            color = oldPixels[i];
            // 获取RGB分量
            pixelsA = Color.alpha(color);
            pixelsR = Color.red(color);
            pixelsG = Color.green(color);
            pixelsB = Color.blue(color);

            // 转换
            pixelsR = (255 - pixelsR);
            pixelsG = (255 - pixelsG);
            pixelsB = (255 - pixelsB);
            // 均小于等于255大于等于0
            if (pixelsR > 255) {
                pixelsR = 255;
            } else if (pixelsR < 0) {
                pixelsR = 0;
            }
            if (pixelsG > 255) {
                pixelsG = 255;
            } else if (pixelsG < 0) {
                pixelsG = 0;
            }
            if (pixelsB > 255) {
                pixelsB = 255;
            } else if (pixelsB < 0) {
                pixelsB = 0;
            }
            // 根据新的RGB生成新像素
            newPixels[i] = Color.argb(pixelsA, pixelsR, pixelsG, pixelsB);

        }
        // 根据新像素生成新图片
        bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
        return bitmap;

    }

    /**
     * 光晕效果
     *
     * @param bmp
     * @param x   光晕中心点在位图中的x坐标
     * @param y   光晕中心点在位图中的y坐标
     * @param r   光晕的半径
     * @return
     */
    public static Bitmap halo(Bitmap bmp, int x, int y, float r) {
        // 高斯矩阵
        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int delta = 18; // 值越小图片会越亮，越大则越暗

        int idx = 0;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                int distance = (int) (Math.pow(k - x, 2) + Math.pow(i - y, 2));
                // 不是中心区域的点做模糊处理
                if (distance > r * r) {
                    for (int m = -1; m <= 1; m++) {
                        for (int n = -1; n <= 1; n++) {
                            pixColor = pixels[(i + m) * width + k + n];
                            pixR = Color.red(pixColor);
                            pixG = Color.green(pixColor);
                            pixB = Color.blue(pixColor);

                            newR = newR + (int) (pixR * gauss[idx]);
                            newG = newG + (int) (pixG * gauss[idx]);
                            newB = newB + (int) (pixB * gauss[idx]);
                            idx++;
                        }
                    }

                    newR /= delta;
                    newG /= delta;
                    newB /= delta;

                    newR = Math.min(255, Math.max(0, newR));
                    newG = Math.min(255, Math.max(0, newG));
                    newB = Math.min(255, Math.max(0, newB));

                    pixels[i * width + k] = Color.argb(255, newR, newG, newB);

                    newR = 0;
                    newG = 0;
                    newB = 0;
                }
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    // 熔铸
    public static Bitmap hotImage(Bitmap bitmap) {

        int width, height;
        int[] oldPixels;
        int[] newPixels;
        int color;
        int pixelsR, pixelsG, pixelsB, pixelsA;

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        oldPixels = new int[width * height];
        newPixels = new int[width * height];

        // 获取像素
        bitmap.getPixels(oldPixels, 0, width, 0, 0, width, height);

        for (int i = 1; i < height * width; i++) {
            color = oldPixels[i];
            // 获取RGB分量
            pixelsA = Color.alpha(color);
            pixelsR = Color.red(color);
            pixelsG = Color.green(color);
            pixelsB = Color.blue(color);
            // R
            int pixel = pixelsR * 128 / (pixelsG + pixelsB + 1);
            if (pixel < 0) {
                pixel = 0;
            } else if (pixel > 255) {
                pixel = 255;
            }
            pixelsR = pixel;
            // G
            pixel = pixelsG * 128 / (pixelsB + pixelsR + 1);
            if (pixel < 0) {
                pixel = 0;
            } else if (pixel > 255) {
                pixel = 255;
            }
            pixelsG = pixel;
            // B
            pixel = pixelsB * 128 / (pixelsR + pixelsG + 1);
            if (pixel < 0) {
                pixel = 0;
            } else if (pixel > 255) {
                pixel = 255;
            }
            pixelsB = pixel;

            // 根据新的RGB生成新像素
            newPixels[i] = Color.argb(pixelsA, pixelsR, pixelsG, pixelsB);

        }

        bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 图片锐化（拉普拉斯变换）
     *
     * @param bmp
     * @return
     */
    public static Bitmap sharpenImageAmeliorate(Bitmap bmp) {
        // 拉普拉斯矩阵
        int[] laplacian = new int[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int idx = 0;
        float alpha = 0.3F;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + n) * width + k + m];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
                        idx++;
                    }
                }

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 模糊效果(普通算法)
     *
     * @param bmp
     * @return
     */
    public static Bitmap blurImage(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int newColor = 0;

        int[][] colors = new int[9][3];
        for (int i = 1, length = width - 1; i < length; i++) {
            for (int k = 1, len = height - 1; k < len; k++) {
                for (int m = 0; m < 9; m++) {
                    int s = 0;
                    int p = 0;
                    switch (m) {
                        case 0:
                            s = i - 1;
                            p = k - 1;
                            break;
                        case 1:
                            s = i;
                            p = k - 1;
                            break;
                        case 2:
                            s = i + 1;
                            p = k - 1;
                            break;
                        case 3:
                            s = i + 1;
                            p = k;
                            break;
                        case 4:
                            s = i + 1;
                            p = k + 1;
                            break;
                        case 5:
                            s = i;
                            p = k + 1;
                            break;
                        case 6:
                            s = i - 1;
                            p = k + 1;
                            break;
                        case 7:
                            s = i - 1;
                            p = k;
                            break;
                        case 8:
                            s = i;
                            p = k;
                    }
                    pixColor = bmp.getPixel(s, p);
                    colors[m][0] = Color.red(pixColor);
                    colors[m][1] = Color.green(pixColor);
                    colors[m][2] = Color.blue(pixColor);
                }

                for (int m = 0; m < 9; m++) {
                    newR += colors[m][0];
                    newG += colors[m][1];
                    newB += colors[m][2];
                }

                newR = (int) (newR / 9F);
                newG = (int) (newG / 9F);
                newB = (int) (newB / 9F);

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                newColor = Color.argb(255, newR, newG, newB);
                bitmap.setPixel(i, k, newColor);

                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        return bitmap;
    }

    /**
     * 柔化效果(高斯模糊)
     *
     * @param bmp
     * @return
     */
    public static Bitmap blurImageAmeliorate(Bitmap bmp) {
        // 高斯矩阵
        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int delta = 16; // 值越小图片会越亮，越大则越暗

        int idx = 0;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + m) * width + k + n];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * gauss[idx]);
                        newG = newG + (int) (pixG * gauss[idx]);
                        newB = newB + (int) (pixB * gauss[idx]);
                        idx++;
                    }
                }

                newR /= delta;
                newG /= delta;
                newB /= delta;

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);

                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 怀旧效果
     *
     * @param bmp
     * @return
     */
    public static Bitmap oldRemeber(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        int pixColor = 0;
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++) {
            for (int k = 0; k < width; k++) {
                pixColor = pixels[width * i + k];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
                pixels[width * i + k] = newColor;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
