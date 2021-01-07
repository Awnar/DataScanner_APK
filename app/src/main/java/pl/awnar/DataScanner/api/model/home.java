package pl.awnar.DataScanner.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;
import java.util.TreeMap;

public class home implements Parcelable {
    public Map<String, Map<String, String>> endpoints;

    protected home(Parcel in) {
        endpoints = new TreeMap<>();
        in.readMap(endpoints, home.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(endpoints);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<home> CREATOR = new Creator<home>() {
        @Override
        public home createFromParcel(Parcel in) {
            return new home(in);
        }

        @Override
        public home[] newArray(int size) {
            return new home[size];
        }
    };
}
