package com.capstone.offerbank;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.offerbank.provider.AppsProvider;

import java.util.List;


public class AppsAdapter extends CursorAdapter {

    private final int INSTALL = 0;
    private final int UN_INSTALL = 1;
    private final int UPDATE = 2;
    private final int OPEN = 3;
    private final int COMING_SOON = 4;

    public AppsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_card_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        Holder h = new Holder(view);
        view.setTag(h);

        final Holder vh = (Holder) view.getTag();




        vh.tvTitle.setText(cursor.getString(
                cursor.getColumnIndex(AppsProvider.JioAppsColumns.TITLE)));

        vh.tvVersion.setText(cursor.getString
                (cursor.getColumnIndex(AppsProvider.JioAppsColumns.VERSION)));

        //vh.menuView.setVisibility(View.VISIBLE);
            /*if (jioAppItem.getSecondaryViewMode() != 1) {
                vh.menuView.setVisibility(View.INVISIBLE);
            }*/

        final int currentState = getStateForPackage
                (cursor.getString(cursor.getColumnIndex(AppsProvider.JioAppsColumns.PACKAGE)),
                        Integer.parseInt(cursor.getString
                                (cursor.getColumnIndex(AppsProvider.JioAppsColumns.VERSION))));
        switch (currentState) {
            case INSTALL:
                vh.tvVersion.setText("New");
                break;
            case UN_INSTALL:
                vh.tvVersion.setText("Installed");
                break;
            case UPDATE:
                vh.tvVersion.setText("Update");
                break;
            case OPEN:
                vh.tvVersion.setText("Installed");
                break;
            case COMING_SOON:
                vh.tvVersion.setText("Coming Soon");
                break;
        }

