package com.projectreachout.Utilities.DateAndTimePicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

import static com.projectreachout.GeneralStatic.DATE_RESPONSE;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        ResponseDate responseDate = getArguments() != null ? getArguments().getParcelable(DATE_RESPONSE) : null;
        if (responseDate != null) {
            responseDate.setDate(year, month, day);
        }
    }
}
