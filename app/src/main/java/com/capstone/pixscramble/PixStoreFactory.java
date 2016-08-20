package com.capstone.pixscramble;

import android.content.Context;


public class PixStoreFactory {


    public static IPixStore getPixStore(Context context) {
        return PixStoreImpl.getInstance(context);
    }
}
