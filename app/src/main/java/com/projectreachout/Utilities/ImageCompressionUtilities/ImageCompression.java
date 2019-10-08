package com.projectreachout.Utilities.ImageCompressionUtilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import id.zelory.compressor.Compressor;

public class ImageCompression {

    private Context context;
    private File actualImage;

    public ImageCompression(Context context, File actualImage) {
        this.context = context;
        this.actualImage = actualImage;
    }

    public String customCompressImage() {

        Log.v("zzzzz", actualImage.toString());
        if (actualImage == null) {
            showMessage("Please choose an image!");
            return null;
        }

        // Compress image in main thread using custom Compressor
        File compressedImage = null;
        try {
            compressedImage = new Compressor(context)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(actualImage);

            // compressedImageView.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
            // compressedSizeTextView.setText(String.format("Size : %s", getReadableFileSize(compressedImage.length())));

            Log.v("zzzzz", "compressed Abds: " + compressedImage.getAbsolutePath());
            Log.v("zzzzz", "compressed: " + compressedImage.getPath());
            Log.v("zzzzz", "Original Size: " + String.format("Size : %s", getReadableFileSize(actualImage.length())));
            Log.v("zzzzz", "Compressed Size: " + String.format("Size : %s", getReadableFileSize(compressedImage.length())));


            return compressedImage.getPath();

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("zzzzz", "catch:  " + e.getMessage());

            showMessage(e.getMessage());
        } /*finally {
            showMessage(compressedImage.getPath());
            Log.v("zzzzz", "compressed Abds: " + compressedImage.getAbsolutePath());
            Log.v("zzzzz", "compressed: " + compressedImage.getPath());
        }*/

        return null;
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void showMessage(String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
