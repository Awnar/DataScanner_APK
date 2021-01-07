package pl.awnar.DataScanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import pl.awnar.DataScanner.api.API;
import pl.awnar.DataScanner.api.model.home;
import pl.awnar.DataScanner.ui.main.PlaceholderFragment;
import pl.awnar.DataScanner.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private Image image;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB db = new DB(this);
        sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        String name = getIntent().getStringExtra("name");
        if (!name.equals(sharedPref.getString("user_name", "")))
            db.Clear();
        sharedPref.edit().putString("user_name", name).apply();

        home data = getIntent().getExtras().getParcelable("home");
        db.Module(data);

        PlaceholderFragment.setActivity(this);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), db.getModules());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            ImagePicker.create(this).single().toolbarImageTitle("Dotknij by wybrać").start();
        });

        API.SetActivbity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            image = ImagePicker.getFirstImageOrNull(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}