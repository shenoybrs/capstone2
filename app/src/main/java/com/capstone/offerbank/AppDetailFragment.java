package com.capstone.offerbank;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A fragment representing a single App detail screen. This fragment is either contained in a in two-pane mode (on tablets) or a {@link AppDetailActivity} on handsets.
 */
public class AppDetailFragment extends Fragment {

    private static final String TAG = AppDetailFragment.class.getSimpleName();
    private static final String FRAG_SCREENSHOT ;
    public static final String ARG_APP ;
    private static final String ARG_ERR ;
    private BroadcastReceiver mLoginReceiver;

    public static final String ARG_PKG_NAME ;
    private static final String DIALOG_TAG ;
    static{
        FRAG_SCREENSHOT = "FRAG_SCREENSHOT";
        ARG_APP = "item_app";
        ARG_ERR = "item_err";

        ARG_PKG_NAME = "package-name";
        DIALOG_TAG = "DIALOG_TAG";
    }
    private static final int DIALOG_LOGOUT_ERROR = 222;
    private App mApp;
    private AppResponse mAppResponse;
    private String mPkgName;
    private long mDownloadId = -1l;
    ScreenshotAdapter mAdapter;
    private GestureDetector mGestureDetector;

    private View mRootView;
    private Button btn1;// Install/Open
    private Button btn2;// Uninstall
    private Button btn3;// Update
    private Button cancelDownload;

    private View mButtonContainer;
    private View mProgressContainer;
    private ProgressBar mProgressBar;
    private int mDownloadPercent = -1;

    private Context mContext;

    NetworkImageView mLargeScreenshot;
   // private static boolean isUserLoggedOutFromDetailView=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments().containsKey(ARG_APP)) {
            mApp = getArguments().getParcelable(ARG_APP);
            if (mApp != null) {
                mPkgName = mApp.packageName;
                mDownloadId = Prefs.getInstance().getDownloadIdForPackage(mApp.packageName);
            }

        }
        if (getArguments().containsKey(ARG_PKG_NAME)) {
            mPkgName = getArguments().getString(ARG_PKG_NAME);
            mDownloadId = Prefs.getInstance().getDownloadIdForPackage(mPkgName);
        }

        mContext = getActivity();
