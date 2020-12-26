package pl.awnar.DataScanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import pl.awnar.DataScanner.api.API;
import pl.awnar.DataScanner.api.model.home;
import pl.awnar.DataScanner.api.model.loginRecive;


public class LoginActivity extends AppCompatActivity implements Observer {

    private Spinner spinner;
    private home data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        API.Init();
        API.SetActivbity(this);
        API.Home home = new API.Home();
        home.addObserver(this);
        home.Run();

        API.Login login = new API.Login();
        login.addObserver(this);
        API.Register register = new API.Register();
        register.addObserver(this);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button registerButton = findViewById(R.id.register);
        spinner = findViewById(R.id.spinner);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                boolean enable = !editable.toString().trim().isEmpty() && !passwordEditText.getText().toString().trim().isEmpty();
                loginButton.setEnabled(enable);
                registerButton.setEnabled(enable);
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                boolean enable = !editable.toString().trim().isEmpty() && !usernameEditText.getText().toString().trim().isEmpty();
                loginButton.setEnabled(enable);
                registerButton.setEnabled(enable);
            }
        });

        loginButton.setOnClickListener(view -> {
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
        });

        registerButton.setOnClickListener(view -> {
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
            if (actionId == EditorInfo.IME_ACTION_DONE) {
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
                login.Run(username, password);
                return true;
            }
            return false;
        };

        passwordEditText.setOnEditorActionListener(editorListener);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof API.Home) {
            if (o == null) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.connect_error);
                return;
            }
            data = (home) o;
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            for (Map.Entry<String, String> entry : data.name.entrySet())
                spinnerAdapter.add(entry.getValue());
            spinnerAdapter.notifyDataSetChanged();
        }
        if (observable instanceof API.Login || observable instanceof API.Register) {
            if (o == null) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.bad_login_data);
                return;
            }
            loginRecive rec = (loginRecive) o;
            if (rec.Error == null) {
                API.SetToken(rec.Authorization);
                API.SetPoint(data.url.get(((Integer) spinner.getSelectedItemPosition()).toString()));

                //Intent myIntent = new Intent(this, MainActivity.class);
                //startActivity(myIntent);
            }else{
                ((TextView) findViewById(R.id.ERROR)).setText(rec.Error);
            }
        }
    }
}