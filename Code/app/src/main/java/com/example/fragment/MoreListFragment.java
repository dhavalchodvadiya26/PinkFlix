package com.example.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.util.GradientTextView;
import com.example.util.fullscreen.FullScreenDialogContent;
import com.example.util.fullscreen.FullScreenDialogController;
import com.example.streamingapp.MyApplication;
import com.example.streamingapp.R;

public class MoreListFragment extends Fragment implements FullScreenDialogContent {

    private static final String EXTRA_NAME = "EXTRA_NAME";
    private static final String RESULT_FULL_NAME = "RESULT_FULL_NAME";


    private FullScreenDialogController dialogController;
    private MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        myApplication = MyApplication.getInstance();
        GradientTextView nav_name = rootView.findViewById(R.id.nav_name);
        GradientTextView nav_email = rootView.findViewById(R.id.nav_email);
        GradientTextView txtMyAccount = rootView.findViewById(R.id.txtMyAccount);
        GradientTextView txtWatchList = rootView.findViewById(R.id.txtWatchList);
        GradientTextView txtTransaction = rootView.findViewById(R.id.txtTransaction);
        GradientTextView txtMemberShip = rootView.findViewById(R.id.txtMemberShip);
        GradientTextView txtSetting = rootView.findViewById(R.id.txtSetting);
        GradientTextView txtLogout = rootView.findViewById(R.id.txtLogout);
        GradientTextView txtHelp = rootView.findViewById(R.id.txtHelp);

        if (myApplication.getIsLogin())
            txtLogout.setText("Logout");
        else
            txtLogout.setText("Login");

        if (myApplication.getIsLogin()) {
            nav_name.setText(myApplication.getUserName());
            nav_email.setText(myApplication.getUserEmail());
        }

        txtMyAccount.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(RESULT_FULL_NAME, "MyAccount");
            dialogController.confirm(result);
        });
        txtWatchList.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(RESULT_FULL_NAME, "WatchList");
            dialogController.confirm(result);
        });
        txtTransaction.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(RESULT_FULL_NAME, "MyTransaction");
            dialogController.confirm(result);
        });
        txtMemberShip.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(RESULT_FULL_NAME, "Membership");
            dialogController.confirm(result);
        });
        txtSetting.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(RESULT_FULL_NAME, "Setting");
            dialogController.confirm(result);
        });
        txtLogout.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(RESULT_FULL_NAME, txtLogout.getText().toString().trim());
            dialogController.confirm(result);
        });

        txtHelp.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(RESULT_FULL_NAME, txtHelp.getText().toString().trim());
            dialogController.confirm(result);
        });
        return rootView;

    }

    @Override
    public void onDialogCreated(FullScreenDialogController dialogController) {
        this.dialogController = dialogController;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onConfirmClick(FullScreenDialogController dialog) {
        Bundle result = new Bundle();
        assert getArguments() != null;
        result.putString(RESULT_FULL_NAME, getArguments().getString(EXTRA_NAME) + " ");
        dialog.confirm(result);
        return true;
    }

    @Override
    public boolean onDiscardClick(FullScreenDialogController dialog) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(R.string.txt_watchlist_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogController.discard();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing to do
                    }
                }).show();

        return true;
    }

    @Override
    public boolean onExtraActionClick(MenuItem actionItem, FullScreenDialogController dialogController) {
        Toast.makeText(getContext(), actionItem.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }
}
