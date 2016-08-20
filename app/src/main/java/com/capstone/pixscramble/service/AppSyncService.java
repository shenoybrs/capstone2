package com.capstone.pixscramble.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.capstone.pixscramble.PixResponse;
import com.capstone.pixscramble.IPixStore;
import com.capstone.pixscramble.Pix;
import com.capstone.pixscramble.PixStoreFactory;
import com.capstone.pixscramble.provider.AppsProvider;

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
                System.currentTimeMillis() + 6000 ,
                AlarmManager.INTERVAL_DAY,
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

                IPixStore store = PixStoreFactory.getPixStore(this);
                PixResponse response = store.getPixs();



                if (response != null && response.error == null) {
                    processPix(response.pixs);
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

    private void processPix(List<Pix> pixs) {
        if (pixs != null) {
            final ContentResolver cr = getContentResolver();
          //  cr.delete(AppsProvider.CONTENT_URI_CLEAR, null, null);
            count = 0;

            for (Pix pix : pixs) {
                PixContent pixContent = new PixContent(pix);
                cr.insert(AppsProvider.CONTENT_URI_INSERT, pixContent.getValues());
            }
        }
    }




    private class PixContent {
        Pix mPix;
        public PixContent(Pix pix) {
            mPix = pix;
        }

        ContentValues getValues() {
            ContentValues values = new ContentValues();
            values.put(AppsProvider.JioAppsColumns.ICON_URL, mPix.m.toString());
            //inserting the value of read as unread
            values.put(AppsProvider.JioAppsColumns.READ, 0);
            return values;
        }
    }
}
