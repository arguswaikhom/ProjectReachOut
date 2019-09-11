package com.projectreachout.Utilities.PermissionUtilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class DevicePermissionUtils implements  ActivityCompat.OnRequestPermissionsResultCallback{
    private final int STORAGE_PERMISSION_CODE = 123;

    private final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private final String EXTERNAL_STORAGE_READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String EXTERNAL_STORAGE_WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private Context mContext;

    public DevicePermissionUtils(Context context) {
        this.mContext = context;
    }

    public boolean hasCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasExternalStorageReadPermissionGranted() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasExternalStorageWritePermissionGranted() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasAllPermissionsGranted() {
        return (hasCameraPermissionGranted() && hasExternalStorageReadPermissionGranted() && hasExternalStorageWritePermissionGranted());
    }

    public void requestAllPermissions() {
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{EXTERNAL_STORAGE_WRITE_PERMISSION, EXTERNAL_STORAGE_READ_PERMISSION, CAMERA_PERMISSION}, STORAGE_PERMISSION_CODE);
    }

    public void handlePermissions() {
        if(!hasAllPermissionsGranted()) {
            requestAllPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] strings, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMessage("Permission granted");
            } else {
                showMessage("Permission denied");
            }
        }
    }

    // TODO: Replace with common static method
    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    /*if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showSettingsDialog();
        }*/
}