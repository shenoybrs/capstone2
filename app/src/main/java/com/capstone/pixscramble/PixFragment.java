package com.capstone.pixscramble;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.pixscramble.provider.AppsProvider;
import com.capstone.pixscramble.service.AppSyncService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PixFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int APP_LOADER = 0;

    CountDownTimer countDownTimer = null;
    private View timerLayout;
    private TextView timerText;
    private TextView selectionText;
    private static final String TAG = "PixFragment";
    private static int DIALOG_NETWORK_ERROR = 101;
    private final static int DIALOG_LOGOUT_ERROR = 111;
    private BroadcastReceiver mAppsReceiver;
    private TextView logs_msg;
    private Button retry;
    private ProgressBar progress_app;
    private String groupName, appCategory, tablet;
    private String where;
    private String[] banks;
    private final int GET_APP_LIST_ID = 105;
    private BroadcastReceiver mLoginReceiver;
    private GridView grid;
    private LinearLayout grid_info;
    private AppsAdapter mAppAdapter;
    private List<AppItem> mAppItems = null;
    public static boolean isUserLoggedOut = false;
    private List<Integer> showedImagesPosition;
    private int currentImagePosition;
    NetworkImageView appIcon = null;
    private Cursor cursorData;
    private int identifiedImageCount = 0;

    private void findImagePosition(int clickedPosition) {
        if (clickedPosition == currentImagePosition) {

            identifiedImageCount++;
            ContentValues contentValues = new ContentValues();
            contentValues.put("read", 1);
            //update the database to read
            getActivity().getContentResolver().update
                    (AppsProvider.CONTENT_URI_UPDATE, contentValues, "_id = ?",
                            new String[]{String.valueOf(clickedPosition + 1)});
            if (identifiedImageCount < 9) {
                showNextImageView();
            } else {
                ContentValues contentValues1 = new ContentValues();

                getActivity().getContentResolver().delete(AppsProvider.CONTENT_URI_CLEAR, "read", new String[]{String.valueOf(1)});
                identifiedImageCount = 0;
                mAppAdapter.setState(PixStoreConstants.USER_WON);
                Toast.makeText(getActivity(),"U Won Start a new game",Toast.LENGTH_SHORT);
                getActivity().finish();

            }
        } else {

        }
    }

    private void showNextImageView() {
        int position = generateRandomPosition();
        cursorData.moveToPosition(position);
        if (VolleyService.getInstance(getActivity().getApplicationContext()).getImageLoader() != null) {
            appIcon.setImageUrl(cursorData.getString(cursorData.getColumnIndex
                            (AppsProvider.JioAppsColumns.ICON_URL)),
                    VolleyService.getInstance(getActivity().getApplicationContext()).getImageLoader());
        }
        showedImagesPosition.add(position);
        currentImagePosition = position;
        appIcon.setVisibility(View.VISIBLE);
        selectionText.setVisibility(View.VISIBLE);
    }


    public int generateRandomPosition() {
        boolean validPosition = false;
        Random random = new Random();
        int randomNumber = -1;
        while (!validPosition) {
            randomNumber = random.nextInt(9);//generating random number from 0-8
            if (randomNumber > -1 && !showedImagesPosition.contains(randomNumber)
                    && showedImagesPosition.size() <= 9) {
                validPosition = true;
            }
        }
        return randomNumber;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(APP_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public View
    onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_apps_one, container, false);
        appIcon = (NetworkImageView) v.findViewById(R.id.single_image);
        selectionText = (TextView) v.findViewById(R.id.selection_text);
        timerText = (TextView) v.findViewById(R.id.timer_text);
        timerLayout = v.findViewById(R.id.timer_layout);

        mAppAdapter = new AppsAdapter(getActivity(), null, 0);

        grid = (GridView) v.findViewById(R.id.grid);
        grid_info = (LinearLayout) getActivity().findViewById(R.id.grid_info);

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
                findImagePosition(position);
            }
        });
        logs_msg = (TextView) v.findViewById(R.id.loading_app);
        retry = (Button) v.findViewById(R.id.retry_app);
        progress_app = (ProgressBar) v.findViewById(R.id.progress_app);
        mAppAdapter.setState(PixStoreConstants.TIMER_START);
        return v;
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




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showedImagesPosition = new ArrayList<>();
        getLoaderManager().initLoader(APP_LOADER, null, this);
        appCategory = null;
        if (getArguments() != null) {
            groupName = getArguments().getString("group");
            appCategory = getArguments().getString("category");
            banks = getArguments().getStringArray("banks");
            tablet = getArguments().getString("tablet");
        }


    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }




    @Override
    public void onResume() {
        super.onResume();
        checkNetworkAvailableAndLoad();

        if (mAppAdapter != null && mAppAdapter.getCount() > 0)
            removeInfo(false);

    }


    private void checkNetworkAvailableAndLoad() {

        final boolean networkAvailable = CommonUtils.networkAvailable(this.getActivity().getApplicationContext());
        if (networkAvailable) {
            if ((CommonUtils.isDeviceConnectedTo2Gor3G(getActivity().getApplicationContext()))) {

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
        LinearLayout grid_info = (LinearLayout) getActivity().findViewById(R.id.grid_info);
        if (grid_info != null) {
            if (!show)
                grid_info.setVisibility(View.GONE);
            else
                grid_info.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                AppsProvider.CONTENT_URI,
                null,
                null,
                null,
                null);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        logs_msg.setVisibility(View.GONE);
        progress_app.setVisibility(View.GONE);


        if (data.getCount() > 0) {
            this.cursorData = data;
            if (mAppAdapter.getState() == PixStoreConstants.TIMER_START) {

                mAppAdapter.swapCursor(data);
                if (countDownTimer == null) {
                    countDownTimer = new CountDownTimer(16000, 1000) {

                        public void onTick(long millisUntilFinished) {

                            timerText.setText(String.valueOf(millisUntilFinished / 1000));
                        }

                        public void onFinish() {
                            timerLayout.setVisibility(View.GONE);
                            mAppAdapter.setState(PixStoreConstants.USER_PLAYING);

                            mAppAdapter.notifyDataSetInvalidated();
                            Log.d(TAG, "inside onfinish");
                            showNextImageView();

                            cancel();
                        }
                    };
                    countDownTimer.start();
                }
            } else if (mAppAdapter.getState() == PixStoreConstants.USER_PLAYING) {
                mAppAdapter.swapCursor(data);
                countDownTimer = null;
            }
        }
        else
        {
            //no data available please wake the service to fetch the data
            logs_msg.setText(R.string.jw_loading_apps_msg);
            progress_app.setVisibility(View.VISIBLE);
            AppSyncService.setAlarm(getActivity());
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAppAdapter.swapCursor(null);
    }







}