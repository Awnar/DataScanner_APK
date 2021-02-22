package pl.awnar.DataScanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayOutputStream;

import pl.awnar.DataScanner.api.API;
import pl.awnar.DataScanner.api.model.Data;
import pl.awnar.DataScanner.api.model.home;
import pl.awnar.DataScanner.ui.main.PlaceholderFragment;
import pl.awnar.DataScanner.ui.main.SectionsPagerAdapter;
import pl.awnar.DataScanner.ui.main.refreshHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API.SetActivbity(this);
        DB db = new DB(this);
        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        String name = getIntent().getStringExtra("name");
        if (!name.equals(sharedPref.getString("user_name", "")))
            db.Clear();
        sharedPref.edit().putString("user_name", name).apply();

        home data = getIntent().getExtras().getParcelable("home");
        db.Module(data);

        PlaceholderFragment.setActivity(this);
        PlaceholderFragment.setDB(db);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), db.getModules());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                refreshHelper.getTab().onResume();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> ImagePicker.create(this).single().toolbarImageTitle("Dotknij by wybrać").returnMode(ReturnMode.ALL).start());

        final Button settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(this, Settings.class);
            startActivity(myIntent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String path = ImagePicker.getFirstImageOrNull(data).getPath();
            BitmapFactory.decodeFile(path).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            Data img = new Data();
            img.in_blob = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            img.in_blob_type = "IMG";
            API.PostData post = new API.PostData();
            post.Run(img);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}