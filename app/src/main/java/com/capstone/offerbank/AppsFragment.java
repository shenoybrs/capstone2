package com.capstone.offerbank;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.offerbank.provider.AppsProvider;

import java.util.ArrayList;
import java.util.List;


public class AppsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int APP_LOADER = 0;
    private static final String EVENT_SUGGEST_INSTALL = "JW_SI";
    private static final String EVENT_SUGGEST_LAUNCH = "JW_SL";
    private static final String EVENT_SUGGEST_UPGRADE = "JW_SU";
    private static final String EVENT_SUGGEST_COMINGSOON = "JW_CS";

    public static final int DETAIL_ACTIVITY_RESULT_LOGOUT = 12;
    private static final String TAG = "AppsFragment";
    private static int DIALOG_NETWORK_ERROR = 101;
    private final static int DIALOG_LOGOUT_ERROR = 111;
    private BroadcastReceiver mAppsReceiver;
    private TextView logs_msg;
    private Button retry;
    private ProgressBar progress_app;
    private String groupName, appCategory ,tablet;
    private String where;
    private String[] banks;
    private final int GET_APP_LIST_ID = 105;
    private BroadcastReceiver mLoginReceiver;
    private GridView grid;
    private RelativeLayout grid_info;
    private AppsAdapter mAppAdapter;
    private List<AppItem> mAppItems = null;
    public static boolean isUserLoggedOut = false;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Intent movieIntent, AppItem appModel);
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(APP_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        View v = getView();
        if (v != null) {
            grid = (GridView) v.findViewById(R.id.grid);
            grid.setNumColumns(2);
            grid.invalidate();
        }
    }

    @Override
    public View
    onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_apps_one, container, false);

       /* Cursor cur = getActivity().getContentResolver().query(AppsProvider.CONTENT_URI,
                null, null, null, null);*/

        mAppAdapter = new AppsAdapter(getActivity(),null,0);

        grid = (GridView) v.findViewById(R.id.grid);
        grid_info = (RelativeLayout) getActivity().findViewById(R.id.grid_info);

        grid.setAdapter(mAppAdapter);

        ImageButton backArrow = (ImageButton) v.findViewById(R.id.back_arrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        grid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                try {

                    final boolean networkAvailable = CommonUtils
                            .networkAvailable(getActivity());
                    if (networkAvailable) {
                        // code has to write here
                        launchApplication(position);
                    } else {
                        showNetworkErrorDialog();
                    }
                    //launchApplication(position);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        logs_msg = (TextView) v.findViewById(R.id.loading_app);
        retry = (Button) v.findViewById(R.id.retry_app);
        progress_app = (ProgressBar) v.findViewById(R.id.progress_app);
        AppResponse response = AppPreferenceUtils.getInstance().getStoredData(getActivity(), getKey());
       /* if (!AppPreferenceUtils.getInstance().isRequiredUpdated(getActivity(), getKey()) && response != null) {
            showdata(response);
            Log.d(TAG, "Caching data and key " + getKey());
        } else {*/
            Log.d(TAG, "server and key " + getKey());

            /*JioAppAsyncTask jioAppAsyncTask = new JioAppAsyncTask(getActivity(), groupName, appCategory, new ContentChangeCallback() {
                @Override
                public void onSuccess(AppResponse response) {

                    if (getActivity() != null && !getActivity().isFinishing()) {
                        AppPreferenceUtils.getInstance().persistApplist(getActivity(), response, getKey());
                        showdata(response);
                    }
                }


                @Override
                public void onFailure(AppResponse response) {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        grid = (GridView) (getActivity()).findViewById(R.id.grid);
                        grid_info = (RelativeLayout) getActivity().findViewById(R.id.grid_info);
                        grid_info.setVisibility(View.VISIBLE);
                        grid.setVisibility(View.GONE);
                        progress_app.setVisibility(View.GONE);
                        retry.setVisibility(View.GONE);
                        if (response.error != null && response.error.message != null) {
                            logs_msg.setText(response.error.message);
                        } else {
                            logs_msg.setText(R.string.no_apps_msg);

                            // need to add message here
                        }
                        AppPreferenceUtils.getInstance().resetCache(getActivity(), getKey());
                    }
                }
            });
            jioAppAsyncTask.execute();*/
        //}
        return v;
    }


    private String getKey() {
        return groupName + appCategory;
    }

    private void launchApplication(int position) {

        try {
            Activity myParent = getActivity();
            //Cursor cursor = (Cursor) mAppAdapter.getItem(position);

            List<AppItem> appItems = AppsProvider.getJioAppItems(getActivity(),where,banks);


           AppItem appItem =  appItems.get(position);


            String event = null;


            switch (Integer.parseInt(appItem.getVersionCode())) {
                case AppItem.STATE_NOT_INSTALLED:
                    event = EVENT_SUGGEST_INSTALL;
                    break;
                case AppItem.STATE_INSTALLED:
                    event = EVENT_SUGGEST_LAUNCH;
                    break;
                case AppItem.STATE_INSTALLED_UPDATE_AVAILABLE:
                    event = EVENT_SUGGEST_UPGRADE;
                    break;
                case AppItem.STATE_COMING_SOON:
                    event = EVENT_SUGGEST_COMINGSOON;
            }

            if (event != EVENT_SUGGEST_COMINGSOON) {
                doClickAction(appItem);

            } else if (event == EVENT_SUGGEST_COMINGSOON) {
                Context context = getActivity();

                /*SimpleDialogFragmentExt.SimpleDialogExtBuilder  simpleDialogFragment= SimpleDialogFragmentExt
                        .createBuilder(getActivity(),
                                getFragmentManager());
                simpleDialogFragment.setSimpleDialogListener(simpleDialogListener);
                simpleDialogFragment.setTitle(R.string.app_coming_soon);
                simpleDialogFragment .setMessage(
                        getString(R.string.app_coming_soon_description));
                simpleDialogFragment .setPositiveButtonText(android.R.string.ok)
                        .show();*/


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void doClickAction(AppItem appItem) {

        if (appItem.getSecondaryViewMode() == 2) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + appItem.getPackageName()));
                    startActivity(intent);
            if (appItem.getState() == AppItem.STATE_INSTALLED_UPDATE_AVAILABLE) {
                openApp(getActivity(), appItem.getPackageName(), appItem.getApkUrl());
            } else {
                handleClickProcess(getActivity(), appItem.getPackageName(), appItem.getApkUrl());
            }
//            openApp(getActivity(), appItem.getPackageName(), appItem.getApkUrl());
        } else if (appItem.getSecondaryViewMode() == 4) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appItem.getApkUrl()));
            startActivity(browserIntent);
        } else {
            Intent i = appItem.intent;
            Bundle bundle = new Bundle();
            if (appItem.getState() == AppItem.STATE_NOT_INSTALLED) {
                String value = "jioworld";
                bundle.putString("installSource", value);
            } else if (appItem.getState() == AppItem.STATE_INSTALLED) {
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("bank://appcenter/appdetail/" + appItem.getPackageName()));
            Intent intent = new Intent(getActivity(),AppDetailActivity.class);
            i = intent;
            }
            Intent intent = new Intent(getActivity(), AppDetailActivity.class);
            i = intent;
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            bundle.putString("packageName", appItem.getPackageName());
            bundle.putInt("SecondaryViewValue", appItem.getSecondaryViewMode());
            i.putExtras(bundle);

            ((Callback) getActivity())
                    .onItemSelected(i, appItem);

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

    private void handleClickProcess(Context context, String packageName, String appUrl) {
        if (!TextUtils.isEmpty(packageName)) {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                // We found the activity now start the activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (appUrl.contains("play.google.com/store")) {
                try {
                    final boolean networkAvailable = CommonUtils
                            .networkAvailable(getActivity());
                    if (networkAvailable) {
                        // code has to write here
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                        startActivity(intent);
                    } else {
                        showNetworkErrorDialog();
                    }
                } catch (Exception e) {
                    // nothing to do just open browser
                    redirectToBrowser(appUrl);
                }
            } else {
                redirectToBrowser(appUrl);
            }
        }
    }

    private void redirectToBrowser(String appUrl) {
        final boolean networkAvailable = CommonUtils
                .networkAvailable(getActivity());
        if (networkAvailable) {
            // code has to write here
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
            getActivity().startActivity(webIntent);
        } else {
            showNetworkErrorDialog();
        }
    }


    private void showNetworkErrorDialog() {
        Context context = getActivity();
        AppUtils.getAppCenterDialog(context, context.getString(R.string.network_availability_title), context.getString(R.string.network_availability_description),
                true, false, null).show();
    }

    private void showNetworkError() {
        logs_msg.setText(R.string.network_availability_description);
        retry.setVisibility(View.GONE);
        progress_app.setVisibility(View.GONE);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeInfo(true);
                logs_msg.setText(R.string.jw_loading_apps_msg);
                progress_app.setVisibility(View.VISIBLE);
                retry.setVisibility(View.GONE);
                checkNetworkAvailableAndLoad();
            }
        });
    }



