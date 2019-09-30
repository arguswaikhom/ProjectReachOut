package com.projectreachout.Utilities.MessageUtilities;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

public class MessageUtils {
    public interface OnSnackBarActionListener {
        void onActionBarClicked(View view, int requestCode);
    }

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    public void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showNoActionShortSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showActionIndefiniteSnackBar(View view, String message, String actionMsg, int requestCode, OnSnackBarActionListener onSnackbarActionListener) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction(actionMsg, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSnackbarActionListener.onActionBarClicked(view, requestCode);
            }
        }).show();
    }
}
