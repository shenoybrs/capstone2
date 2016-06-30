package com.capstone.offerbank;

import android.content.Context;


public class AppStoreFactory {


    public static IAppStore getAppStore(Context context) {
        return AppStoreImpl.getInstance(context);
    }
}
