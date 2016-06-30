package com.capstone.offerbank;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.NetworkImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ScreenshotAdapter extends ArrayAdapter<URL> {

    private final LayoutInflater mInflator;

    public ScreenshotAdapter(Context context) {
        this(context, new ArrayList<URL>());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private ScreenshotAdapter(Context context, List<URL> screenUrls) {
        super(context, 0);
        mInflator = LayoutInflater.from(context);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            addAll(screenUrls);            
        }else{
            for(URL url : screenUrls){
                add(url);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.screenshot, parent, false);
        }
        NetworkImageView niv = (NetworkImageView) convertView;
        JSSCommonService.getInstance(getContext().getApplicationContext()).getImageLoader();

        URL url = getItem(position);
        niv.setImageUrl(url.toString(),JSSCommonService.getInstance(getContext().getApplicationContext()).getImageLoader());


        return niv;
    }

    // @Override
    // public Object instantiateItem(ViewGroup container, int position) {
    // URL url = mScreenUrls.get(position);
    //
    // LayoutParams params = new LayoutParams(container.getLayoutParams());
    // params.height = LayoutParams.WRAP_CONTENT;
    // params.width = LayoutParams.WRAP_CONTENT;
    // NetworkImageView niv = new NetworkImageView(container.getContext());
    // niv.setLayoutParams(params);
    // niv.setImageUrl(url.toString(), CommonLibrary.getInstance().getImageLoader());
    // niv.setTag(url);
    //
    // return niv;
    // }

}
