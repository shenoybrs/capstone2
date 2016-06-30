package com.capstone.offerbank;

/**
 * Created by sampath.anumolu on 5/29/2015.
 */
public class AppStoreConstants {
    public  static  final String APPSTORE_GROUP_JIOAPP ;
    public static final String APP_DETAIL_URL ;
    public static final String URL_JIO_APP_CENTER ;

    static{
        APPSTORE_GROUP_JIOAPP ="JioApps";
        APP_DETAIL_URL = "https://script.google.com/macros/s/AKfycbwZkGFGqHtFTBMpBNHQqULYaRn1zU2qcQKP8SWyD8RF0HwsENQ/exec?apikey=xxxx-xxxx-xxxx-xxxx-santosh&mode=1";
        URL_JIO_APP_CENTER = "https://script.google.com/macros/s/AKfycbwZkGFGqHtFTBMpBNHQqULYaRn1zU2qcQKP8SWyD8RF0HwsENQ/exec?apikey=xxxx-xxxx-xxxx-xxxx-santosh&mode=1";
    }

    //to get Icon on Snapshot
    public static final int ICON_TYPE_INSTALLED = 1;
    public static final int ICON_TYPE_NEWAPP = 0;
    public static final int ICON_TYPE_UPDATE_AVAIL = 2;
    public static final int ICON_TYPE_COMING_SOON = 3;
}
