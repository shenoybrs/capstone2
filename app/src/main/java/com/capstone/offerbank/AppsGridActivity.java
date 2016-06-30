package com.capstone.offerbank;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Arrays;


public class AppsGridActivity extends ActionBarActivity implements IAppCenterDialogListener,AppsFragment.Callback {
    private boolean mTwoPane;
    private static final String FRAG_TAG;
    private static final String success;
    private static final String message;
    private static final String updateMDK;
    private static final String TAG = "AppsGridActivity" ;
    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 11;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private AppResponse mAppResponse;

    private App app;
    static {
        FRAG_TAG = "AppsGridActivity.detail_frag";
        success = "Success: your application is compatible with AppCenter Library";
        message = "Version Incompatible";
        updateMDK = "The app version is obsolete, kindly update.";
    }

    private ProgressBar progress_app1,progress_app2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_grid);
        progress_app1 = (ProgressBar) findViewById(R.id.progress_app1);
        progress_app1.setVisibility(View.VISIBLE);


        if (findViewById(R.id.app_detail_container) != null) {
            Log.d(TAG, "onCreate() called");
            progress_app2 = (ProgressBar) findViewById(R.id.progress_app2);
            mTwoPane = true;


        } else {
            mTwoPane = false;

        }
        loadFragment(mTwoPane);
    }
    private void loadFragment(boolean tablet) {
        Log.d(TAG, "loadFragment() called");
        progress_app1.setVisibility(View.GONE);
        Bundle bundle1 = new Bundle();
        AppsFragment fragment = new AppsFragment();
        Bundle extras = getIntent().getExtras();
        bundle1 = extras;
        if(extras!=null) {

            if (extras.containsKey("banks"))
            {
                Log.d(TAG, "banks is :: " + Arrays.toString(extras.getStringArray("banks")));
            }

            if (extras.containsKey("group")) {
                Log.d(TAG, "Group is :: " + extras.getString("group"));
            }
            if (extras.containsKey("category")) {
                Log.d(TAG, "category is :: " + extras.getString("category"));
            }

            if (tablet)
            {
                bundle1.putString("tablet", "tablet");
            }
            fragment.setArguments(extras);
        }
        //Issue Fix: ANDLIBS-60 : Applied 'if' condition
        try {
            if (!isFinishing()) {
                getSupportFragmentManager().beginTransaction().add(R.id.app_grid_container, fragment,
                        FRAG_TAG)
                        .commit();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }








    @Override
    public void onOkButtonClick() {
        finish();
    }

    @Override
    public void onCancelButtonClick() {

    }


    @Override
    public void onItemSelected(Intent appIntent, AppItem appModel) {
        if (mTwoPane) {
            //tablet UI
            //get the app details for the package name and call the fragment

            launchApp(appModel.getPackageName());
        }
        else
        {
            //for the mobile ui

            startActivityForResult(appIntent, DETAIL_ACTIVITY_REQUEST_CODE);

        }
    }

    private void launchApp(final String packageName) {


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            int resultCode = 0;
            @Override
            protected void onPreExecute() {
                progress_app2.setVisibility(View.VISIBLE);
                
                //chooseFragment(null);

            }

            @Override
            protected Void doInBackground(Void... params) {
                IAppStore store = AppStoreFactory.getAppStore(PreferenceUtils.getInstance().getAppContext());
                mAppResponse = store.getAppDetails(packageName, AppStoreConstants.APP_DETAIL_URL);
                if (mAppResponse!=null && mAppResponse.apps!=null && mAppResponse.error==null) {
                    app = mAppResponse.apps.get(0);
                    resultCode=200;
                }
                else
                {
                    resultCode = mAppResponse.error.code;
                    app = null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                progress_app2.setVisibility(View.GONE);
                if (true) {
                    if(resultCode == 200) {
                        if (this != null) {
                            if (app != null)
                                if (app.versionCode != 0) {
                                    if (mAppResponse != null && app != null) {
                                        chooseFragment(app);
                                    } else if (mAppResponse != null && mAppResponse.error.code >= 300 && mAppResponse.error.code <= 500) {
                                        AppUtils.getAppCenterDialog(AppsGridActivity.this, getString(R.string.commonlib_unexpected_error_title),
                                                getString(R.string.commonlib_unexpected_error_desc), true, false, null).show();

                                    } else {
                                        AppUtils.getAppCenterDialog(AppsGridActivity.this, getString(R.string.commonlib_No_Application_Available),
                                                getString(R.string.commonlib_No_Application_Available_description), true, false, null).show();

                                    }
                                } else {
                                    if (resultCode == 200) {
                                        AppUtils.getAppCenterDialog(AppsGridActivity.this, getString(R.string.commonlib_app_coming_soon),
                                                getString(R.string.commonlib_app_coming_soon_description), true, false, null).show();

                                    } else {
                                        AppUtils.getAppCenterDialog(AppsGridActivity.this, getString(R.string.commonlib_unexpected_error_title),
                                                getString(R.string.commonlib_unable_to_reach_server), true, false, null).show();

                                    }
                                }

                        }
                    }else {
                        AppUtils.getAppCenterDialog(AppsGridActivity.this, getString(R.string.commonlib_unexpected_error_title),
                                getString(R.string.commonlib_unexpected_error_desc), true, false, null).show();

                    }
                }
            }
        };


        task.execute();

    }

    private void chooseFragment(App app ) {
        Bundle arguments = new Bundle();
        if (app != null) {
            arguments.putParcelable(AppDetailFragment.ARG_APP, app);
        }
        if (!TextUtils.isEmpty(app.packageName)) {
            arguments.putString(AppDetailFragment.ARG_PKG_NAME, app.packageName);
        }
        String url = app.apkUrl;
        if (app.secondaryViewMode == AppDetailActivity.JIO_STORE  ) {
            AppDetailFragment fragment = new AppDetailFragment();
            fragment.setArguments(arguments);
            if (true) {
                getSupportFragmentManager().beginTransaction().add(R.id.app_detail_container, fragment,
                        FRAG_TAG)
                        .commit();
            }
        }
        else if (app.secondaryViewMode == AppDetailActivity.GOOGLE_STORE  ) {
            if (url.contains("market://") || url.contains("play.google.com")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                finish();
            }
            else
            {
                AppUtils.getAppCenterDialog(AppsGridActivity.this, getString(R.string.commonlib_unexpected_error_title),
                        getString(R.string.commonlib_unexpected_error_desc), true, false, null).show();

            }
        }
        else if (app.secondaryViewMode == AppDetailActivity.TEASER_VIEW  ) {

        }
    }


}
