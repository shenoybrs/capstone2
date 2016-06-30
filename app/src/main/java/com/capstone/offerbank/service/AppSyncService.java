package com.capstone.offerbank.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.capstone.offerbank.App;
import com.capstone.offerbank.AppResponse;
import com.capstone.offerbank.AppStoreFactory;
import com.capstone.offerbank.IAppStore;
import com.capstone.offerbank.provider.AppsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppSyncService extends IntentService {
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final int MAX_ATTEMPTS = 5;
    private static final String TAG = "AppSyncService";
    private static int count = 0;
    static PendingIntent pendingIntent = null;


    public AppSyncService() {
        super("AppSyncService");
    }

    public static void resetAlarm(Context ctx) {

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    public static void setAlarm(Context ctx){
        /*
         * Setup an alarm to sync up the items that are found in the JioWorld "Apps" tab
         * This service also does the Update check.
         */

        Intent updateIntent = new Intent(ctx,AppSyncService.class);
        updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (AppsProvider.getJioAppItems(ctx,null,null).size() == 0) {
            // The DB is empty, sync now.
            ctx.startService(updateIntent);
        }

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

         pendingIntent = PendingIntent.getService(ctx,
                0,
                updateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + 60000 ,
                AlarmManager.INTERVAL_HALF_DAY/12,
                pendingIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Syncing bank Apps DB");
        //fetch categories from server
        //no need the retry mechanism as we are trying for 3 times in get categories

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            try {

                IAppStore store = AppStoreFactory.getAppStore(this);
                AppResponse response = store.getApps("", "", 0, 0);



                if (response != null && response.error == null) {
                    processFeaturedApps(response.apps);
                    break;
                } else {
                    //Retry for MAX_ATTEMPTS
                    try {
                        Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                        Thread.sleep(backoff);
                    } catch (InterruptedException e1) {
                        // Finished before we could complete. Exit ...
                        Log.d(TAG, "Thread interrupted: abort remaining retries!");
                        Thread.currentThread().interrupt();
                        break;
                    }

                    backoff *= 2;
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to sync JioApps", e);
            }
        }
    }

    private void processFeaturedApps(List<App> apps) {
        if (apps != null) {
            final ContentResolver cr = getContentResolver();
          //  cr.delete(AppsProvider.CONTENT_URI_CLEAR, null, null);
            List<JioApp> jioApps = new ArrayList<JioApp>();
            count = 0;

            for (App app : apps) {
                JioApp jioApp = new JioApp(app);
                    if (count == apps.size() - 1)
                        cr.insert(AppsProvider.CONTENT_URI_INSERT, jioApp.getValues());
                    else {
                        cr.insert(AppsProvider.CONTENT_URI, jioApp.getValues());
                        count++;
                    }
                    Log.v(TAG, "Added: " + jioApp.mApp.name);
                    jioApps.add(jioApp);
            }
        }
    }




    private class JioApp {
        App mApp;
        byte[] launcherIcon = null;

        public JioApp() {
        }

        public JioApp(App app) {
            mApp = app;
        }

        ContentValues getValues() {
            ContentValues values = new ContentValues();
            values.put(AppsProvider.JioAppsColumns.PACKAGE, mApp.packageName);
            values.put(AppsProvider.JioAppsColumns.ICON, launcherIcon);
            values.put(AppsProvider.JioAppsColumns.TITLE, mApp.name);
//            values.put(JioAppsColumns.DELETABLE, mApp.deletable);
            values.put(AppsProvider.JioAppsColumns.VERSION, mApp.versionCode);
            values.put(AppsProvider.JioAppsColumns.INDEX, mApp.index);
            values.put(AppsProvider.JioAppsColumns.AUTHOR, mApp.author);
            values.put(AppsProvider.JioAppsColumns.PRICE, mApp.price);
            values.put(AppsProvider.JioAppsColumns.ICON_URL, mApp.iconSmall.toString());
            values.put(AppsProvider.JioAppsColumns.RATING_AVG, mApp.ratingsAverage);

            return values;
        }
    }
}
