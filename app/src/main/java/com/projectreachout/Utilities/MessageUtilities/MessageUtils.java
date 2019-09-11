package com.projectreachout.Utilities.MessageUtilities;

import android.content.Context;
import android.widget.Toast;

public class MessageUtils {
    private Context mContext;

    public MessageUtils(Context context) {
        this.mContext = context;
    }

    public void showShortToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
