package com.capstone.offerbank.server;

import android.content.Context;


public class CommonServerProviderFactory {
    private static CommonServerProviderInterface sProvider;

    public static synchronized CommonServerProviderInterface createProvider(Context context){
        if(sProvider == null){
            sProvider = new CommonServerProvider(context);
        }
        return sProvider;
    }
}
