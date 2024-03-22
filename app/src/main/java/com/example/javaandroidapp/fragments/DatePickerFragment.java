package com.example.javaandroidapp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.example.javaandroidapp.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Button button = getActivity().findViewById(R.id.addDate);
        button.setText(String.format("%d/%d/%d", dayOfMonth, month, year));
        new TimePickerFragment().show(getActivity().getSupportFragmentManager(), "timePicker");
    }
}