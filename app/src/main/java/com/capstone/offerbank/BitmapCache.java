package com.capstone.offerbank;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An {@link ImageCache} implementation that saves images to the Cache
 * Directory.
 */
public class BitmapCache implements ImageCache {

    private static final String TAG = "BitmapCache";
    private final String mBasePath;

    private int mMaxImageDimension;



    /**
     * Constructor
     *
     * @param ctx
     */
    public BitmapCache(Context ctx) {
        //TODO: Need to look into a DiskLruCache option.
        File cacheDir = ctx.getCacheDir();
        mBasePath = cacheDir.getAbsolutePath() + File.separator + "pimg";
        File f = new File(mBasePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        initCacheDir(ctx);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) ctx.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        if (screenWidth < screenHeight) {
            mMaxImageDimension = screenWidth;
        } else {
            mMaxImageDimension = screenHeight;
        }
    }

    private void initCacheDir(Context ctx){

        File f = new File(mBasePath);
        if(!f.exists()){
            f.mkdirs();
        }
        //Clear the file cache.
        deleteRecursive(f);
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }


    @Override
    public Bitmap getBitmap(String key) {
        String pathName = mBasePath + File.separator + key.hashCode();
        File file = new File(pathName);
        Bitmap thumbnail = null;
        try {
            if(file.exists())
                thumbnail = BitmapFactory.decodeFile(pathName);
        } catch (OutOfMemoryError e) {
            // better to show content without images than crash here
            Log.e(TAG, "Failed to decode bitmap from cache");
        }
        return thumbnail;
    }

    @Override
    public void putBitmap(String key, Bitmap bmp) {
        FileOutputStream out = null;
        try {
            String pathName = mBasePath + File.separator + key.hashCode();


            File f = new File(mBasePath);
            if (!f.exists()) {
                f.mkdirs();
            }

            out = new FileOutputStream(pathName);

            int width = bmp.getWidth();
            int height = bmp.getHeight();

            int targetWidth;
            int targetHeight;

            if (width >= height && width > mMaxImageDimension) {
                // too big landscape image
                float scaleFactor = (float) mMaxImageDimension / (float) width;
                targetWidth = (int) ((float) width * scaleFactor);
                targetHeight = (int) ((float) height * scaleFactor);

            } else if (height > width && height > mMaxImageDimension) {
                // too big portrait image
                float scaleFactor = (float) mMaxImageDimension / (float) height;
                targetWidth = (int) ((float) width * scaleFactor);
                targetHeight = (int) ((float) height * scaleFactor);
            } else {
                targetWidth = width;
                targetHeight = height;
            }

            Bitmap storedBitmap = Bitmap.createScaledBitmap(
                    bmp, targetWidth, targetHeight, false);
            storedBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
        } catch (Exception e) {
            Log.e(TAG, "Failed to insert a bitmap to cache", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // eat it.
                }
            }
        }
    }
}
