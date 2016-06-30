package com.capstone.offerbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ProviderInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class JSSCommonService {

    private static final String TAG = JSSCommonService.class.getSimpleName();

    public static boolean sIsDebug = false;
    private static final String PREF_FILE_NAME = "jio_general_prefs";
    private static final String PREF_OWNER_KEY = "is_owner_app";
    private static final String PREF_FIRST_SSO_APP = "is_first_app";
    private static final String PREF_INIT_DONE = "InitDone";
    private static JSSCommonService sInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private boolean mInitInProgress = false;
    private ProviderInfo mProviderInfo;
    private Context mContext;
    private boolean mIsOwner = false;
    private boolean mInitDone = false;
    private boolean mIsFirstApp = true;
    private SharedPreferences mPrefs;
    private boolean isZLAMode;


    public static synchronized JSSCommonService getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new JSSCommonService(context);
        }
        return sInstance;
    }

    private JSSCommonService(Context context) {
        mContext = context.getApplicationContext();

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext, null/*VolleyUtils.getHttpStack()*/);
        }
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache(mContext));
        }

    }


    public ImageLoader getImageLoader() {
        return mImageLoader;
    }


    public Context getContext() {
        return mContext;
    }

}