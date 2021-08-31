package com.crossand.zero;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainUserGameActivity extends AppCompatActivity {
    public static MainUserGameActivity act;
    Drawable gamer1;
    Drawable gamer2;
    boolean whatPlayer1 = true;
    boolean isWin = true;

    ImageView im1;
    ImageView im2;
    ImageView im3;
    ImageView im4;
    ImageView im5;
    ImageView im6;
    ImageView im7;
    ImageView im8;
    ImageView im9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_game);
        act = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initData();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initData() {
        gamer1 = getDrawable(R.drawable.l);
        gamer2 = getDrawable(R.drawable.b);
        im1 = findViewById(R.id.im1);
        im2 = findViewById(R.id.im2);
        im3 = findViewById(R.id.im3);
        im4 = findViewById(R.id.im4);
        im5 = findViewById(R.id.im5);
        im6 = findViewById(R.id.im6);
        im7 = findViewById(R.id.im7);
        im8 = findViewById(R.id.im8);
        im9 = findViewById(R.id.im9);
    }

    private void showResult(boolean isWin) {
        ResultDialog resultDialog = new ResultDialog(isWin);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        resultDialog.show(transaction, "dialog");
    }

    public void set( View view) {
        if (view.getForeground() == null) {
            if (whatPlayer1) {
                view.setForeground(gamer1);
                checkWin(gamer1);
                whatPlayer1 = false;
            } else {
                view.setForeground(gamer2);
                checkWin(gamer2);
                whatPlayer1 = true;
            }
        }
    }

    private void checkWin(Drawable player) {
        if (im1.getForeground() == player && im2.getForeground() == player && im3.getForeground() == player) {
            winResult(im1, im2, im3, player);
        } else if (im4.getForeground() == player && im5.getForeground() == player && im6.getForeground() == player) {
            winResult(im4, im5, im6, player);
        } else if (im7.getForeground() == player && im8.getForeground() == player && im9.getForeground() == player) {
            winResult(im7, im8, im9, player);
        } else if (im1.getForeground() == player && im4.getForeground() == player && im7.getForeground() == player) {
            winResult(im1, im4, im7, player);
        } else if (im2.getForeground() == player && im5.getForeground() == player && im8.getForeground() == player) {
            winResult(im2, im5, im8, player);
        } else if (im3.getForeground() == player && im6.getForeground() == player && im9.getForeground() == player) {
            winResult(im3, im6, im9, player);
        } else if (im1.getForeground() == player && im5.getForeground() == player && im9.getForeground() == player) {
            winResult(im1, im5, im9, player);
        } else if (im7.getForeground() == player && im5.getForeground() == player && im3.getForeground() == player) {
            winResult(im7, im5, im3, player);
        } else if (im1.getForeground() != null && im2.getForeground() != null &&
                im3.getForeground() != null && im4.getForeground() != null && im5.getForeground() != null
                && im6.getForeground() != null && im7.getForeground() != null && im8.getForeground() != null
                && im9.getForeground() != null) {
            Toast.makeText(this, "Draw !", Toast.LENGTH_LONG).show();
            new Handler(Looper.getMainLooper()).postDelayed(this::refreshGame, 1000);
        }
    }

    private void winResult(ImageView im1, ImageView im2, ImageView im3, Drawable player) {
        if (player == gamer1 && isWin) {
            showResult(true);
            isWin = false;
            setLine(im1, im2, im3);
        } else if (player == gamer2 && isWin) {
            showResult(false);
            isWin = false;
            setLine(im1, im2, im3);
        }
    }

    void setLine(ImageView im1, ImageView im2, ImageView im3) {
        im1.setBackground(getDrawable(R.color.teal_700));
        im2.setBackground(getDrawable(R.color.teal_700));
        im3.setBackground(getDrawable(R.color.teal_700));
        new Handler(Looper.getMainLooper()).postDelayed(this::refreshGame, 1000);
    }

    void refreshGame() {
        isWin = true;
        im1.setBackground(null);
        im1.setForeground(null);
        im2.setBackground(null);
        im2.setForeground(null);
        im3.setBackground(null);
        im3.setForeground(null);
        im4.setBackground(null);
        im4.setForeground(null);
        im5.setBackground(null);
        im5.setForeground(null);
        im6.setBackground(null);
        im6.setForeground(null);
        im7.setBackground(null);
        im7.setForeground(null);
        im8.setBackground(null);
        im8.setForeground(null);
        im9.setBackground(null);
        im9.setForeground(null);
        whatPlayer1 = true;
    }
}