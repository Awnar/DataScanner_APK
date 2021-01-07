package pl.awnar.DataScanner.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Observable;

import pl.awnar.DataScanner.R;
import pl.awnar.DataScanner.api.API;
import pl.awnar.DataScanner.api.model.home;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements java.util.Observer {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_URL = "section_url";
    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;
    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index, String url) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        bundle.putString(ARG_SECTION_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void setActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        String url = "";
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            url = getArguments().getString(ARG_SECTION_URL);
        }
        pageViewModel.setIndex(index);
        pageViewModel.setUrl(url);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        // TODO update list
        super.onResume();
        Log.d("change tab", pageViewModel.getUrl());
        API.SetPoint(pageViewModel.getUrl());
        API.GetData getdata = new API.GetData();
        getdata.addObserver(this);
        getdata.Run();
    }


    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof API.Home) {
            home data = (home) o;
            if (data == null || data.endpoints == null)
                Toast.makeText(mActivity, "Błąd połączenia", Toast.LENGTH_LONG).show();
        }
    }
}