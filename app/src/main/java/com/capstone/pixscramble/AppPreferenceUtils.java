package com.capstone.pixscramble;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class AppPreferenceUtils {
    private static final  String TAG = "AppPreferenceUtils";
    private static AppPreferenceUtils sInstance = null;
    private static Context mAppContext = null;
    private static final Object lock = new Object();
    private static final String PREF_NAME_JIO_ENVIRONMENT_CONFIG;
    private static final String LAST_PULL_STATUS;
    private static final String LAST_PULL_VERSION;
    private static final String LAST_FAILED_GLOBAL_LIST_PULL_TIME;
    private static final String STORE_APP_DATA;
    private static final String CACHE_TIME;

    static {
        PREF_NAME_JIO_ENVIRONMENT_CONFIG = "JIO_ENVIRONMENT_CONFIG_APP_CENTER_LOCAL";
        LAST_PULL_STATUS = "last_pull_status";
        LAST_PULL_VERSION = "last_pull_version";
        LAST_FAILED_GLOBAL_LIST_PULL_TIME = "ssid_pull_time";
        STORE_APP_DATA = "store_app_data";
        CACHE_TIME = "cache_time";
    }

    public static final long ONE_DAY_IN_MILLISECONDS = 86400000;

    public void intiPreferenceUtils(Context var1) {
        mAppContext = var1;
    }

    public static AppPreferenceUtils getInstance() {
        Log.d(TAG, "getInstance() called");
        Object var0 = lock;
            if (sInstance == null) {
                sInstance = new AppPreferenceUtils();
            }

        return sInstance;
    }

    private AppPreferenceUtils() {
    }


    public static void storeVersionValues(Context var0, String var1) {
        Log.d(TAG, "storeVersionValues() called with Value:" + var1);
        SharedPreferences var2;
        if (Build.VERSION.SDK_INT >= 9) {
            var2 = var0.getApplicationContext().getSharedPreferences(PREF_NAME_JIO_ENVIRONMENT_CONFIG, 4);
        } else {
            var2 = var0.getApplicationContext().getSharedPreferences(PREF_NAME_JIO_ENVIRONMENT_CONFIG, 0);
        }

        SharedPreferences.Editor var3;
        (var3 = var2.edit()).putString(LAST_PULL_VERSION, var1);
        var3.apply();
    }

    public static String getVersionValues(Context var0) {
        Log.d(TAG, "getVersionValues() called with Value:");
        SharedPreferences var1;
        if (Build.VERSION.SDK_INT >= 9) {
            var1 = var0.getApplicationContext().getSharedPreferences(PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
        } else {
            var1 = var0.getApplicationContext().getSharedPreferences(PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_PRIVATE);
        }

        return var1.getString(LAST_PULL_VERSION, "-1");
    }

    public static Context getAppContext() throws IllegalArgumentException {
        if (mAppContext == null) {
            throw new IllegalArgumentException("Please call initPreference before using ....");
        } else {
            return mAppContext;
        }
    }

    public static Long getLastFailedSSIDPullTime(Context context) {
        SharedPreferences timePreference;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            timePreference = context.getApplicationContext().getSharedPreferences(
                    PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
        } else {
            timePreference = context.getApplicationContext().getSharedPreferences(
                    PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_PRIVATE);
        }

        Long ssidLastTime = timePreference.getLong(LAST_FAILED_GLOBAL_LIST_PULL_TIME, 0);
        return ssidLastTime;
    }

    public static void setLastFailedSSIDPullTime(Context context, Long time) {

        SharedPreferences timePreference;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            timePreference = context.getApplicationContext().getSharedPreferences(
                    PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
        } else {
            timePreference = context.getApplicationContext().getSharedPreferences(
                    PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = timePreference.edit();
        editor.putLong(LAST_FAILED_GLOBAL_LIST_PULL_TIME, time);
        editor.apply();
    }


    public boolean isRequiredUpdated(Context mContext, String key) {
        boolean update = false;
        SharedPreferences var1;
        if (Build.VERSION.SDK_INT >= 9) {
            var1 = mContext.getApplicationContext().getSharedPreferences(PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
        } else {
            var1 = mContext.getApplicationContext().getSharedPreferences(PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_PRIVATE);
        }
        long store_time = var1.getLong(CACHE_TIME + key, 0);
        if (store_time != 0) {
            // its completed 24 hrs please get data from server
            update = is24HrsCompleted(store_time);
        } else {
            update = true;
        }

        return update;
    }

    // this method is used to determine
    private boolean is24HrsCompleted(Long store_time) {
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - store_time;
        return TimeUnit.MILLISECONDS.toHours(difference) >= 24;

    }

    public void resetCache(Context mContext, String key) {
        SharedPreferences timePreference;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            timePreference = mContext.getApplicationContext().getSharedPreferences(
                    PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
        } else {
            timePreference = mContext.getApplicationContext().getSharedPreferences(
                    PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_PRIVATE);
        }


        SharedPreferences.Editor editor = timePreference.edit();
        editor.putLong(CACHE_TIME + key, 0);
        editor.putString(STORE_APP_DATA + key, null);
        editor.apply();
    }


    public PixResponse getStoredData(Context mContext, String key) {
        PixResponse response = null;

        SharedPreferences var1;
        if (Build.VERSION.SDK_INT >= 9) {
            var1 = mContext.getApplicationContext().getSharedPreferences(PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
        } else {
            var1 = mContext.getApplicationContext().getSharedPreferences(PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_PRIVATE);
        }
        String data = var1.getString(STORE_APP_DATA + key, null);
        if (data != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<PixResponse>() {
            }.getType();
            response = gson.fromJson(data, type);

        }
        return response;
    }




}