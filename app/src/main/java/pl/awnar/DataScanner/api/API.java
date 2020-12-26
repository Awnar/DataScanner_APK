package pl.awnar.DataScanner.api;

import android.annotation.SuppressLint;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.util.Observable;

import pl.awnar.DataScanner.LoginActivity;
import pl.awnar.DataScanner.api.model.home;
import pl.awnar.DataScanner.api.model.loginRecive;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class API {
    static private String TOKEN = "";
    static private String API_POINT = "";
    static final private String API_URL = "http://192.168.1.11:5000";
    @SuppressLint("StaticFieldLeak")
    static private LoginActivity mActivity;
    static private Retrofit retrofit = null;

    public static void Init() {
        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void SetActivbity(LoginActivity activity) {
        mActivity = activity;
    }

    public static void SetPoint(String point) {
        API_POINT = point;
    }

    public static void SetToken(String token) {
        TOKEN = token;
    }

    public static class Home extends Observable implements Callback<home> {
        public void Run() {
            try {
                homeIF req = retrofit.create(homeIF.class);
                Call<home> call = req.HomeF();
                call.enqueue(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        public void onResponse(@NotNull Call<home> call, Response<home> response) {
            setChanged();
            if (response.isSuccessful())
                notifyObservers(response.body());
            else
                notifyObservers(null);
        }

        @Override
        public void onFailure(@NotNull Call<home> call, Throwable t) {
            setChanged();
            notifyObservers(null);
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        private interface homeIF {
            @GET("/")
            Call<home> HomeF();
        }
    }

    public static class Login extends Observable implements Callback<loginRecive> {
        public void Run(String name, String pass) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(pass.getBytes());
                LoginIF req = retrofit.create(LoginIF.class);
                Call<loginRecive> call = req.loginIF(name, bytesToHex(md.digest()));
                call.enqueue(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        private static String bytesToHex(byte[] hash) {
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }

        @Override
        public void onResponse(@NotNull Call<loginRecive> call, Response<loginRecive> response) {
            setChanged();
            if (response.isSuccessful())
                notifyObservers(response.body());
            else
                notifyObservers(null);
        }

        @Override
        public void onFailure(@NotNull Call<loginRecive> call, Throwable t) {
            setChanged();
            notifyObservers(null);
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        private interface LoginIF {
            @Multipart
            @POST("login")
            Call<loginRecive> loginIF(
                    @Part("name") String name,
                    @Part("pass") String pass
            );
        }
    }

    public static class Register extends Observable implements Callback<loginRecive> {
        public void Run(String name, String pass) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(pass.getBytes());
                RegIF req = retrofit.create(RegIF.class);
                Call<loginRecive> call = req.regIF(name, bytesToHex(md.digest()));
                call.enqueue(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        private static String bytesToHex(byte[] hash) {
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }

        @Override
        public void onResponse(@NotNull Call<loginRecive> call, Response<loginRecive> response) {
            setChanged();
            if (response.isSuccessful())
                notifyObservers(response.body());
            else
                notifyObservers(null);
        }

        @Override
        public void onFailure(@NotNull Call<loginRecive> call, Throwable t) {
            setChanged();
            notifyObservers(null);
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        private interface RegIF {
            @Multipart
            @POST("register")
            Call<loginRecive> regIF(
                    @Part("name") String name,
                    @Part("pass") String pass
            );
        }
    }
}