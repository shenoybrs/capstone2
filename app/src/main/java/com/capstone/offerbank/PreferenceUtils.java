package com.capstone.offerbank;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceUtils {
    private static PreferenceUtils sInstance = null;
    private static Context mAppContext = null;
    private static final Object lock = new Object();
    public static final String SSIDS_LIST ;
    private boolean deviceRootedInfo = false;
    private boolean deviceRootedConf = false;
    //public static final String DEFAULT_SSID_LIST = "{\"ssid\":\"Jionet\"}";

    private static final String PREF_NAME_JIO_ENVIRONMENT_CONFIG ;
    private static final String HEART_BEAT_TIME_STAMP_KEY ;
    private static final String DEVICE_ROOTED_INFO_KEY ;

    static{
        SSIDS_LIST = "ssid_list";

        PREF_NAME_JIO_ENVIRONMENT_CONFIG = "JIO_ENVIRONMENT_CONFIG";
        HEART_BEAT_TIME_STAMP_KEY = "heart_beat_time_stamp_key";
        DEVICE_ROOTED_INFO_KEY = "device_rooted_key";
    }

    public void intiPreferenceUtils(Context cxt) {
        mAppContext = cxt.getApplicationContext();
    }

    public static PreferenceUtils getInstance() {
        synchronized (lock) {
            if (sInstance == null) {
                sInstance = new PreferenceUtils();
            }

        }
        return sInstance;

    }

    private PreferenceUtils() {

    }


    // These below 2 methodes used to save time stamp after every n/w call success and retrive timestamp as well
    public static void storeHeartBeatRecentTimeStamp(long UpdatedTimeStamp) {

        if (mAppContext == null) {
            throw new IllegalArgumentException("Please call initPreference before using ....");

        } else {
            SharedPreferences storeHeartBeatInfoPref = mAppContext.getApplicationContext().getSharedPreferences(
                        PreferenceUtils.PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                        Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = storeHeartBeatInfoPref.edit();
            editor.putLong(HEART_BEAT_TIME_STAMP_KEY, UpdatedTimeStamp);
            editor.commit();

        }
    }


    public  void storeDeviceRootedInfo(boolean isRooted) {

       deviceRootedInfo=isRooted;
    }

    public static long getHeartBeatRecentTimeStamp() {
        if (mAppContext == null) {
            throw new IllegalArgumentException("Please call initPreference before using ....");

        }
        SharedPreferences getHeartBeatPref = mAppContext.getApplicationContext().getSharedPreferences(
                    PreferenceUtils.PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
                long timeStamp = getHeartBeatPref.getLong(HEART_BEAT_TIME_STAMP_KEY, 0);

        return timeStamp;
    }


    public void storeRootConfEnabled(boolean confEnabled) {

        deviceRootedConf = confEnabled;
    }

    public boolean getRootConfEnabled()
    {
        return deviceRootedConf;
    }

    public boolean getDeviceRootedInfo() {

        return deviceRootedInfo;
    }


    public static void storeSSIDValues(Context context, String ssidList) {

        SharedPreferences ssidListPreference;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            ssidListPreference = context.getApplicationContext().getSharedPreferences(
                    PreferenceUtils.PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
        } else {
            ssidListPreference = context.getApplicationContext().getSharedPreferences(
                    PreferenceUtils.PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = ssidListPreference.edit();
        editor.putString(SSIDS_LIST, ssidList);
        editor.commit();
    }


    public static String getSSIDList(Context context) {
        SharedPreferences ssidListPreference;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            ssidListPreference = context.getApplicationContext().getSharedPreferences(
                    PreferenceUtils.PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_MULTI_PROCESS);
        } else {
            ssidListPreference = context.getApplicationContext().getSharedPreferences(
                    PreferenceUtils.PREF_NAME_JIO_ENVIRONMENT_CONFIG,
                    Context.MODE_PRIVATE);
        }
        String ssidList = ssidListPreference.getString(SSIDS_LIST, "[\"Jionet\", \"Jio_Prod\"]");
        return ssidList;
    }


    public static Context getAppContext() throws IllegalArgumentException {
        if (mAppContext == null) {
            throw new IllegalArgumentException("Please call initPreference before using ....");
        }
        return mAppContext;
    }
}