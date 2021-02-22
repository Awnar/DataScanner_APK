package pl.awnar.DataScanner.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.util.Observable;

import pl.awnar.DataScanner.R;
import pl.awnar.DataScanner.api.model.Data;
import pl.awnar.DataScanner.api.model.List;
import pl.awnar.DataScanner.api.model.Test;
import pl.awnar.DataScanner.api.model.home;
import pl.awnar.DataScanner.api.model.loginRecive;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class API {
    static private String TOKEN = "";
    static private String API_POINT = "";
    static final private String API_URL = "http://192.168.1.11:5000";
    @SuppressLint("StaticFieldLeak")
    static private Activity mActivity;
    static private Retrofit retrofit = null;

    public static void Init() {
        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void SetActivbity(Activity activity) {
        mActivity = activity;
    }

    public static void SetPoint(String point) {
        API_POINT = point;
    }

    public static void SetToken(String token) {
        TOKEN = token;
    }

    public static String getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return mActivity.getString(R.string.app_name) + " " + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static class Home extends Observable implements Callback<home> {
        public void Run() {
            try {
                homeIF req = retrofit.create(homeIF.class);
                Call<home> call = req.HomeF(getAppVersion(mActivity));
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
            Call<home> HomeF(@Header("User-Agent") String userAgent);
        }
    }

    public static class APItest extends Observable implements Callback<Test> {
        public void Run(String key) {
            try {
                TestIF req = retrofit.create(TestIF.class);
                Call<Test> call = req.testIF(getAppVersion(mActivity), key);
                call.enqueue(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        public void onResponse(@NotNull Call<Test> call, Response<Test> response) {
            setChanged();
            if (response.isSuccessful())
                notifyObservers(response.body());
            else
                notifyObservers(null);
        }

        @Override
        public void onFailure(@NotNull Call<Test> call, Throwable t) {
            setChanged();
            notifyObservers(null);
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        private interface TestIF {
            @GET("/Test")
            Call<Test> testIF(
                    @Header("User-Agent") String userAgent,
                    @Header("Authorization") String authorization);
        }
    }

    public static class Login extends Observable implements Callback<loginRecive> {
        public void Run(String name, String pass) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(pass.getBytes());
                LoginIF req = retrofit.create(LoginIF.class);
                Call<loginRecive> call = req.loginIF(getAppVersion(mActivity), name, bytesToHex(md.digest()));
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
                    @Header("User-Agent") String userAgent,
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
                Call<loginRecive> call = req.regIF(getAppVersion(mActivity), name, bytesToHex(md.digest()));
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
                    @Header("User-Agent") String userAgent,
                    @Part("name") String name,
                    @Part("pass") String pass
            );
        }
    }

    public static class GetList extends Observable implements Callback<List> {
        public void Run(String lastsync) {
            try {
                DataIF req = retrofit.create(DataIF.class);
                Call<List> call = req.dataIF(API_POINT + "/", getAppVersion(mActivity), TOKEN, lastsync);
                call.enqueue(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        public void onResponse(@NotNull Call<List> call, Response<List> response) {
            setChanged();
            if (response.isSuccessful())
                notifyObservers(response.body());
            else
                notifyObservers(null);
        }

        @Override
        public void onFailure(@NotNull Call<List> call, Throwable t) {
            setChanged();
            notifyObservers(null);
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        private interface DataIF {
            //@Multipart
            @GET
            Call<List> dataIF(
                    @Url String url,
                    @Header("User-Agent") String userAgent,
                    @Header("Authorization") String authorization,
                    @Query("lastupdate") String lastupdate);
        }
    }

    public static class GetItem extends Observable implements Callback<Data> {
        public void Run(int id) {
            try {
                ItemIF req = retrofit.create(ItemIF.class);
                Call<Data> call = req.itemIF(API_POINT + "/" + id, getAppVersion(mActivity), TOKEN);
                call.enqueue(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        public void onResponse(@NotNull Call<Data> call, Response<Data> response) {
            setChanged();
            if (response.isSuccessful())
                notifyObservers(response.body());
            else
                notifyObservers(null);
        }

        @Override
        public void onFailure(@NotNull Call<Data> call, Throwable t) {
            setChanged();
            notifyObservers(null);
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        private interface ItemIF {
            //@Multipart
            @GET
            Call<Data> itemIF(
                    @Url String url,
                    @Header("User-Agent") String userAgent,
                    @Header("Authorization") String authorization);
        }
    }

    public static class PostData extends Observable implements Callback<Data> {
        public void Run(Data data) {
            try {
                DataIF req = retrofit.create(DataIF.class);
                Call<Data> call = req.dataIF(API_POINT + "/", getAppVersion(mActivity), TOKEN, data);
                call.enqueue(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        public void onResponse(@NotNull Call<Data> call, Response<Data> response) {
            setChanged();
            if (response.isSuccessful())
                notifyObservers(response.body());
            else
                notifyObservers(null);
        }

        @Override
        public void onFailure(@NotNull Call<Data> call, Throwable t) {
            setChanged();
            notifyObservers(null);
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        private interface DataIF {
            @Multipart
            @POST
            Call<Data> dataIF(
                    @Url String url,
                    @Header("User-Agent") String userAgent,
                    @Header("Authorization") String authorization,
                    @Part("data") Data data);
        }
    }

    public static class Logout extends Observable implements Callback<loginRecive> {
        public void Run() {
            try {
                LogoutIF req = retrofit.create(LogoutIF.class);
                Call<loginRecive> call = req.logoutIF();
                call.enqueue(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
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

        private interface LogoutIF {
            @POST("logout")
            Call<loginRecive> logoutIF();
        }
    }
}