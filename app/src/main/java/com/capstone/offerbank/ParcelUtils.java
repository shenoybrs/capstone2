package com.capstone.offerbank;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParcelUtils {

    public static void writeString(Parcel dest, String value) {
        dest.writeInt(value == null ? 0 : 1);
        if (value != null) {
            dest.writeString(value);
        }
    }

    public static String readString(Parcel in) {
        boolean hasValue = in.readInt() == 1;
        return hasValue ? in.readString() : null;
    }

    public static void writeStringArray(Parcel dest, String[] value) {
        dest.writeInt(value == null ? 0 : value.length);
        if (value != null) {
            dest.writeStringArray(value);
        }
    }

    public static String[] readStringArray(Parcel in) {
        int count = in.readInt();
        if (count > 0) {
            String[] val = new String[count];
            in.readStringArray(val);
            return val;
        }
        return null;
    }

    public static void writeValue(Parcel dest, Object value) {
        dest.writeInt(value == null ? 0 : 1);
        if (value != null) {
            dest.writeValue(value);
        }
    }

    public static Object readValue(Parcel in, ClassLoader loader) {
        boolean hasValue = in.readInt() == 1;
        return hasValue ? in.readValue(loader) : null;
    }

    public static void writeParcelable(Parcel dest, Parcelable value, int flags) {
        dest.writeInt(value == null ? 0 : 1);
        if (value != null) {
            dest.writeParcelable(value, flags);
        }
    }

    public static Parcelable readParcelable(Parcel in, ClassLoader loader) {
        boolean hasValue = in.readInt() == 1;
        return hasValue ? in.readParcelable(loader) : null;
    }

    public static <T extends Parcelable> void writeTypedArray(Parcel dest, T[] value, int parcelableFlags) {
        dest.writeInt(value == null ? -1 : value.length);
        if (value != null) {
            dest.writeTypedArray(value, parcelableFlags);
        }
    }

    public static <T> T[] readTypedArray(Parcel in, Creator<T> creator) {
        T[] outVal = null;
        int size = in.readInt();
        if (size > 0) {
            outVal = creator.newArray(size);
            in.readTypedArray(outVal, creator);
        }
        return outVal;
    }

    public static void writeTypedList(Parcel dest, List<? extends Parcelable> value) {
        dest.writeInt(value == null ? -1 : value.size());
        if (value != null && !value.isEmpty()) {
            dest.writeTypedList(value);
        }
    }

    public static <T> void readTypedList(Parcel in, List<T> outVal, Creator<T> creator) {
        int size = in.readInt();
        if (size > 0) {
            in.readTypedList(outVal, creator);
        } else {
            outVal = null;
        }
    }
    
    public static void writeList(Parcel dest, List<?> value) {
        dest.writeInt(value == null ? -1 : value.size());
        if (value != null && !value.isEmpty()) {
            dest.writeList(value);
        }
    }

    public static <T> void readList(Parcel in, List<T> outVal, ClassLoader loader) {
        if (in.readInt() > 0) {
            in.readList(outVal, loader);
        } else {
            outVal = null;
        }
    }

    public static void writeMap(Parcel dest, Map<String, Object> value) {
        dest.writeInt(value == null ? -1 : value.size());
        if (value != null && !value.isEmpty()) {
            Set<String> keys = value.keySet();
            for (String key : keys) {
                dest.writeString(key);
                writeValue(dest, value.get(key));
            }
        }
    }

    public static void readMap(Parcel in, Map<String, Object> outVal, ClassLoader loader) {
        int size = in.readInt();
        if (size <= 0) {
            outVal = null;
        } else {
            for (int i = 0; i < size; i++) {
                String key = in.readString();
                Object value = readValue(in, loader);
                outVal.put(key, value);
            }
        }
    }
}
