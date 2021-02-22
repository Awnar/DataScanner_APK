package pl.awnar.DataScanner.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Observable;

import pl.awnar.DataScanner.DB;
import pl.awnar.DataScanner.R;
import pl.awnar.DataScanner.api.API;
import pl.awnar.DataScanner.api.model.Data;
import pl.awnar.DataScanner.api.model.List;
import pl.awnar.DataScanner.api.model.home;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements java.util.Observer {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_URL = "section_url";
    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;
    private static DB mdb;
    private PageViewModel pageViewModel;
    private IssuesAdapter adapter;
    private int last_id;

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

    public static void setDB(DB db) {
        mdb = db;
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
        //final TextView textView = root.findViewById(R.id.section_label);
        //pageViewModel.getText().observe(this, s -> textView.setText(s));

        adapter = new IssuesAdapter(root.getContext(), new ArrayList<>());
        ListView listView = root.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        /*listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Toast.makeText(root.getContext(), "zzzzzzzzz", Toast.LENGTH_LONG).show();
            //Intent item = new Intent(MainActivity.this, ItemActivity.class);
            //item.putExtra("data", new Gson().toJson(adapter.data.get(position)));
            //MainActivity.this.startActivity(item);
        });*/
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshHelper.setTab(this);
        Log.d("change tab", pageViewModel.getUrl());
        refresh();
    }

    private void refresh() {
        Log.d("refresh", pageViewModel.getUrl());
        API.SetPoint(pageViewModel.getUrl());
        API.GetList getdata = new API.GetList();
        getdata.addObserver(this);
        //TODO przetestować
        getdata.Run(mdb.getModules().get(pageViewModel.getIndex() - 1)[2]);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof API.Home) {
            home data = (home) o;
            if (data == null || data.endpoints == null)
                Toast.makeText(mActivity, R.string.connect_error, Toast.LENGTH_LONG).show();
        } else if (observable instanceof API.GetList) {
            if (o == null) {
                Toast.makeText(mActivity, R.string.connect_error, Toast.LENGTH_LONG).show();
                return;
            }
            List rec = (List) o;
            if (rec.ERROR == null) {
                if (rec.Data == null || rec.TIME == null)
                    return;
                /*mdb.updateData(pageViewModel.getUrl(), rec.Data);
                mdb.lastSync(pageViewModel.getUrl(), rec.TIME);
                adapter.setItems(mdb.getData(pageViewModel.getUrl()));
                adapter.notifyDataSetChanged();*/
                mdb.lastSync(pageViewModel.getUrl(), rec.TIME);
                last_id = 0;
                for (int item : rec.Data) {
                    API.SetPoint(pageViewModel.getUrl());
                    API.GetItem api = new API.GetItem();
                    api.addObserver(this);
                    api.Run(item);
                    last_id++;
                }
            } else
                Toast.makeText(mActivity, rec.ERROR, Toast.LENGTH_LONG).show();
        } else if (observable instanceof API.GetItem) {
            if (o == null) {
                Toast.makeText(mActivity, R.string.connect_error, Toast.LENGTH_LONG).show();
                return;
            }
            Data rec = (Data) o;
            if (rec.server_ID == null) return;
            mdb.updateData(pageViewModel.getUrl(), rec);
            //if(--last_id<=0) {
            adapter.setItems(mdb.getData(pageViewModel.getUrl()));
            adapter.notifyDataSetChanged();
            //}
        } else if (observable instanceof API.PostData)
            refresh();
    }
}