package com.projectreachout.MessageManager;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

public class MessageManager {
    private Context mContext;

    public MessageManager(Context context) {
        this.mContext = context;
    }

    public void showShortToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
