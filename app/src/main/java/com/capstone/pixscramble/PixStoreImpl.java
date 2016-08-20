package com.capstone.pixscramble;

import android.content.Context;

import com.capstone.pixscramble.gson.GsonParser;
import com.capstone.pixscramble.server.CommonServerProviderFactory;
import com.capstone.pixscramble.server.CommonServerProviderInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class PixStoreImpl implements IPixStore {

    private static PixStoreImpl sInstance;
    private PixResponse mPixResponse;
    private final Context mContext;

    public static PixStoreImpl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PixStoreImpl(context);

        }

        return sInstance;
    }

    private PixStoreImpl(Context context) {
        mContext = context;
    }


    private PixResponse getFlickrPix(String url) {
        JSONObject jsonRes = null;
        PixResponse pixResponse = new PixResponse();
        PixResponse app = null;
        JSONArray categoryJson = null;
        CommonServerProviderInterface provider = CommonServerProviderFactory.createProvider(mContext);
        String jioResponse = null;
        ArrayList<Pix> list = new ArrayList<>();
        Pix singleApp;
        try {
            String response = null;
            int code = 200;
            response = provider.getFlickrStoreData(url);

            if (code == 200) {

                if (!response.isEmpty() || !response.equals(null)) {
                    jsonRes = new JSONObject(response);
                    //THis check is needed to distinguish response for single app
                    //and Array of apps
                    categoryJson = jsonRes.getJSONArray("items");
                    GsonParser<Pix> parser = new GsonParser<Pix>(
                            Pix.class);
                    try {
                        pixResponse = new PixResponse();


                        for (int i = 0; i < categoryJson.length(); i++) {
                            JSONObject temp = categoryJson.getJSONObject(i);
                            String imageUrl = temp.getString("media");


                            list.add(parser.parse(imageUrl));
                        }
                    } catch (Exception e) {
                        return null;
                    }
                    pixResponse.pixs = list;


                } else {
                    jsonRes = new JSONObject(response);
                    pixResponse = new PixResponse();
                    pixResponse.pixs = new ArrayList<Pix>();
                    pixResponse.error = new PixResponse.ResponseError();
                    pixResponse.error.code = Integer.parseInt(jsonRes.getString("errorCode"));
                    pixResponse.error.message = jsonRes.getString("errorMessage");

                }


            } else {
                jsonRes = new JSONObject(response);
                pixResponse = new PixResponse();
                pixResponse.pixs = new ArrayList<Pix>();
                pixResponse.error = new PixResponse.ResponseError();
                pixResponse.error.code = Integer.parseInt(jsonRes.getString("errorCode"));
                pixResponse.error.message = jsonRes.getString("errorMessage");
            }
            return pixResponse;


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pixResponse;
    }


    @Override
    public PixResponse getPixs() {
        final Semaphore mutex = new Semaphore(1);

        try {

            mutex.acquire();
            mPixResponse = getFlickrPix(PixStoreConstants.URL_JIO_APP_CENTER);
            mutex.release();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mPixResponse;
    }


}