/*
        mLoginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("This is in mLoginReceiver");
                if (AppsFragment.ACTION_LOGOUT_FINISHED.equals(intent.getAction())) {
                    System.out.println("This is in mLoginReceiver ACTION_LOGOUT_FINISHED");
                    isUserLoggedOutFromDetailView=true;

                }
            }
        };

        registerLoginAndLogOutReceiver();*/

    }

    @Override
    public View
    onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationManager nm =
                (NotificationManager) PreferenceUtils.getInstance().getAppContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        if (mApp != null)
            nm.cancel(mApp.getNotificationId());
        mRootView = inflater.inflate(R.layout.fragment_app_detail, container, false);
        ImageButton backArrow = (ImageButton) mRootView.findViewById(R.id.back_arrow);

        backArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
        mProgressContainer = mRootView.findViewById(R.id.progressContainer);
        mButtonContainer = mRootView.findViewById(R.id.btn_bar);
        btn1 = (Button) mRootView.findViewById(R.id.btn1);
        btn2 = (Button) mRootView.findViewById(R.id.btn2);
        btn3 = (Button) mRootView.findViewById(R.id.btn3);


        cancelDownload = (Button) mRootView.findViewById(R.id.cancel_download);
        cancelDownload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // cancel the download
                //ContentProvider to SQlite
                /*DownloadManager dm =
                        new DownloadManager(PreferenceUtils.getInstance().getAppContext().getContentResolver(),
                                PreferenceUtils.getInstance().getAppContext().getPackageName());*/

                hideProgress();
                configureButtons();
            }
        });

        MyViewFlipper vf = (MyViewFlipper) mRootView.findViewById(R.id.viewFlipper);
        vf.setInAnimation(this.getActivity(), android.R.anim.fade_in);
        vf.setOutAnimation(this.getActivity(), android.R.anim.fade_out);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector(vf);
        mGestureDetector = new GestureDetector(this.getActivity(), customGestureDetector);

        mRootView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        if (mApp == null) {
            mRootView.findViewById(R.id.contentContainer).setVisibility(View.GONE);
            mRootView.findViewById(R.id.progressLoading).setVisibility(View.VISIBLE);
            // Load the app details..
            loadAppData();
        } else {
            setupUI();
        }

        return mRootView;
    }

    @Override
    public void onDestroy() {
       // unRegisterLoginAndLogOutReceiver();
        super.onDestroy();
    }

    private void loadAppData() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                IAppStore store = AppStoreFactory.getAppStore(PreferenceUtils.getInstance().getAppContext());
                mAppResponse = store.getAppDetails(mPkgName, AppStoreConstants.APP_DETAIL_URL);
                if (mAppResponse!=null && mAppResponse.apps!=null && mAppResponse.error==null) {
                    mApp = mAppResponse.apps.get(0);
                }
                else
                {
                    mApp = null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (mApp != null) {
                        if (mApp.versionCode != 0)

                        {
                            if (mAppResponse != null && mApp != null) {
                                if (getActivity() != null){
                                    getActivity().runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            String url = mApp.apkUrl;
                                            if (url.contains("market://") || url.contains("play.google.com")) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(url));
                                                startActivity(intent);
                                                getActivity().finish();
                                            } else {
                                                setupUI();
                                            }
                                        }
                                    });
                                }
                            } else if (mAppResponse != null && mAppResponse.error.code >= 300 && mAppResponse.error.code <= 500) {
                                AppUtils.getAppCenterDialog(mContext, mContext.getString(R.string.commonlib_unexpected_error_title),
                                        mContext.getString(R.string.commonlib_unexpected_error_desc), true, false, null).show();

                            } else {
                                AppUtils.getAppCenterDialog(mContext, mContext.getString(R.string.commonlib_No_Application_Available),
                                        mContext.getString(R.string.commonlib_No_Application_Available_description), true, false, null).show();

                            }
                        } else {
                            AppUtils.getAppCenterDialog(mContext, mContext.getString(R.string.commonlib_app_coming_soon),
                                    mContext.getString(R.string.commonlib_app_coming_soon_description), true, false, null).show();

                        }

                    } else {
                        AppUtils.getAppCenterDialog(mContext, mContext.getString(R.string.commonlib_unexpected_error_title),
                                mContext.getString(R.string.commonlib_unexpected_error_desc), true, false, null).show();

                    }
                }
            }
        };
        task.execute();
    }

    private void setupUI() {

        mRootView.findViewById(R.id.contentContainer).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.progressLoading).setVisibility(View.GONE);
        configureButtons();

        if (mApp != null) {

            MyViewFlipper viewFlipper = (MyViewFlipper) mRootView.findViewById(R.id.viewFlipper);
            setFlipperImage(viewFlipper);
            ((NetworkImageView) mRootView.findViewById(R.id.iconsmall)).setImageUrl(
                    mApp.iconSmall.toString(), JSSCommonService.getInstance(getActivity()).getImageLoader());

            ((TextView) mRootView.findViewById(R.id.name)).setText(mApp.name);
            ((TextView) mRootView.findViewById(R.id.author)).setText("Version " +mApp.versionName);
            ((TextView) mRootView.findViewById(R.id.versionName)).setText("Updated on -" + convertToDate(mApp.date));//mApp.date);


            ((TextView) mRootView.findViewById(R.id.description)).setText(AppUtils.getUTFString(mApp.description));




/*            ((TextView) mRootView.findViewById(com.csf.uilib.R.id.ratings_count)).setText(String
                    .valueOf(mApp.ratingsCount));*///field does not exist in server response
/*
            ((RatingBar) mRootView.findViewById(com.csf.uilib.R.id.ratings_bar)).setRating(mApp.ratingsAverage);
            ((TextView) mRootView.findViewById(com.csf.uilib.R.id.size)).setText(Formatter.formatFileSize(
                    getActivity(), mApp.sizeInBytes));
*/

            initScreenShots(mRootView);
        }
    }
    private void setFlipperImage(MyViewFlipper viewFlipper) {
        if(mApp.iconBanner.toString()!=null) {
            viewFlipper.addView(getNetworkImageView(mApp.iconBanner.toString()));
        }
        if(mApp.iconLarge.toString()!=null) {
            viewFlipper.addView(getNetworkImageView(mApp.iconLarge.toString()));
        }

    }

    private NetworkImageView getNetworkImageView(String promoUrl){
        NetworkImageView image = new NetworkImageView(getActivity().getApplicationContext());
        LayoutParams vp =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        image.setLayoutParams(vp);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setImageUrl(
                promoUrl, JSSCommonService.getInstance(getActivity()).getImageLoader());
        return  image;
    }
    private void initScreenShots(View rootView) {
        if (mApp.screenShots != null) {

            LayoutInflater inflater = LayoutInflater.from(rootView.getContext());

            OnClickListener ocl = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    URL url = (URL) v.getTag();
                    loadImage(url.toString());
                    /*ScreenshotDialog sd = new ScreenshotDialog(url.toString());
                    sd.loadImage(getActivity());*/
                }
            };
            LinearLayout sc = (LinearLayout) rootView.findViewById(R.id.screen_container);
            LayoutParams params = new LayoutParams(sc.getLayoutParams());
            params.height = LayoutParams.WRAP_CONTENT;
            params.width = LayoutParams.WRAP_CONTENT;
            for (URL url : mApp.screenShots) {
                if (sc.getChildCount() > 0) {
                    params.setMargins(16, 0, 0, 0);
                }
                NetworkImageView niv = (NetworkImageView) inflater.inflate(
                        R.layout.list_image, (ViewGroup) rootView, false);
                niv.setLayoutParams(params);
                niv.setImageUrl(url.toString(), JSSCommonService.getInstance(getActivity()).getImageLoader());
                niv.setTag(url);
                niv.setOnClickListener(ocl);
                sc.addView(niv);
            }
        }

    }

    private void loadImage(String url) {
        Intent i= new Intent(getActivity(), ScreenShotActivity.class);
        Bundle args = new Bundle(1);
        args.putString(ScreenShotActivity.ARG_URL, url);
        i.putExtras(args);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtils.setAppStates(mApp);
        configureButtons();
//        if (mRootView != null) {
//            setInstalledInfo(mRootView);
//        }
        if (AppsFragment.isUserLoggedOut)
            showLogOutPopUP();
    }


    private void configureButtons() {

        OnClickListener installClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApp != null) {

                    if (mApp.apkUrl.contains("market://") || mApp.apkUrl.contains("play.google.com")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(mApp.apkUrl));
                        startActivity(intent);
                        // getActivity().finish();
                    }
                }

            }
        };

        if (getActivity() != null) {
            if (mApp != null && mApp.apkUrl != null && !TextUtils.isEmpty(mApp.apkUrl) && Build.VERSION.SDK_INT >= mApp.minsdk) {



                PackageManager pm = getActivity().getPackageManager();
                PackageInfo pi = null;
                try {
                    pi = pm.getPackageInfo(mApp.packageName, 0);
                } catch (NameNotFoundException e1) {
                    // eat it;
                }

                if (null == pi) {
                    btn2.setVisibility(View.GONE);
                    btn3.setVisibility(View.GONE);

                    btn1.setText("INSTALL");
                    btn1.setVisibility(View.VISIBLE);
                    btn1.setOnClickListener(installClick);

                } else {
                    final Intent li = pm.getLaunchIntentForPackage(mApp.packageName);
                    if (null == li) {
                        btn1.setVisibility(View.GONE);
                    } else {
                        btn1.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                startActivity(li);
                            }
                        });
                        btn1.setText("OPEN");
                        btn1.setEnabled(true);
                        btn1.setVisibility(View.VISIBLE);
                        if ((mApp.maxRuns == 0 || mApp.currentRuns < mApp.maxRuns)
                                && mApp.expireDate.after(Calendar.getInstance())) {
                            btn1.setEnabled(true);
                            btn1.requestFocus();
                        } else {
                            btn1.setEnabled(false);
                        }
                    }

                    if (mApp.uninstallable) {
                        btn2.setVisibility(View.VISIBLE);
                        btn2.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // uninstall the app.
                                Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", mApp.packageName,
                                        null));



                                startActivity(intent);

                                // TODO Cancel the download
                                // processing/notification if update was
                                // started.
                            }
                        });