        vh.appIcon.setDefaultImageResId(R.drawable.app_default_logo);
        vh.appIcon.setErrorImageResId(R.drawable.app_default_logo);
        if (JSSCommonService.getInstance(context.getApplicationContext()).getImageLoader() != null) {
            vh.appIcon.setImageUrl(cursor.getString(cursor.getColumnIndex
                    (AppsProvider.JioAppsColumns.ICON_URL)).toString(),
                    JSSCommonService.getInstance(context.getApplicationContext()).getImageLoader());
        }



    }





    private void doClickAction(AppItem appItem) {

        // Before proceeding, check for Network.
        final boolean networkAvailable = CommonUtils
                .networkAvailable(mContext.getApplicationContext());

        if (!networkAvailable) {
            AppUtils.getAppCenterDialog(mContext, mContext.getString(R.string.network_availability_title),
                    mContext.getString(R.string.network_availability_description), true, false, null).show();
            return;
        }

        if (appItem.getSecondaryViewMode() == 2) {
            /*Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + appItem.getPackageName()));
                    startActivity(intent);*/
            openApp(mContext, appItem.getPackageName(), appItem.getApkUrl());
        } else if (appItem.getSecondaryViewMode() == 4) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appItem.getApkUrl()));
            mContext.startActivity(browserIntent);
        } else {
            Intent i = appItem.intent;
            Bundle bundle = new Bundle();
            if (appItem.getState() == AppItem.STATE_NOT_INSTALLED) {
                String value = "jioworld";
                bundle.putString("installSource", value);
            } else if (appItem.getState() == AppItem.STATE_INSTALLED) {
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("bank://appcenter/appdetail/" + appItem.getPackageName()));
           /* Intent intent = new Intent(getActivity(),AppDetailActivity.class);
            i = intent;*/
            }
            /*Intent intent = new Intent(mContext, AppDetailActivity.class);
            i = intent;
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            bundle.putString("packageName", appItem.getPackageName());
            bundle.putInt("SecondaryViewValue", appItem.getSecondaryViewMode());
            i.putExtras(bundle);
            if (mContext != null) {
                mContext.startActivity(i);
            }*/
        }
    }

    private void openApp(Context context, String packageName, String appUrl) {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        boolean marketFound = false;

        if (appUrl.contains("play.google.com/store")) {
            // find all applications able to handle our rateIntent
            final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
            for (ResolveInfo otherApp : otherApps) {
                // look for Google Play application
                if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                    ActivityInfo otherAppActivity = otherApp.activityInfo;
                    ComponentName componentName = new ComponentName(
                            otherAppActivity.applicationInfo.packageName,
                            otherAppActivity.name
                    );
                    rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    rateIntent.setComponent(componentName);
                    context.startActivity(rateIntent);
                    marketFound = true;
                    break;
                }
            }
        }
        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
            context.startActivity(webIntent);
        }
    }

    private int getStateForPackage(String pkgName, int versionServer) {
        PackageManager pm = mContext.getPackageManager();
        int appState = -1;
        try {
            PackageInfo pi = pm.getPackageInfo(pkgName, 0);
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(pkgName);

            List<ResolveInfo> list = pm
                    .queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
            if (list != null) {
                for (ResolveInfo ri : list) {
                    // Check version and update accordingly ...
                    //int versionServer = c.getInt(iVersion);
                    if (versionServer != 0) {
                        if (versionServer > pi.versionCode) { // Update available

                            appState = AppStoreConstants.ICON_TYPE_UPDATE_AVAIL;//update available
                        } else { // Installed, no update ...
                            appState = AppStoreConstants.ICON_TYPE_INSTALLED;
                        }
                    } else {

                        appState = AppStoreConstants.ICON_TYPE_COMING_SOON;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e1) {
            if (versionServer != 0) {
                appState = AppStoreConstants.ICON_TYPE_NEWAPP;
            } else {
                appState = AppStoreConstants.ICON_TYPE_COMING_SOON;
            }
        }
        return appState;
    }





    public interface ContentChangeCallback {
        void onContentChange();
    }

    private void openClick(String packageName) {
        Intent LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        mContext.startActivity(LaunchIntent);
    }

    private void unInstallClick(String packageName) {
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", packageName, null));
        mContext.startActivity(uninstallIntent);
    }

    private void installClick(final App mApp) {

        final boolean networkAvailable = CommonUtils
                .networkAvailable(mContext);
        if (networkAvailable) {
            if (!(CommonUtils.isDeviceConnectedTo2Gor3G(mContext) )) {
                CommonUtils.IcaptivePortalListener iCaptivePortalListener = new CommonUtils.IcaptivePortalListener() {
                    @Override
                    public void onResponse(boolean lockStatus) {

                        if (!lockStatus) //means there is no captive portal blockage
                        {
                            installApk(mApp);
                        } else {
                            networkNotAvailable();
                        }
                    }
                };

            } else {
                installApk(mApp);
            }
        } else {
            networkNotAvailable();
        }
    }

    private void installApk(App mApp) {
        long mDownloadId = Prefs.getInstance().getDownloadIdForPackage(mApp.packageName);
        int status = -1;

    }

    private void networkNotAvailable() {
        AppUtils.getAppCenterDialog(mContext, mContext.getString(R.string.network_availability_title), mContext.getString(R.string.network_availability_description),
                true, false, null).show();
    }

    private int updateDownloadStatusForPackage(String mPackageName) {
        int status = -1;
        Long mDownloadId = Prefs.getInstance().getDownloadIdForPackage(mPackageName);

        if (mDownloadId != -1) {
        }
        return status;
    }


    public class Holder {
        final NetworkImageView appIcon;
        final TextView tvTitle;
        final TextView tvVersion;
        //final RelativeLayout menuView;

        public Holder(View view) {
            appIcon = (NetworkImageView) view.findViewById(R.id.app_jionetwork_icon);
            tvTitle = (TextView) view.findViewById(R.id.app_item_title);
            tvVersion = (TextView) view.findViewById(R.id.app_version_txt_view);
            //menuView = (RelativeLayout) view.findViewById(R.id.menu_layout);
            //ImageView app_menu_icon = (ImageView) view.findViewById(R.id.app_menu_icon);
        }
    }
}