/*    private synchronized void updateContent() {
        Activity act = getActivity();
        if (act != null && !act.isFinishing()) {

        }
    }*/





    private List<AppItem> sortJioAppItems(List<AppItem> mAppItems) {

        ArrayList<String> appIds = new ArrayList<String>();
        List<AppItem> tempList = new ArrayList<AppItem>();

        for (AppItem elem : mAppItems) {
            if (!appIds.contains(elem.getAppID())) {
                appIds.add(elem.getAppID());
                tempList.add(elem);
            }
        }
        return tempList;
    }

    private void registerReceivers() {
        Log.d(TAG, "registerReceivers");
        if (mAppsReceiver == null) {
            mAppsReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "Received pkg update");
                    //updateContent();
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        getActivity().registerReceiver(mAppsReceiver, filter);

    }

   

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appCategory = null;
        if (getArguments() != null) {
            groupName = getArguments().getString("group");
            appCategory = getArguments().getString("category");
            banks =   getArguments().getStringArray("banks");
            tablet = getArguments().getString("tablet");
        }


        registerReceivers();
    }

    @Override
    public void onDestroy() {
        unregisterReceivers();

        super.onDestroy();
    }




    private void unregisterReceivers() {
        Log.d(TAG, "unregisterReceivers");
        if (mAppsReceiver != null) {
            getActivity().unregisterReceiver(mAppsReceiver);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        checkNetworkAvailableAndLoad();

        if (mAppAdapter != null && mAppAdapter.getCount() > 0)
            removeInfo(false);
        if (isUserLoggedOut)
            showLogOutPopUP();
    }


    private void checkNetworkAvailableAndLoad() {

        final boolean networkAvailable = CommonUtils.networkAvailable(this.getActivity().getApplicationContext());
        if (networkAvailable) {
            if ((CommonUtils.isDeviceConnectedTo2Gor3G(getActivity().getApplicationContext()))){

            } else {
                CommonUtils.IcaptivePortalListener iCaptivePortalListener = new CommonUtils.IcaptivePortalListener() {
                    @Override
                    public void onResponse(boolean lockStatus) {
                        if (!lockStatus) {
                            //CaptivePortal lock is ready to acquire
                            if (getActivity() != null) { //added null check as there is an exception
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        } else {
                            //there is a captive portal block
                            //so show no network connection dialog
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showNetworkError();
                                    }
                                });
                            }
                        }
                    }
                };
            }
        } else {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNetworkError();
                    }
                });
            }
        }
    }

    private void removeInfo(Boolean show) {
        RelativeLayout grid_info = (RelativeLayout) getActivity().findViewById(R.id.grid_info);
        if (grid_info != null) {
            if (!show)
                grid_info.setVisibility(View.GONE);
            else
                grid_info.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //String banklist = banks.toString();
        if (banks != null && banks.length > 0) {
            String whereFirst = "author=?";
            where = whereFirst;
            for (int count = 1; count < banks.length; count++) {
                where = where + "OR " + whereFirst;
            }
            return new CursorLoader(getActivity(),
                    AppsProvider.CONTENT_URI,
                    null,
                    where,
                    banks,
                    null);
        }
        else
        {
            return new CursorLoader(getActivity(),
                    AppsProvider.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        logs_msg.setVisibility(View.GONE);
        progress_app.setVisibility(View.GONE);
        if (data.getCount()>0) {
            mAppAdapter.swapCursor(data);
            if (tablet != null && tablet.equals("tablet")) {
                launchApplication(0);
            }
        }
        else
        {
            logs_msg.setText(R.string.no_offers_available);
            logs_msg.setVisibility(View.VISIBLE);
        }

    }






    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAppAdapter.swapCursor(null);
    }


    public interface ContentChangeCallback {
        void onSuccess(AppResponse appResponse);

        void onFailure(AppResponse appResponse);

    }


    private List<AppItem> getJioAppItemsByCategory(List<AppItem> appItems) {
        List<AppItem> categoryJioAppsList = new ArrayList<AppItem>();
        if (appItems != null && appItems.size() > 0)
            for (int i = 0; i < appItems.size(); i++) {
                //if (appCategory.equalsIgnoreCase(appItems.get(i).getCategory()))
                    categoryJioAppsList.add(appItems.get(i));
            }
        return categoryJioAppsList;
    }

    private void showLogOutPopUP() {
        isUserLoggedOut = false;

        Context context = getActivity();
        AppUtils.getAppCenterDialog(context, context.getString(R.string.logout_error_title),
                context.getString(R.string.logout_error_msg), true, false, null).show();

        /*SimpleDialogFragmentExt.SimpleDialogExtBuilder simpleDialogFragment = SimpleDialogFragmentExt.createBuilder(getActivity(), getFragmentManager());
        simpleDialogFragment.setTitle(R.string.logout_error_title)
                .setMessage(
                        getString(R.string.logout_error_msg))
                .setPositiveButtonText(android.R.string.ok);
        simpleDialogFragment.setCancelableOnTouchOutside(false);
        simpleDialogFragment.setRequestCode(DIALOG_LOGOUT_ERROR);
        simpleDialogFragment.setSimpleDialogListener(this);
        simpleDialogFragment.show();*/
    }


}