//                        if (status == -1 || status == DownloadManager.STATUS_FAILED) {
//                            btn2.setEnabled(true);
//                        } else {
//                            btn2.setEnabled(false);
//                        }
                    } else {
                        btn2.setVisibility(View.GONE);
                    }

                    if (pi.versionCode < mApp.versionCode) {
                        btn3.setVisibility(View.VISIBLE);
                        btn1.setVisibility(View.GONE);
                        btn3.setOnClickListener(installClick);
                    }
                }
            } else {
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.GONE);
//                if (mApp != null && Build.VERSION.SDK_INT < mApp.minsdk) {
//                    //Let the user know what SDK version is needed.
//                    TextView txt = (TextView) mRootView.findViewById(R.id.message);
//                    txt.setText(getString(R.string.msg_device_not_compatible));
//                    txt.setVisibility(View.VISIBLE);
//                }
            }
        }
    }


    private void showProgress() {
        mProgressContainer.setVisibility(View.VISIBLE);
        mButtonContainer.setVisibility(View.GONE);

        int delay = 200;
        if (mDownloadPercent <= 0) {
            mProgressBar.setIndeterminate(true);
            cancelDownload.setEnabled(false);
            delay = 500;
        } else {
            mProgressBar.setIndeterminate(false);
            cancelDownload.setEnabled(true);
            mProgressBar.setProgress(mDownloadPercent);
        }
    }

    private void hideProgress() {
        mDownloadPercent = -1;
        mProgressContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.VISIBLE);
    }

    private static final int MSG_PROGRESS = 1;
    private static final int MSG_STOP = 2;




    /**
     * Get the App associated with this fragment
     *
     * @return
     */
    public App getApp() {
        return mApp;
    }



    public static class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private ViewFlipper localVf = null;
        CustomGestureDetector(ViewFlipper vf)
        {
            localVf = vf;
        }



        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                localVf.showNext();
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                localVf.showPrevious();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private String convertToDate(String someDate){
        String val = "";
        //adding a day delay since server sends date as "date":"2015-05-24 18:30:00.0", actual date seen on web page is 25-05-2015
        Long indDiff = 86400000l;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = sdf.parse(someDate);
            Long temp = date.getTime()+indDiff;
            System.out.println(temp);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(temp);
            val = sdf2.format(calendar.getTime());

        }catch (Exception e){
            e.getMessage();
        }
        return val;
    }
    private void showLogOutPopUP(){
       // isUserLoggedOutFromDetailView=false;
        AppsFragment.isUserLoggedOut=false;

        AppUtils.getAppCenterDialog(mContext, mContext.getString(R.string.logout_error_title),
                mContext.getString(R.string.logout_error_msg), true, false, null).show();

       /* SimpleDialogFragmentExt.SimpleDialogExtBuilder simpleDialogFragment = SimpleDialogFragmentExt.createBuilder(getActivity(), getFragmentManager());
        simpleDialogFragment.setTitle(R.string.logout_error_title)
                .setMessage(
                        getString(R.string.logout_error_msg))
                .setPositiveButtonText(android.R.string.ok);
        simpleDialogFragment.setCancelableOnTouchOutside(false);
        simpleDialogFragment.setRequestCode(DIALOG_LOGOUT_ERROR);
        simpleDialogFragment.setSimpleDialogListener(this);
        simpleDialogFragment.show();*/
    }
   /* private void registerLoginAndLogOutReceiver() {
        IntentFilter loginFilter = new IntentFilter();
        // loginFilter.addAction(ACTION_LOGIN_FINISHED);
        loginFilter.addAction(AppsFragment.ACTION_LOGOUT_FINISHED);
        getActivity().registerReceiver(mLoginReceiver, loginFilter);
    }*/
   /* private void unRegisterLoginAndLogOutReceiver() {
        if(mLoginReceiver!=null)
            getActivity().unregisterReceiver(mLoginReceiver);
    }*/
}
