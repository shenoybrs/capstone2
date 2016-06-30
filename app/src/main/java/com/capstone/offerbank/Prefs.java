package com.capstone.offerbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * This class provides a common access point for working with SharedPreferences.
 * 
 * @author tombollwitt
 * 
 */
public class Prefs {

    private final Context ctx;

    private static final String KEY_DOWNLOAD_ID ;

    private static Prefs sInstance;
    private static final String KEY_SKIP_DELETE ;

    private static final String KEY_APP_OBJECT;

    static{
        KEY_DOWNLOAD_ID = "KEY_DOWNLOAD_ID:";
        KEY_SKIP_DELETE = "KEY_SKIP_DELETE";
        KEY_APP_OBJECT = "KEY_APP_OBJECT";
    }

    private Prefs(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    public static Prefs getInstance() {
        if (sInstance == null) {
            sInstance = new Prefs(PreferenceUtils.getInstance().getAppContext());
        }
        return sInstance;
    }

    /**
     * Get the Download ID for the given packageName or -1 if it does not exist.
     * 
     * @param packageName
     * @return
     */
    public long getDownloadIdForPackage(String packageName) {
        return getPreference(KEY_DOWNLOAD_ID + packageName, -1l);
    }

    /**
     * Set the Download ID for the given packageName
     * 
     * @param packageName
     * @param downloadId
     */
    public void setDownloadIdForPackage(String packageName, long downloadId) {
        setPreference(KEY_DOWNLOAD_ID + packageName, downloadId);
    }


    //These method's for for skip the delete when un-installing, we need the
    public void setPersistFlag(String packageName,boolean flag) {
        setPreference(KEY_SKIP_DELETE+packageName,true);
    }
    public boolean getPersistFlag(String packageName) {
        return getPreference(KEY_SKIP_DELETE+packageName, false);
    }

    /*
     * PRIVATE METHODS
     */

    private boolean hasPreference(String key) {
        return getPrefs().contains(key);
    }

    private void removePreference(String key) {
        getPrefs().edit().remove(key).commit();
    }

    private boolean getPreference(String key, boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    private float getPreference(String key, float defaultValue) {
        return getPrefs().getFloat(key, defaultValue);
    }

    private int getPreference(String key, int defaultValue) {
        return getPrefs().getInt(key, defaultValue);
    }

    private long getPreference(String key, long defaultValue) {
        return getPrefs().getLong(key, defaultValue);
    }

    private String getPreference(String key, String defaultValue) {
        return getPrefs().getString(key, defaultValue);
    }

    private void setPreference(String key, boolean value) {
        Editor editor = getPrefs().edit();
        editor.putBoolean(key, value).apply();
    }

    private void setPreference(String key, float value) {
        Editor editor = getPrefs().edit();
        editor.putFloat(key, value).apply();
    }

    private void setPreference(String key, int value) {
        Editor editor = getPrefs().edit();
        editor.putInt(key, value).apply();
    }

    private void setPreference(String key, long value) {
        Editor editor = getPrefs().edit();
        editor.putLong(key, value).apply();
    }

    private void setPreference(String key, String value) {
        Editor editor = getPrefs().edit();
        editor.putString(key, value).apply();
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Stores the app object w.r.t downloadId into preference.
     *
     * @param downloadId downloadId of app being downloaded.
     * @param app App object to store.
     */
    public void storeBankObject(long downloadId, BankSelectActivity.BankNames app) {
        setAppAsJson(downloadId, app);
    }

    private void setAppAsJson(long downloadId, BankSelectActivity.BankNames app){
        Editor editor = getPrefs().edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(app);

        editor.putString(KEY_APP_OBJECT + downloadId, jsonFavorites);
        editor.apply();
    }

    /**
     * Returns the app object associated with respective downloadId.
     *
     * @param downloadId downloadId of app whose download has been completed.
     * @return app object associated with respective downloadId
     */
    public String getAppObject(long downloadId) {
        return getPreference(KEY_APP_OBJECT + downloadId, "");
    }

    /**
     * Removes the app object when app is installed in device
     * @param downloadId
     */
    public void removeAppObject(long downloadId){
        removePreference(KEY_APP_OBJECT + downloadId);
    }


}
