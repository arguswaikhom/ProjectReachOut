package com.projectreachout.AddNewArticle;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.SingleUploadBroadcastReceiver;
import com.projectreachout.Utilities.ImageCompressionUtilities.FileUtil;
import com.projectreachout.Utilities.ImageCompressionUtilities.ImageCompression;
import com.projectreachout.Utilities.ImagePickerUtilities.ImagePickerActivity;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.PermissionUtilities.DevicePermissionUtils;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.projectreachout.GeneralStatic.getDomainUrl;

public class AddNewArticleFragment extends Fragment implements SingleUploadBroadcastReceiver.Delegate, View.OnClickListener {
    private DevicePermissionUtils mDevicePermissionUtils;
    private File mActualImage;
    private OnFragmentInteractionListener mListener;

    public static final String TAG = AddNewArticleFragment.class.getSimpleName();
    private ImageView mImageView;
    private EditText mDescriptionEditText;
    private Button mUploadImage;
    private Button mSubmitPost;

    private ProgressDialog mDialog;

    private SingleUploadBroadcastReceiver uploadReceiver;
    private int REQUEST_IMAGE = 100;

    public AddNewArticleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_new_post, container, false);
        mDevicePermissionUtils = new DevicePermissionUtils(getContext());
        mDevicePermissionUtils.handlePermissions();
        uploadReceiver = new SingleUploadBroadcastReceiver();
        if (mListener != null) {
            mListener.onFragmentInteraction(Uri.parse(getString(R.string.title_add_article)));
        }
        setUpUI(rootView);
        return rootView;
    }

    private void setUpUI(View rootView) {
        mImageView = rootView.findViewById(R.id.iv_fanp_uploaded_image);
        mUploadImage = rootView.findViewById(R.id.btn_fanp_upload_picture);
        mSubmitPost = rootView.findViewById(R.id.btn_fanp_upload_post);
        mDescriptionEditText = rootView.findViewById(R.id.et_fanp_description);

        mDialog = new ProgressDialog(getContext());

        mUploadImage.setOnClickListener(this);
        mSubmitPost.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            uploadReceiver.register(getContext());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
            uploadReceiver.unregister(getContext());
        }
    }

    @Override
    public void onProgress(int progress) {
        Log.v(TAG, String.valueOf(progress));
        mDialog.setMessage("Loading. Please wait...   " + progress + "%");
        mDialog.show();

    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        Log.v(TAG, "bytes: " + uploadedBytes + "\t\ttotal: " + totalBytes);
    }

    @Override
    public void onError(Exception exception) {
        //your implementation
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        mDialog.dismiss();
        Toast.makeText(getContext(), "Upload Completed", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
        Log.v(TAG, "serverResponseCode: " + serverResponseCode + "\tserverResponseBody" + Arrays.toString(serverResponseBody));
    }

    @Override
    public void onCancelled() {
        //your implementation
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fanp_upload_picture: {
                onChooseImageClicked();
                break;
            }
            case R.id.btn_fanp_upload_post: {
                upload();
                break;
            }
        }
    }

    private void onChooseImageClicked() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getContext(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Uri uri = data.getParcelableExtra("path");
                    mActualImage = FileUtil.from(getContext(), uri);
                    mImageView.setImageBitmap(BitmapFactory.decodeFile(mActualImage.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void upload() {
        String url = getDomainUrl() + "/add_article/";
        String description = mDescriptionEditText.getText().toString().trim();
        String time_stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        /*Map<String, String> map = new HashMap<>();
        map.put("user_name", AppController.getInstance().getLoginUserUsername());
        map.put("desc", description);*/

        String path = null;
        if (mActualImage != null) {
            ImageCompression imageCompression = new ImageCompression(getContext(), mActualImage);
            path = imageCompression.customCompressImage();
        }

        if (path != null) {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);
            try {
                new MultipartUploadRequest(Objects.requireNonNull(getContext()), uploadId, url)
                        .addHeader("Authorization", AppController.getInstance().getLoginCredential())
                        .addParameter("user_name", AppController.getInstance().getLoginUserUsername())
                        .addParameter("desc", description)
                        .addParameter("time_stamp", time_stamp)
                        .addFileToUpload(path, "image") //Adding file
                        //.setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                exc.printStackTrace();
                MessageUtils.showShortToast(getContext(), "Couldn't upload this picture...");
            }
        } else {
            MessageUtils.showShortToast(getContext(), "No picture selected");
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        startActivityForResult(intent, REQUEST_IMAGE);
    }
}