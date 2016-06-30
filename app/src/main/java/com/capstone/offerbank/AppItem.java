package com.capstone.offerbank;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class AppItem {
    public static final int STATE_NOT_INSTALLED = 0;
    public static final int STATE_INSTALLED = 1;
    public static final int STATE_INSTALLED_UPDATE_AVAILABLE = 2;
    public static final int STATE_COMING_SOON = 3;

    public Intent intent;
    private Drawable icon;
    private String appLabel;
    private String title;
    private String packageName;
    private String versionCode;
    private int state;
    private String author;
    private double price;
    private float ratingsAverage;
    private URL iconUrl = null;
    private String unquieID;
    private String appID;
    private String apkUrl;
    private String group;
    private String category;




    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getSecondaryViewMode() {
        return secondaryViewMode;
    }

    public void setSecondaryViewMode(int secondaryViewMode) {
        this.secondaryViewMode = secondaryViewMode;
    }

    private int secondaryViewMode;




    public String getunquieID() {
        return unquieID;
    }

    public void setunquieID(String unquieID) {
        this.unquieID = unquieID;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public AppItem(Intent intent, CharSequence appLabel, String aTitle, String aVersionCode,
                   String aPackageName, String author, double price, float ratingsAvg,
                   String iconUrl) {
        this.intent = intent;
        this.appLabel = appLabel.toString();
        this.title = aTitle;
        this.versionCode = aVersionCode;
        this.packageName = aPackageName;
        this.author = author;
        this.price = price;
        this.ratingsAverage = ratingsAvg;
        // Default to not installed ...
        this.state = STATE_NOT_INSTALLED;
        try {
            this.iconUrl=new URL(iconUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public AppItem(String packageName, CharSequence appLabel, String aTitle,
                   String aVersionCode, String aPackageName, String author,
                   double price, float ratingsAvg,
                   String iconUrl, String appID)  {
        this.appLabel = (String) appLabel;
        this.title = aTitle;
        this.versionCode = aVersionCode;
        this.packageName = aPackageName;
        this.author = author;
        this.price = price;
        this.ratingsAverage = ratingsAvg;
        try {
            this.iconUrl= new URL(iconUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // Default to not installed ...
        this.state = STATE_NOT_INSTALLED;
        this.appID = appID;
        boolean isAppStoreIntent = true;
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("jiolib://appcenter/appdetail/" + packageName));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public void setStateInstalled() {
        state = STATE_INSTALLED;
    }

    public void setStateUpdateAvailable() {
        state = STATE_INSTALLED_UPDATE_AVAILABLE;
    }

    public void setStateNotInstalled() {
        state = STATE_NOT_INSTALLED;
    }

    public void setStateComingSoon() {
        state = STATE_COMING_SOON;
    }

    public void setState(int aState) {
        state = aState;
    }

    public int getState() {
        return state;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public float getRatingsAverage() {
        return ratingsAverage;
    }

    public String toString() {
        return appLabel;
        // For visually testing state changes in the apps tab ...
        // return appLabel.toString() + " " + "state: " + state;
    }
    public URL getURL() {
        return iconUrl;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }
}
