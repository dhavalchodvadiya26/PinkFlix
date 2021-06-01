package com.example.dialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class DatePickerDialogFragment extends DialogFragment {
    int selectedYear, selectedMonth, selectedDay;
    OnDateChangeListener mOnDateChangeListener;

    public DatePickerDialogFragment(OnDateChangeListener mOnDateChangeListener) {
        this.mOnDateChangeListener = mOnDateChangeListener;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), (view, year1, month1, dayOfMonth) -> {
            selectedDay = dayOfMonth;
            selectedMonth = (month1 + 1);
            selectedYear = year1;
            mOnDateChangeListener.onDateChanged(String.format("%02d", selectedDay) + "/" + String.format("%02d", selectedMonth) + "/" + selectedYear);
        }, year, month, day);
        mDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        return mDialog;
    }

    public interface OnDateChangeListener {
        void onDateChanged(String date);
    }
}