package com.example.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

public class DialogUtils {
    Context context;
    AlertDialog alertDialog=null;
    AlertDialogListener callBack;
    Activity current_activity;

    public DialogUtils(Context context)
    {
        this.context = context;
        this.current_activity = (Activity) context;
        callBack = (AlertDialogListener) context;
    }


    public interface AlertDialogListener
    {
        public void onPositiveClick(int from);
        public void onNegativeClick(int from);
        public void onNeutralClick(int from);
    }
}
