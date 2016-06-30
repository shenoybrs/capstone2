package com.capstone.offerbank.server;

import android.content.Context;

import com.capstone.offerbank.TaskExecutor;
import com.capstone.offerbank.TaskExecutorFactory;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CommonServerProvider implements CommonServerProviderInterface {

    private final Context mContext;

    public CommonServerProvider(Context context) {
        mContext = context;
    }

    private TaskExecutor getTaskExecutor() {
        return TaskExecutorFactory.getInstance().getTaskExecutor();
    }

    private class AppStoreTask implements Runnable {
        public String mResponse;
        private final String mGroup;
        private final String mCategoryName;
        private final Integer mMode;
        private final String mPackageName;
        private final String mUrl;

        public AppStoreTask(String url, String group, String categoryName, Integer mode, String packageName) {
            mGroup = group;
            mCategoryName = categoryName;
            mMode = mode;
            mPackageName = packageName;
            mUrl = url;
        }

        @Override
        public void run() {
            mResponse = performGetAppStoreData(mUrl, mGroup, mCategoryName, mMode, mPackageName);
        }
    }

    @Override
    public String getAppStoreData(String url, String group, String category, Integer mode, String packageName) {
        AppStoreTask task = new AppStoreTask(url, group, category, mode, packageName);
        getTaskExecutor().execute(task);
        return task.mResponse;
    }

    private String performGetAppStoreData(String url, String group, String categoryName,  Integer mode, String packageName) {
        String response = null;
        try {

            if (packageName != null)
            {
                url = url + "&id=" +packageName;
            }
            response = fetchPlainText(new URL(url));


        } catch (Exception e) {
        }
        return response;
    }

    static String fetchPlainText(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
