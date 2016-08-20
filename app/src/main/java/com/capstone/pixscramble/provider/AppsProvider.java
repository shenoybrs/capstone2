package com.capstone.pixscramble.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.capstone.pixscramble.AppItem;

import java.util.ArrayList;
import java.util.List;

public class AppsProvider extends ContentProvider {
    //to get Icon on Snapshot

    static final String AUTHORITY = "com.capstone.pixscramble.provider.apps";
    static final String TABLE_NAME = "pixs";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    public static final Uri CONTENT_URI_CLEAR = Uri.parse("content://" + AUTHORITY + "/"
            + TABLE_NAME + "/clear");
    public static final Uri CONTENT_URI_INSERT = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME
            + "/insert");
    public static final Uri CONTENT_URI_UPDATE = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME
            + "/update");
    /**
     * MIME type for the entire app list
     */
    private static final String LIST_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;
    /**
     * MIME type for an individual app
     */
    private static final String ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
    private static final String TAG = "AppsProvider" ;

    DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    public static List<AppItem> getJioAppItems(Context context,String selection,String[] selectionArgs) {
        List<AppItem> results = new ArrayList<AppItem>();
        PackageManager pm = context.getPackageManager();

        Intent i;
        AppItem jafi = null;

        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(CONTENT_URI, null, selection,selectionArgs ,null);

        if (c != null) {
            if (c.moveToFirst()) {

                int iRead = c.getColumnIndex(JioAppsColumns.READ);
                int iurlIcon = c.getColumnIndex(JioAppsColumns.ICON_URL);

                int size = (int) (96 * context.getResources().getDisplayMetrics().density);

                do {

                } while (c.moveToNext());
            }
            c.close();
        }
        /*try {
            // Manually add the App Center.
            i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setComponent(new ComponentName(context, AppListActivity.class));
            ActivityInfo ai = pm.getActivityInfo(i.getComponent(), 0);
            String title = context.getString(R.string.appstore_name);
            // Version code isn't relevant here. "0" is fine ...
            jafi =
                    new AppItem(i, title, title, "0", i.getComponent().getPackageName(), "",
                            Double.MIN_NORMAL, Float.MIN_NORMAL);
            jafi.setStateInstalled();
            jafi.setIcon(ai.loadIcon(pm));
            results.add(jafi);
        } catch (NameNotFoundException e) {
            // Should never get here
            Log.s(AppsProvider.class, "Failed to load the App Center item", e);
        }*/
        return results;
    }
//This method is no longer in use After Release Android 1.4.5
   /* private static Bitmap getAppIcon(Context context, byte[] data, boolean isInstalled) {

        // Get the image sizes
        int size = (int) (96 * context.getResources().getDisplayMetrics().density);
        int badgeSize = (int) (96 * context.getResources().getDisplayMetrics().density);
        Bitmap tmpBadge = null;
        // Get the badge
        if (isInstalled) {
            tmpBadge =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.tag_update);
        } else {
            tmpBadge =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.tag_new);
        }
        Bitmap badge = Bitmap.createScaledBitmap(tmpBadge, badgeSize, badgeSize, false);
        if (badge != tmpBadge) {
            tmpBadge.recycle();
        }

        // Get the app icon
        Bitmap tmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap result = Bitmap.createScaledBitmap(tmp, size, size, false);
        if (result != tmp) {
            tmp.recycle();
        }

        // Put it all together...
        Bitmap icon = Bitmap.createBitmap(size, size, Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);

        // Draw the app icon
        Paint p = new Paint();
        p.setAlpha(125);
        canvas.drawBitmap(result, 0, 0, p);

        // Draw the badge
        p.setAlpha(255);
        canvas.drawBitmap(badge,0,0,p );//result.getWidth() - badge.getWidth(), result.getHeight()
                //- badge.getHeight(), p);

        return icon;
    }*/

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Cursor result =
                db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder,"9");
        //returns the top 9 images
        result.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(TAG,"The cursor count is " +String.valueOf(result.getCount()));
        return result;
    }

    @Override
    public String getType(Uri uri) {
        long id = -1;
        try {
            id = ContentUris.parseId(uri);
        } catch (Exception e) {
            // eat it
        }
        if (id == -1) {
            return LIST_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long id = 0;
        try {
            Cursor c =
                    query(uri, null,
                            JioAppsColumns.ICON_URL + " = ?", new String[] { values
                                    .getAsString(JioAppsColumns.ICON_URL) }, null);
            if (c.getCount() == 0) {
                id = db.insert(TABLE_NAME, null, values);
            }
            c.close();
        } catch (Exception e) {
            Log.v(TAG, "Failed to insert the app: " + values, e);
        }

        if (id <= 0) {
            return null;
        }

        uri = ContentUris.withAppendedId(uri, id);
        sendNotify(uri);

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        if (uri.equals(CONTENT_URI_CLEAR)) {
            SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
            count = db.delete(TABLE_NAME, null, null);
            if (count > 0) {
                sendNotify(uri);
            }
        } else {
            Cursor c =
                    query(uri, new String[] { JioAppsColumns.READ, JioAppsColumns.ICON_URL },
                            selection, selectionArgs, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    int i = c.getInt(c.getColumnIndex(JioAppsColumns.READ));
                    if (i == 1) {
                        // delete the entry
                        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
                        count = db.delete(TABLE_NAME, selection, selectionArgs);
                        if (count > 0) {
                            sendNotify(uri);
                        }
                    } else {
                        // DB entry is not deletable. send notify so consumers of this data can update.
                        sendNotify(uri);
                    }
                }
                c.close();
            }

        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int count = db.update(TABLE_NAME, values, selection, selectionArgs);
        if (count > 0) {
            sendNotify(uri);
        }

        return count;
    }

    private void sendNotify(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "apps.db";
        private static final int DATABASE_VERSION = 1;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            onUpgrade(db, 0, DATABASE_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            for (int version = oldV + 1; version <= newV; version++) {
                upgradeTo(db, version);
            }
        }



        private void createAppsTableLatest(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            StringBuilder sb = new StringBuilder("CREATE TABLE " + TABLE_NAME + " (");
            sb.append(JioAppsColumns.ID + " INTEGER PRIMARY KEY, ");
            sb.append(JioAppsColumns.ICON_URL + " TEXT NOT NULL, ");
            sb.append(JioAppsColumns.READ + " INTEGER NOT NULL DEFAULT 0 ");
            sb.append(");");
            db.execSQL(sb.toString());
        }



        private void upgradeTo(SQLiteDatabase db, int version) {
            switch (version) {
            case 1:
                createAppsTableLatest(db);
               break;

            default:
                throw new IllegalStateException("Don't know how to upgrade to version " + version);
            }
        }
    }

    public static class JioAppsColumns {
        public static final String ID = BaseColumns._ID;
        public static final String ICON_URL = "icon_url";
        public static final String READ = "read";
    }
}
