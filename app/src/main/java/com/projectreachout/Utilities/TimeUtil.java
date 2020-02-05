package com.projectreachout.Utilities;

import android.text.format.DateUtils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String getTimeAgaFromSecond(long second) {
        return DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(second),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }
}
