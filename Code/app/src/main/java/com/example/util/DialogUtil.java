package com.example.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {
    public static void AlertBox(final Context context, final String title, final String message) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
        alertbox.setTitle(title);
        alertbox.setCancelable(false);
        alertbox.setMessage(message);
        alertbox.setNeutralButton("OK", (dialog, which) -> dialog.dismiss());

        alertbox.show();
    }

}
