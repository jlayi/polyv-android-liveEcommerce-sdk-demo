package com.easefun.polyv.livecommon.utils.imageloader.glide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.easefun.polyv.foundationsdk.utils.PolyvAppUtils;
import com.easefun.polyv.foundationsdk.utils.PolyvSDCardUtils;

import java.io.File;
import java.io.FileOutputStream;


public class PLVImageUtils {
    private static final int allowLength = 2 * 1024 * 1024;
    public static Bitmap compressImage(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.isFile() || file.length() < allowLength / 2)
            return null;
        //13.4m-80-5.3m，60-3.3m，40-2.3m，20-1.1m
        long rate = file.length() / (1024 * 1024);
        final int startQuality = rate > 20 ? 20 : (rate > 15 ? 30 : (rate > 10 ? 40 : (rate > 5 ? 55 : 70)));
        String tmpFilePath;
        compressImage(filePath, tmpFilePath = createTmpFile(file).getAbsolutePath(), startQuality, true);
        return BitmapFactory.decodeFile(tmpFilePath);
    }

    private static File createTmpFile(File srcFile) {
        File tmpImageFile = new File(PolyvSDCardUtils.createPath(PolyvAppUtils.getApp(), "PolyvImg/tmp"), srcFile.getName());
        if (tmpImageFile.getAbsolutePath().equals(srcFile.getAbsolutePath())) {
            tmpImageFile = new File(tmpImageFile.getParent(), "nc_" + tmpImageFile.getName());
        }
        PolyvSDCardUtils.createNoMediaFile(tmpImageFile.getParent());
        return tmpImageFile;
    }

    public static String compressImage(String filePath, String targetPath, int quality, boolean isSampleSize) throws Exception {
        Bitmap bm = isSampleSize ? getSmallBitmap(filePath) : BitmapFactory.decodeFile(filePath);
//        int degree = readPictureDegree(filePath);//获取相片拍摄角度
//        if (degree != 0) {//旋转照片角度，防止头像横着显示
//            bm = rotateBitmap(bm, degree);
//        }
        File outputFile = new File(targetPath);
        FileOutputStream out = null;
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
            } else {
                outputFile.delete();
            }
            out = new FileOutputStream(outputFile);
            boolean result = bm.compress(Bitmap.CompressFormat.JPEG, quality, out);//可以为0
            if (!result)
                throw new Exception("compress fail");
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null)
                out.close();
        }
        return outputFile.getPath();
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(filePath, options, 500, 500);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    public static int calculateInSampleSize(String filePath, BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
//        int degree = readPictureDegree(filePath);
//        if (degree == 270 || degree == 90) {
//            height = options.outWidth;
//            width = options.outHeight;
//        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

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
        } catch (Exception e) {
        }
        return degree;
    }

    public static int[] getImgWh(String imgFilePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//不分配内存
        BitmapFactory.decodeFile(imgFilePath, options);//返回null
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
//        int degree = readPictureDegree(imgFilePath);
//        if (degree == 270 || degree == 90) {
//            imageHeight = options.outWidth;
//            imageWidth = options.outHeight;
//        }
        return new int[]{imageWidth, imageHeight};
    }
}
