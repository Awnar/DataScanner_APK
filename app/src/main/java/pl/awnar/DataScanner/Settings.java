package pl.awnar.DataScanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observable;
import java.util.Observer;

import pl.awnar.DataScanner.api.API;
import pl.awnar.DataScanner.api.model.home;
import pl.awnar.DataScanner.ui.main.refreshHelper;

public class Settings extends AppCompatActivity implements Observer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(view -> {
            API.SetActivbity(this);
            API.Logout Logout = new API.Logout();
            Logout.addObserver(this);
            Logout.Run();
        });

        final Button synchButton = findViewById(R.id.synch);
        synchButton.setOnClickListener(view -> {
            DB db = new DB(this);
            db.ClearSynchr();
        });

        final Button delButton = findViewById(R.id.del);
        delButton.setOnClickListener(view -> {
            API.SetActivbity(this);
            API.Home home = new API.Home();
            home.addObserver(this);
            home.Run();
            refreshHelper.getTab().ClearAdapter();
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof API.Logout) {
            SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
            sharedPref.edit().putString("API_key", null).apply();
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            finish();
        }
        if (observable instanceof API.Home) {
            DB db = new DB(this);
            db.Clear();
            home data = (home) o;
            LoginActivity.prepareHome(data);
            db.Module(data);
        }
    }
}