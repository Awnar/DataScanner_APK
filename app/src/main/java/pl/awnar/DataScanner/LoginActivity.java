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

import java.io.Serializable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import pl.awnar.DataScanner.api.API;
import pl.awnar.DataScanner.api.model.home;
import pl.awnar.DataScanner.api.model.loginRecive;


public class LoginActivity extends AppCompatActivity implements Observer {

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
        }
        if (observable instanceof API.Login || observable instanceof API.Register) {
            if (o == null) {
                ((TextView) findViewById(R.id.ERROR)).setText(R.string.bad_login_data);
                return;
            }
            loginRecive rec = (loginRecive) o;
            if (rec.ERROR == null) {
                API.SetToken(rec.Authorization);
                Intent myIntent = new Intent(this, MainActivity.class);
                myIntent.putExtra("home", (Serializable) data);
                startActivity(myIntent);
            } else {
                ((TextView) findViewById(R.id.ERROR)).setText(rec.ERROR);
            }
        }
    }
}