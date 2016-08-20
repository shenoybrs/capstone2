package com.capstone.pixscramble;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Arrays;


public class PixGridActivity extends ActionBarActivity implements IAppCenterDialogListener{
    private boolean mTwoPane;
    private static final String FRAG_TAG;
    private static final String success;
    private static final String message;
    private static final String updateMDK;
    private static final String TAG = "PixGridActivity" ;
    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 11;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private PixResponse mPixResponse;

    private Pix app;
    static {
        FRAG_TAG = "PixGridActivity.detail_frag";
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
        PixFragment fragment = new PixFragment();
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





    private void chooseFragment(Pix app ) {

    }


}
