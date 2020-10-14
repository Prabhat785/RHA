package com.example.rha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class splashscreen extends AppCompatActivity {
    SharedPreferences pref;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        loadingbar = new ProgressDialog(this);
        loadingbar.setTitle("Checking internet connection");
        loadingbar.setMessage("Please Wait ");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        pref = getSharedPreferences("user_details", MODE_PRIVATE);

        if(isNetworkAvailable(loadingbar)){
            if (pref.contains("username") && pref.contains("password")) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, Login.class));
                finish();
            }
        }
        else{
            Toast.makeText(splashscreen.this,"Check your internet connection",Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isNetworkAvailable(ProgressDialog loadingbar) {

        Context  context=splashscreen.this;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) {loadingbar.dismiss(); return false;}
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            loadingbar.dismiss();
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            loadingbar.dismiss();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
}
