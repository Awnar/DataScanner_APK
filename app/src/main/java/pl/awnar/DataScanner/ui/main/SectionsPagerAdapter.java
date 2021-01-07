package pl.awnar.DataScanner.ui.main;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import pl.awnar.DataScanner.api.model.home;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final Map<Integer, String[]> data;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Map<Integer, String[]> data) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        this.data = data;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        int key = (int) data.keySet().toArray()[position];
        return PlaceholderFragment.newInstance(position + 1, data.get(key)[1]);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        int key = (int) data.keySet().toArray()[position];
        return data.get(key)[0];
    }

    @Override
    public int getCount() {
        return data.size();
    }
}