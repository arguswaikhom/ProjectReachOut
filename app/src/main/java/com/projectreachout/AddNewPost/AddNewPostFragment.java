package com.projectreachout.AddNewPost;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.projectreachout.AppController;
import com.projectreachout.ImageCompression.FileUtil;
import com.projectreachout.ImageCompression.ImageCompression;
import com.projectreachout.MessageManager.MessageManager;
import com.projectreachout.PermissionManager.DevicePermissionManager;
import com.projectreachout.R;
import com.projectreachout.SingleUploadBroadcastReceiver;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.ImagePicker.ImagePickerActivity.REQUEST_GALLERY_IMAGE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddNewPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddNewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AddNewPostFragment extends Fragment implements SingleUploadBroadcastReceiver.Delegate {
    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    private DevicePermissionManager mDevicePermissionManager;
    private MessageManager mMessageManager;

    private File mActualImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddNewPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewPostFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static AddNewPostFragment newInstance(String param1, String param2) {
        AddNewPostFragment fragment = new AddNewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static final String LOG_TAG = AddNewPostFragment.class.getSimpleName();
    public static final String TAG = AddNewPostFragment.class.getSimpleName();


    private ImageView mImageView;
    private EditText mDescriptionEditText;
    private Button mUploadImage;
    private Button mSubmitPost;

    private ProgressDialog mDialog;

    private SingleUploadBroadcastReceiver uploadReceiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_new_post, container, false);

        mDevicePermissionManager = new DevicePermissionManager(getContext());
        mMessageManager = new MessageManager(getContext());
        mDevicePermissionManager.handlePermissions();

        uploadReceiver = new SingleUploadBroadcastReceiver();

        if (mListener != null) {
            mListener.onFragmentInteraction(Uri.parse(getString(R.string.title_add_article)));
        }

        mImageView = rootView.findViewById(R.id.iv_fanp_uploaded_image);
        mUploadImage = rootView.findViewById(R.id.btn_fanp_upload_picture);
        mSubmitPost = rootView.findViewById(R.id.btn_fanp_upload_post);
        mDescriptionEditText = rootView.findViewById(R.id.et_fanp_description);

        /*TextView warningTV = rootView.findViewById(R.id.tv_wbl_notice);
        warningTV.setVisibility(View.VISIBLE);
        warningTV.setText(Html.fromHtml("<b>Warning:</b> \t" + getString(R.string.warning_no_image_compression)));*/

        mDialog = new ProgressDialog(getContext());

        mUploadImage.setOnClickListener(this::chooseImage);

        mSubmitPost.setOnClickListener(v -> upload());

        return rootView;
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

    private void chooseImage(View view) {
        if (!mDevicePermissionManager.hasAllPermissionsGranted()) {
            mMessageManager.showShortToast("Permission Required");
            mDevicePermissionManager.requestAllPermissions();
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                mActualImage = FileUtil.from(getContext(), data.getData());
                mImageView.setImageBitmap(BitmapFactory.decodeFile(mActualImage.getAbsolutePath()));
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
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
                Toast.makeText(getContext(), "Couldn't upload this picture...", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No picture selected", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
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

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
