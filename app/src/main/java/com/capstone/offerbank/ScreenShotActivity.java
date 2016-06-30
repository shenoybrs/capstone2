package com.capstone.offerbank;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;


public class ScreenShotActivity extends Activity{

    private ImageView mImageView;
    public static final String ARG_URL = "item_url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_screenshot);
        mImageView = (ImageView) findViewById(R.id.icon);
        String url = getIntent().getExtras().getString(ARG_URL);
        JSSCommonService.getInstance(this).getImageLoader().get(url, new ImageListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onResponse(final ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    mImageView.setImageDrawable(new BitmapDrawable(getResources(), response
                            .getBitmap()));
                }
            }
        });
    }
}
