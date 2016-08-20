package com.capstone.pixscramble;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;

public class Pix implements Parcelable {

    public static final Creator<Pix> CREATOR = new Creator<Pix>() {

        public Pix createFromParcel(Parcel parcel) {
            return new Pix(parcel);
        }

        public Pix[] newArray(int size) {
            return new Pix[size];
        }
    };

    public URL m;


    private Pix(Parcel in) {

        m = (URL) ParcelUtils.readValue(in, URL.class.getClassLoader());


    }


    public Pix(String iconSmall) throws MalformedURLException {
        this.m = new URL(iconSmall);

    }


    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeValue(dest, m);

    }
}
