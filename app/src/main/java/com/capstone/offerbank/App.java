package com.capstone.offerbank;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class App implements Parcelable {

    public static final Creator<App> CREATOR = new Creator<App>() {

        public App createFromParcel(Parcel parcel) {
            return new App(parcel);
        }

        public App[] newArray(int size) {
            return new App[size];
        }
    };
    public int index;
    public final String name;
    public String author;
    public final String packageName;
    public final String versionName;
    public int versionCode;
    public String group;
    public String date;
    private long sizeInBytes;
    public String description;
    public double price;
    public float ratingsAverage;
    private int ratingsCount;
    public final String apkUrl;
    public URL iconSmall;
    public URL iconLarge;
    public URL iconBanner;
    public List<URL> screenShots;
    public boolean uninstallable;
    public boolean uninstallBeforeUpdate;
    public boolean installed;
    public boolean updateAvailable;
    public int currentRuns;
    public int maxRuns = 0;
    public Calendar expireDate;
    public int minsdk;
    private int required;
    public boolean livePromo;
    public String groupID;
    public final String appId;
    public final int secondaryViewMode;
    public String appcategory;
    public URL promoUrl1;
    public URL promoUrl2;
    public URL promoUrl3;
    public String tagLine;

    private App(Parcel in) {
        screenShots = new ArrayList<URL>();

        index = in.readInt();
        name = ParcelUtils.readString(in);
        author = ParcelUtils.readString(in);
        packageName = ParcelUtils.readString(in);
        versionName = ParcelUtils.readString(in);
        versionCode = in.readInt();

        group = ParcelUtils.readString(in);
        date =  ParcelUtils.readString(in);
        sizeInBytes = in.readLong();
        description = ParcelUtils.readString(in);
        price = in.readDouble();
        ratingsAverage = in.readFloat();
        ratingsCount = in.readInt();
        apkUrl = ParcelUtils.readString(in);
        iconSmall = (URL) ParcelUtils.readValue(in, URL.class.getClassLoader());
        iconLarge = (URL) ParcelUtils.readValue(in, URL.class.getClassLoader());
        iconBanner = (URL) ParcelUtils.readValue(in, URL.class.getClassLoader());
        ParcelUtils.readList(in, screenShots, URL.class.getClassLoader());
        uninstallable = in.readInt() == 1;
        uninstallBeforeUpdate = in.readInt() == 1;
        installed = in.readInt() == 1;
        updateAvailable = in.readInt() == 1;

        currentRuns = in.readInt();
        maxRuns = in.readInt();
        expireDate = (Calendar) ParcelUtils.readValue(in, Calendar.class.getClassLoader());
        minsdk = in.readInt();
        required = in.readInt();
        livePromo = in.readInt() == 1;
        groupID = in.readString();
        appId = in.readString();
        secondaryViewMode = in.readInt();
        appcategory= in.readString();
        promoUrl1 = (URL) ParcelUtils.readValue(in, URL.class.getClassLoader());
        promoUrl2 = (URL) ParcelUtils.readValue(in, URL.class.getClassLoader());
        promoUrl3 = (URL) ParcelUtils.readValue(in, URL.class.getClassLoader());
        tagLine = ParcelUtils.readString(in);

    }

    public int getNotificationId() {
        return packageName.hashCode();
    }

    public App(String name,String packageName,String versionName,String apkUrl,String appId,String groupID,int secondaryViewMode){
        this.name = name;
        this.packageName = packageName;
        this.versionName = versionName;
        this.apkUrl = apkUrl;
        this.appId = appId;
        this.groupID = groupID;
        this.secondaryViewMode= secondaryViewMode;
    }



    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        ParcelUtils.writeString(dest, name);
        ParcelUtils.writeString(dest, author);
        ParcelUtils.writeString(dest, packageName);
        ParcelUtils.writeString(dest, versionName);
        dest.writeInt(versionCode);
        ParcelUtils.writeString(dest, group);
        ParcelUtils.writeString(dest, date);
        dest.writeLong(sizeInBytes);
        ParcelUtils.writeString(dest, description);
        dest.writeDouble(price);
        dest.writeFloat(ratingsAverage);
        dest.writeInt(ratingsCount);
        ParcelUtils.writeString(dest, apkUrl);
        ParcelUtils.writeValue(dest, iconSmall);
        ParcelUtils.writeValue(dest, iconLarge);
        ParcelUtils.writeValue(dest, iconBanner);
        ParcelUtils.writeList(dest, screenShots);
        dest.writeInt(uninstallable ? 1 : 0);
        dest.writeInt(uninstallBeforeUpdate ? 1 : 0);
        dest.writeInt(installed ? 1 : 0);
        dest.writeInt(updateAvailable ? 1 : 0);

        dest.writeInt(currentRuns);
        dest.writeInt(maxRuns);
        ParcelUtils.writeValue(dest, expireDate);
        dest.writeInt(minsdk);
        dest.writeInt(required);
        dest.writeInt(livePromo ? 1 : 0 );
        dest.writeString(groupID);
        dest.writeString(appId);
        dest.writeInt(secondaryViewMode);
        dest.writeString(appcategory);
        ParcelUtils.writeValue(dest, promoUrl1);
        ParcelUtils.writeValue(dest, promoUrl2);
        ParcelUtils.writeValue(dest, promoUrl3);
        ParcelUtils.writeString(dest, tagLine);
    }
}
