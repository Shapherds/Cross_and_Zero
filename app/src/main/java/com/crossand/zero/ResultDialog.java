package com.crossand.zero;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ResultDialog extends DialogFragment {
    private final boolean isWin;
    ResultDialog(boolean isWin ){
        this.isWin=isWin;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title;
        String body;
        if (isWin) {
            title = "Player 1 win!";
            body = "player 2 be careful";
        }
        else{
            title = "player 2 Win!";
            body = "player 1 be careful";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle(title)
                .setMessage(body);
        return builder.create();
    }
}
