package com.capstone.pixscramble.server;

import android.content.Context;

import com.capstone.pixscramble.TaskExecutor;
import com.capstone.pixscramble.TaskExecutorFactory;

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

    private class FlickrStoreTask implements Runnable {
        public String mResponse;
        private final String mUrl;

        public FlickrStoreTask(String url) {
            mUrl = url;
        }

        @Override
        public void run() {
            mResponse = performGetFlickrStoreData(mUrl);
        }
    }

    @Override
    public String getFlickrStoreData(String url) {
        FlickrStoreTask task = new FlickrStoreTask(url);
        getTaskExecutor().execute(task);
        return task.mResponse;
    }

    private String performGetFlickrStoreData(String url) {
        String response = null;
        try {
            if (url != null)
            {
                response = fetchPlainText(new URL(url));
            }

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
