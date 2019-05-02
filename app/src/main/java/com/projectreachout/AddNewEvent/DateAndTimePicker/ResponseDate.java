package com.projectreachout.AddNewEvent.DateAndTimePicker;

import android.os.Parcelable;

public interface ResponseDate extends Parcelable {
    void setDate(int year, int month, int day);
}