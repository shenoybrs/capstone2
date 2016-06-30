package com.capstone.offerbank;

import android.content.Context;

import com.capstone.offerbank.gson.GsonParser;
import com.capstone.offerbank.server.CommonServerProviderFactory;
import com.capstone.offerbank.server.CommonServerProviderInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class AppStoreImpl implements IAppStore {

    private static AppStoreImpl sInstance;
    private AppResponse mAppResponse;
    private final Context mContext;

    public static AppStoreImpl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppStoreImpl(context);

        }

        return sInstance;
    }

    private AppStoreImpl(Context context) {
        mContext = context;
    }

    @Override
    public AppResponse getAppDetails(String packageName, String url) {
        try {

            final Semaphore mutex = new Semaphore(1);
            mutex.acquire();
            mAppResponse = getGLAppStoreData(url, AppStoreConstants.APPSTORE_GROUP_JIOAPP, null, 1, packageName);
            mutex.release();
            if (mAppResponse == null) {
                mAppResponse = new AppResponse();
                mAppResponse.apps = new ArrayList<App>();
                mAppResponse.error = new AppResponse.ResponseError();
                mAppResponse.error.code = 500;
                mAppResponse.error.message = mContext.getResources().getString(R.string.app_store_failure);
                return mAppResponse;
            }
            return mAppResponse;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AppResponse getGLAppStoreData(String url, String group, String category, Integer mode, String packageName) {
        JSONObject jsonRes = null;
        AppResponse mAppGlResponse = new AppResponse();
        AppResponse app = null;
        JSONArray categoryJson = null;
        CommonServerProviderInterface provider = CommonServerProviderFactory.createProvider(mContext);
        String jioResponse = null;
        ArrayList<App> appResponse = new ArrayList<App>();
        App singleApp;
        try {
            String response = null;
            int code = 200;
            response = provider.getAppStoreData(url, group, category, mode, packageName);

            if (code == 200) {

                if (!response.isEmpty() || !response.equals(null)) {
                    jsonRes = new JSONObject(response);
                    //THis check is needed to distinguish response for single app
                    //and Array of apps
                    if (packageName == null) {
                        categoryJson = jsonRes.getJSONArray("apps");
                        GsonParser<App> parser = new GsonParser<App>(
                                App.class);
                        try {
                            mAppGlResponse = new AppResponse();


                            for (int i = 0; i < categoryJson.length(); i++) {
                                appResponse.add(parser.parse(categoryJson.getString(i)));
                            }
                        } catch (Exception e) {
                            return null;
                        }
                        mAppGlResponse.apps = appResponse;
                        AppUtils.setAppStates(appResponse);

                    } else {
                        GsonParser<AppResponse> parser = new GsonParser<AppResponse>(
                                AppResponse.class);
                        try {
                            app = parser.parse(jsonRes.toString());
                        } catch (Exception e) {
                            return null;

                        }
                        mAppGlResponse = new AppResponse();
                        mAppGlResponse.apps = new ArrayList<App>();
                        mAppGlResponse.apps.add(app.apps.get(0));
                        AppUtils.setAppStates(mAppGlResponse.apps);
                    }
                } else {
                    mAppGlResponse = new AppResponse();
                    mAppGlResponse.apps = new ArrayList<App>();

                }


            } else {
                jsonRes = new JSONObject(response);
                mAppGlResponse = new AppResponse();
                mAppGlResponse.apps = new ArrayList<App>();
                mAppGlResponse.error = new AppResponse.ResponseError();
                mAppGlResponse.error.code = Integer.parseInt(jsonRes.getString("errorCode"));
                mAppGlResponse.error.message = jsonRes.getString("errorMessage");
            }
            return mAppGlResponse;


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mAppGlResponse;
    }


    @Override
    public AppResponse getApps(String group, String category, int startIndex, int pageCount) {
        final Semaphore mutex = new Semaphore(1);

        try {

            mutex.acquire();
            mAppResponse = getGLAppStoreData(AppStoreConstants.URL_JIO_APP_CENTER, group, category, 1, null);
            mutex.release();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mAppResponse;
    }


}
