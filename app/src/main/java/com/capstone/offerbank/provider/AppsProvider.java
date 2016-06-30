package com.capstone.offerbank.provider;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.capstone.offerbank.AppItem;

import java.util.ArrayList;
import java.util.List;

public class AppsProvider extends ContentProvider {
    //to get Icon on Snapshot
    public static final int ICON_TYPE_INSTALLED = 1;
    public static final int ICON_TYPE_NEWAPP = 0;
    public static final int ICON_TYPE_UPDATE_AVAIL = 2;
    public static final int ICON_TYPE_COMING_SOON = 3;
    static final String AUTHORITY = "com.capstone.offerbank.provider.apps";
    static final String TABLE_NAME = "apps";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    public static final Uri CONTENT_URI_CLEAR = Uri.parse("content://" + AUTHORITY + "/"
            + TABLE_NAME + "/clear");
    public static final Uri CONTENT_URI_INSERT = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME
            + "/insert");
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
        Cursor c = cr.query(CONTENT_URI, null, selection,selectionArgs , JioAppsColumns.INDEX + " ASC");

        if (c != null) {
            if (c.moveToFirst()) {
                int iTitle = c.getColumnIndex(JioAppsColumns.TITLE);
                int iIcon = c.getColumnIndex(JioAppsColumns.ICON);
                int iPackage = c.getColumnIndex(JioAppsColumns.PACKAGE);
                int iVersion = c.getColumnIndex(JioAppsColumns.VERSION);
                int iAuthor = c.getColumnIndex(JioAppsColumns.AUTHOR);
                int iPrice = c.getColumnIndex(JioAppsColumns.PRICE);
                int iRate = c.getColumnIndex(JioAppsColumns.RATING_AVG);
                int iurlIcon = c.getColumnIndex(JioAppsColumns.ICON_URL);

                int size = (int) (96 * context.getResources().getDisplayMetrics().density);

                do {
                    String pkgName = c.getString(iPackage);
                    try {
                        PackageInfo pi = pm.getPackageInfo(pkgName, 0);
                        Intent intent = new Intent(Intent.ACTION_MAIN, null);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setPackage(pkgName);
                        List<ResolveInfo> list = pm
                                .queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
                        if (list != null) {
                            for (ResolveInfo ri : list) {
                                i = new Intent(Intent.ACTION_MAIN);
                                i.addCategory(Intent.CATEGORY_LAUNCHER);
                                i.setComponent(new ComponentName(ri.activityInfo.packageName,
                                        ri.activityInfo.name));
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                                Drawable icon = ri.activityInfo.loadIcon(pm);
                                if (icon instanceof BitmapDrawable) {
                                    // ensure that the icon is sized appropriately
                                    Bitmap bmp = ((BitmapDrawable) icon).getBitmap();
                                    if (((BitmapDrawable) icon).getBitmap().getHeight() > size) {
                                        Bitmap result =
                                                Bitmap.createScaledBitmap(bmp, size, size, false);
                                        icon = new BitmapDrawable(context.getResources(), result);
                                    }
                                }

                                // Check version and update accordingly ...
                                int versionServer = c.getInt(iVersion);
                                if (versionServer!=0) {
                                    if (versionServer > pi.versionCode) { // Update
                                        // available
                                        // ...
                                        // TODO: getAppIcon w/ update available
                                        // badge ...
                                        CharSequence title = c.getString(iTitle);
                                        String pkg = c.getString(iPackage);
                                        //Removed calling getAppIcon method since canvas way of image processing is reducing the sharpness of image Byte Data
                                   /* byte[] updateIcon = c.getBlob(iIcon);
                                    Bitmap updateBitmap = getAppIcon(context, updateIcon, true);*/
                                        byte[] data = c.getBlob(iIcon);
                                        Bitmap updateBitmap = null;
                                        if (data != null) {
                                            updateBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        }
                                        // Bitmap updateBitmap = getAppIcon(context, data, true);
                                        jafi =
                                                new AppItem(pkg, title, c.getString(iTitle),
                                                        String.valueOf(c.getInt(iVersion)),
                                                        c.getString(iPackage), c.getString(iAuthor), c
                                                        .getDouble(iPrice), c.getFloat(iRate),
                                                        c.getString(iurlIcon),"");
                                        jafi.setStateUpdateAvailable();
                                        if (updateBitmap != null)
                                            jafi.setIcon(new BitmapDrawable(context.getResources(),
                                                    updateBitmap));
                                        jafi.setState(ICON_TYPE_UPDATE_AVAIL);//update available
                                    } else { // Installed, no update ...
                                        byte[] data = c.getBlob(iIcon);
                                        //Removed calling getAppIcon method since canvas way of image processing is reducing the sharpness of image Byte Data
                                        // Bitmap updateBitmap = getAppIcon(context, data, true);

                                        Bitmap updateBitmap = null;
                                        if (data != null) {
                                            updateBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        }

                                        jafi =
                                                new AppItem(i, ri.loadLabel(pm),
                                                        c.getString(iTitle),
                                                        String.valueOf(c.getInt(iVersion)),
                                                        c.getString(iPackage), c.getString(iAuthor),
                                                        c.getDouble(iPrice), c.getFloat(iRate),
                                                        c.getString(iurlIcon));
                                        jafi.setStateInstalled();
                                        if (updateBitmap != null)
                                            jafi.setIcon(new BitmapDrawable(context.getResources(),
                                                    updateBitmap));
                                        jafi.setState(ICON_TYPE_INSTALLED);
                                    }
                                }else
                                {
                                    byte[] data = c.getBlob(iIcon);
                                    //Removed calling getAppIcon method since canvas way of image processing is reducing the sharpness of image Byte Data
                                    // Bitmap updateBitmap = getAppIcon(context, data, true);

                                    Bitmap updateBitmap = null;
                                    if (data != null) {
                                        updateBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    }

                                    jafi =
                                            new AppItem(i, ri.loadLabel(pm),
                                                    c.getString(iTitle),
                                                    String.valueOf(c.getInt(iVersion)),
                                                    c.getString(iPackage), c.getString(iAuthor),
                                                    c.getDouble(iPrice), c.getFloat(iRate),
                                                    c.getString(iurlIcon));
                                    jafi.setStateComingSoon();
                                    if (updateBitmap != null)
                                        jafi.setIcon(new BitmapDrawable(context.getResources(),
                                                updateBitmap));
                                    jafi.setState(ICON_TYPE_COMING_SOON);
                                }


                                results.add(jafi);
                            }
                        }

                    } catch (NameNotFoundException e1) {
                        // App is not installed.
                        CharSequence title = c.getString(iTitle);
                        String pkg = c.getString(iPackage);
                        //Removed calling getAppIcon method since canvas way of image processing is reducing the sharpness of image Byte Data
                       /* byte[] icon = c.getBlob(iIcon);
                        Bitmap bmp = getAppIcon(context, icon, false);*/
                        byte[] data = c.getBlob(iIcon);
                        Bitmap bmp=null;
                       if(data!=null) {
                           bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                       }
                        jafi =
                                new AppItem(pkg, title, c.getString(iTitle), String.valueOf(c
                                        .getInt(iVersion)), c.getString(iPackage), c
                                        .getString(iAuthor), c.getDouble(iPrice),
                                        c.getFloat(iRate),c.getString(iurlIcon),"");
                        int versionServer = c.getInt(iVersion);
                        if (versionServer!=0) {
                            jafi.setStateNotInstalled();
                            if (bmp != null)
                                jafi.setIcon(new BitmapDrawable(context.getResources(), bmp));
                            jafi.setState(ICON_TYPE_NEWAPP);
                        }
                        else
                        {
                            jafi.setStateComingSoon();
                            if (bmp != null)
                                jafi.setIcon(new BitmapDrawable(context.getResources(), bmp));
                            jafi.setState(ICON_TYPE_COMING_SOON);
                        }
                        results.add(jafi);
                    }
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
                db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
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
                    query(uri, new String[] { JioAppsColumns.PACKAGE },
                            JioAppsColumns.PACKAGE + " = ?", new String[] { values
                                    .getAsString(JioAppsColumns.PACKAGE) }, null);
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
                    query(uri, new String[] { JioAppsColumns.DELETABLE, JioAppsColumns.PACKAGE },
                            selection, selectionArgs, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    int i = c.getInt(c.getColumnIndex(JioAppsColumns.DELETABLE));
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
        private static final int DATABASE_VERSION = 4;

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

        private void createAppsTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            StringBuilder sb = new StringBuilder("CREATE TABLE " + TABLE_NAME + " (");
            sb.append(JioAppsColumns.ID + " INTEGER PRIMARY KEY, ");
            sb.append(JioAppsColumns.ICON + " BLOB NOT NULL, ");
            sb.append(JioAppsColumns.PACKAGE + " TEXT NOT NULL, ");
            sb.append(JioAppsColumns.TITLE + " TEXT NOT NULL, ");
            sb.append(JioAppsColumns.INDEX + " INTEGER NOT NULL DEFAULT 0, ");
            sb.append(JioAppsColumns.DELETABLE + " INTEGER NOT NULL DEFAULT 1");
            sb.append(");");
            db.execSQL(sb.toString());
        }

        private void createAppsTableLatest(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            StringBuilder sb = new StringBuilder("CREATE TABLE " + TABLE_NAME + " (");
            sb.append(JioAppsColumns.ID + " INTEGER PRIMARY KEY, ");
            sb.append(JioAppsColumns.ICON + " BLOB , ");
            sb.append(JioAppsColumns.PACKAGE + " TEXT NOT NULL, ");
            sb.append(JioAppsColumns.TITLE + " TEXT NOT NULL, ");
            sb.append(JioAppsColumns.ICON_URL + " TEXT NOT NULL, ");
            sb.append(JioAppsColumns.INDEX + " INTEGER NOT NULL DEFAULT 0, ");
            sb.append(JioAppsColumns.DELETABLE + " INTEGER NOT NULL DEFAULT 1,");
            sb.append(JioAppsColumns.VERSION + " INTEGER NOT NULL DEFAULT 0,");
            sb.append(JioAppsColumns.AUTHOR + " TEXT,");
            sb.append(JioAppsColumns.PRICE + " DOUBLE NOT NULL DEFAULT 0,");
            sb.append(JioAppsColumns.RATING_AVG + " FLOAT NOT NULL DEFAULT 0");
            sb.append(");");
            db.execSQL(sb.toString());
        }

        private void addV2Columns(SQLiteDatabase db) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + JioAppsColumns.VERSION
                    + " INTEGER NOT NULL DEFAULT 0");
        }

        private void addV3Columns(SQLiteDatabase db) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + JioAppsColumns.AUTHOR
                    + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + JioAppsColumns.PRICE
                    + " DOUBLE NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + JioAppsColumns.RATING_AVG
                    + " FLOAT NOT NULL DEFAULT 0");
        }

        private void upgradeTo(SQLiteDatabase db, int version) {
            switch (version) {
            case 1:
                createAppsTable(db);
               break;
            case 2:
                addV2Columns(db);
                break;
            case 3:
                addV3Columns(db);
                break;
            case 4:
                createAppsTableLatest(db);
                break;
            default:
                throw new IllegalStateException("Don't know how to upgrade to version " + version);
            }
        }
    }

    public static class JioAppsColumns {
        public static final String ID = BaseColumns._ID;
        public static final String ICON = "icon";
        public static final String ICON_URL = "icon_url";
        public static final String PACKAGE = "package";
        public static final String TITLE = "title";
        public static final String INDEX = "indx";
        public static final String DELETABLE = "deletable";
        public static final String VERSION = "version";
        public static final String AUTHOR = "author";
        public static final String PRICE = "price";
        public static final String RATING_AVG = "ratingAverage";
    }
}
