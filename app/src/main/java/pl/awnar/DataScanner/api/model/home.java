package pl.awnar.DataScanner.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class home implements Parcelable {
    public Map<String, String> name;
    public Map<String, String> url;

    protected home(Parcel in) {
        name = new TreeMap<String, String>();
        in.readMap(name, home.class.getClassLoader());
        url = new TreeMap<String, String>();
        in.readMap(url, home.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(name);
        dest.writeMap(url);
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
