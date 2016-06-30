package com.capstone.offerbank;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

/**
 * An activity representing a single App detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {AppDetailActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing more than a {@link AppDetailFragment}.
 */
public class AppDetailActivity extends AppCompatActivity implements IAppCenterDialogListener{

    private static final String FRAG_TAG = "detail_frag";
    private static final String FRAG_TAG1 = "detail_frag1";
    private App app;
    public static String source = "jioworld";
    private int secondaryViewValue = 1;
    public static final int JIO_STORE = 0;
    public static final int GOOGLE_STORE = 2;
    public static final int TEASER_VIEW = 3;
    private AppResponse mAppResponse;
    private ProgressBar mProgressBar;
    private View mProgressContainer;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /* getActionBar().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        setBackButtonBackground(R.drawable.jionet_action_back_drawable);*/
        setContentView(R.layout.activity_app_detail);


        mProgressContainer = findViewById(R.id.progressLoadingDetail);

        if (getIntent().hasExtra("installSource")) {
            source = getIntent().getExtras().get("installSource").toString();
        }


        app = getIntent().getParcelableExtra(AppDetailFragment.ARG_APP);
        String pkgName = null;
        if (getIntent().hasExtra("packageName")) {
            pkgName = getIntent().getExtras().get("packageName").toString();
        }

        String title = "";
        //AppDetailActivity.this.setTitle(title);



        final boolean networkAvailable = CommonUtils
                .networkAvailable(getApplicationContext());
        if (networkAvailable) {
            if (!(CommonUtils.isDeviceConnectedTo2Gor3G(this))) {
                final String finalPkgName = pkgName;
                CommonUtils.IcaptivePortalListener iCaptivePortalListener = new CommonUtils.IcaptivePortalListener() {
                    @Override
                    public void onResponse(boolean lockStatus) {

                        if (!lockStatus) //means there is no captive portal blockage
                        {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (savedInstanceState == null) {
                                        launchApp(finalPkgName);
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (savedInstanceState == null) {
                                        networkNotAvailable();
                                    }
                                }
                            });

                        }
                    }
                };

            }else{
                launchApp(pkgName);
            }
        }
        else
        {
            networkNotAvailable();
        }

    }

private void networkNotAvailable()
{
        AppUtils.getAppCenterDialog(this, getString(R.string.network_availability_title),
                getString(R.string.network_availability_description), true, false, this).show();


}

    private void launchApp(final String packageName) {


            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                int resultCode = 0;
                @Override
                protected void onPreExecute() {
                    mProgressContainer.setVisibility(View.VISIBLE);
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
                    mProgressContainer.setVisibility(View.GONE);
                    if (true) {
                        if(resultCode == 200) {
                            if (this != null) {
                                if (app != null)
                                    if (app.versionCode != 0) {
                                        if (mAppResponse != null && app != null) {
                                            chooseFragment(app);
                                        } else if (mAppResponse != null && mAppResponse.error.code >= 300 && mAppResponse.error.code <= 500) {
                                            AppUtils.getAppCenterDialog(AppDetailActivity.this, getString(R.string.commonlib_unexpected_error_title),
                                                    getString(R.string.commonlib_unexpected_error_desc), true, false, null).show();

                                        } else {
                                            AppUtils.getAppCenterDialog(AppDetailActivity.this, getString(R.string.commonlib_No_Application_Available),
                                                    getString(R.string.commonlib_No_Application_Available_description), true, false, null).show();

                                        }
                                    } else {
                                        if (resultCode == 200) {
                                            AppUtils.getAppCenterDialog(AppDetailActivity.this, getString(R.string.commonlib_app_coming_soon),
                                                    getString(R.string.commonlib_app_coming_soon_description), true, false, null).show();

                                        } else {
                                            AppUtils.getAppCenterDialog(AppDetailActivity.this, getString(R.string.commonlib_unexpected_error_title),
                                                    getString(R.string.commonlib_unable_to_reach_server), true, false, null).show();

                                        }
                                    }

                            }
                        }else {
                            AppUtils.getAppCenterDialog(AppDetailActivity.this, getString(R.string.commonlib_unexpected_error_title),
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
     if (app.secondaryViewMode == JIO_STORE  ) {
         AppDetailFragment fragment = new AppDetailFragment();
         fragment.setArguments(arguments);
         if (true) {
             getSupportFragmentManager().beginTransaction().add(R.id.app_detail_container, fragment,
                     FRAG_TAG)
                     .commit();
         }
     }
     else if (app.secondaryViewMode == GOOGLE_STORE  ) {
         if (url.contains("market://") || url.contains("play.google.com")) {
             Intent intent = new Intent(Intent.ACTION_VIEW);
             intent.setData(Uri.parse(url));
             startActivity(intent);
             finish();
         }
         else
         {
             AppUtils.getAppCenterDialog(AppDetailActivity.this, getString(R.string.commonlib_unexpected_error_title),
                     getString(R.string.commonlib_unexpected_error_desc), true, false, null).show();

         }
     }
     else if (app.secondaryViewMode == TEASER_VIEW  ) {

     }
 }




    public static String getDownloadFileName(App app) {
        return (app.packageName + "-" + app.versionName + ".apk").replaceAll(" ", "");
    }



    // @Override
    // public void onDismiss(boolean canInstall) {
    // // TODO What to do...
    //
    // }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onOkButtonClick() {
        finish();
    }

    @Override
    public void onCancelButtonClick() {

    }
}
