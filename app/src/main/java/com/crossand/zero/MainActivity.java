package com.crossand.zero;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private int pStatus = 0;
    private ProgressBar progressBar;

    private final Handler handler = new Handler(Looper.myLooper());
    public static boolean isGame = true;
    private boolean isfirst = true;
    private SharedPreferences.Editor myEditor;
    private SharedPreferences myPreferences;
    @SuppressLint("StaticFieldLeak")
    public static MainActivity activity;

    @SuppressLint({"SourceLockedOrientationActivity", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createProgress();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity = this;
        myPreferences = this.getSharedPreferences(Configuration.PREF_NAME.getData(), MODE_PRIVATE);
        myEditor = myPreferences.edit();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isGame) {
                Intent myIntent = new Intent(this, MainUserGameActivity.class);
                startActivity(myIntent);
                this.finish();
            }
        }, 5 * 1000);
    }

    void createProgress() {
        progressBar = findViewById(R.id.progressBar);
        new Thread(() -> {
            while (true) {
                handler.post(() -> progressBar.setProgress(pStatus));
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pStatus++;
                if (pStatus == 100) {
                    pStatus = 0;
                }
            }
        }).start();
    }

    @SuppressLint("CommitPrefEdits")
    public void initData(String deep) {
        if (isfirst) {
            isfirst = false;
            Connections VConnect = new Connections(this, myPreferences, this, myEditor, deep);
            VConnect.getGeo();
        }
    }
}