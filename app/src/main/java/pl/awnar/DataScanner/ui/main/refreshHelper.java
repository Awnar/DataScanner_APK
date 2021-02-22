package pl.awnar.DataScanner.ui.main;

import android.annotation.SuppressLint;

public class refreshHelper {
    @SuppressLint("StaticFieldLeak")
    private static PlaceholderFragment ttab;

    public static PlaceholderFragment getTab() {
        return ttab;
    }

    public static void setTab(PlaceholderFragment tab) {
        ttab = tab;
    }
}
