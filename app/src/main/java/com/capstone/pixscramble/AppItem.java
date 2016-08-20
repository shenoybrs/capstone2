package com.capstone.pixscramble;

import java.net.MalformedURLException;
import java.net.URL;

public class AppItem {
    private int read;
    private URL iconUrl = null;




     public AppItem(String iconUrl, int read)  {

        try {
            this.iconUrl= new URL(iconUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
         this.read = read;
        // Default to not installed ...
    }


    public void setRead(int aRead) {
        read = aRead;
    }

    public int getRead() {
        return read;
    }




    public URL getURL() {
        return iconUrl;
    }

}
