package pl.awnar.DataScanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import pl.awnar.DataScanner.api.API;
import pl.awnar.DataScanner.api.model.Test;
import pl.awnar.DataScanner.api.model.home;
import pl.awnar.DataScanner.api.model.loginRecive;


public class LoginActivity extends AppCompatActivity implements Observer {

    private home data;
    private String name;
    private String key;
    private boolean login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        API.Init();
        API.SetActivbity(this);
        API.Home home = new API.Home();
        home.addObserver(this);
        home.Run();

        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        key = sharedPref.getString("API_key", "");
        if (!key.isEmpty()) {
            API.APItest test = new API.APItest();
            test.addObserver(this);
            test.Run(key);
            name = sharedPref.getString("user_name", "");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        API.Login login = new API.Login();
        login.addObserver(this);
        API.Register register = new API.Register();
        register.addObserver(this);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button registerButton = findViewById(R.id.register);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean enable = !editable.toString().trim().isEmpty() && !passwordEditText.getText().toString().trim().isEmpty();
                loginButton.setEnabled(enable);
                registerButton.setEnabled(enable);
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean enable = !editable.toString().trim().isEmpty() && !usernameEditText.getText().toString().trim().isEmpty();
                loginButton.setEnabled(enable);
                registerButton.setEnabled(enable);
            }
        });

        loginButton.setOnClickListener(view -> {
            if (isdata()) return;

            String username = usernameEditText.getText().toString().trim();
            if (username.isEmpty()) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.username_empty);
                return;
            }
            String password = passwordEditText.getText().toString().trim();
            if (password.length() < 4) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.password_short);
                return;
            }
            login.Run(username, password);
            name = username;
        });

        registerButton.setOnClickListener(view -> {
            if (isdata()) return;

            String username = usernameEditText.getText().toString().trim();
            if (username.isEmpty()) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.username_empty);
                return;
            }
            String password = passwordEditText.getText().toString().trim();
            if (password.length() < 4) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.password_short);
                return;
            }
            register.Run(username, password);
        });

        TextView.OnEditorActionListener editorListener = (v, actionId, event) -> {
            if (isdata()) return false;

            String username = usernameEditText.getText().toString().trim();
            if (username.isEmpty()) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.username_empty);
                return false;
            }
            String password = passwordEditText.getText().toString().trim();
            if (password.length() < 4) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.password_short);
                return false;
            }
            hideKeyboard(this);
            login.Run(username, password);
            name = username;
            return true;
        };

        passwordEditText.setOnEditorActionListener(editorListener);
        usernameEditText.setText(sharedPref.getString("user_name", ""));
    }

    private boolean isdata() {
        if (data != null && data.endpoints != null)
            return false;
        ((TextView) findViewById(R.id.ERROR)).setText(R.string.connect_error);
        return true;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof API.APItest) {
            Test res = (Test) o;
            if (res == null || res.OK == null) {
                key = null;
                return;
            }
            if (data == null || data.endpoints == null) {
                login = true;
                return;
            }
            API.SetToken(key);
            Intent myIntent = new Intent(this, MainActivity.class);
            myIntent.putExtra("home", data);
            myIntent.putExtra("name", name);
            startActivity(myIntent);
            finish();
        }
        if (observable instanceof API.Home) {
            data = (home) o;
            if (data == null || data.endpoints == null) {
                data = null;
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.connect_error);
                return;
            }
            prepareHome(data);
            if (login) {
                API.SetToken(key);
                Intent myIntent = new Intent(this, MainActivity.class);
                myIntent.putExtra("home", data);
                myIntent.putExtra("name", name);
                startActivity(myIntent);
                finish();
            }
        }
        if (observable instanceof API.Login || observable instanceof API.Register) {
            if (o == null) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.connect_error);
                return;
            }
            loginRecive rec = (loginRecive) o;
            if (rec.ERROR == null) {
                if (name == null) {
                    Toast.makeText(this, R.string.any_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                API.SetToken(rec.Authorization);
                SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
                sharedPref.edit().putString("API_key", rec.Authorization).apply();
                Intent myIntent = new Intent(this, MainActivity.class);
                myIntent.putExtra("home", data);
                myIntent.putExtra("name", name);
                startActivity(myIntent);
                finish();
            } else {
                ((TextView) findViewById(R.id.ERROR)).setText(rec.ERROR);
            }
        }
    }

    public static void prepareHome(home data) {
        ArrayList<String> toDelete = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : data.endpoints.entrySet()) {
            if (entry.getValue().containsKey("name") && entry.getValue().containsKey("url"))
                if (!entry.getValue().get("name").equals("") && !entry.getValue().get("url").equals(""))
                    continue;
            toDelete.add(entry.getKey());
        }
        for (String object : toDelete)
            data.endpoints.remove(object);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null)
            view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}